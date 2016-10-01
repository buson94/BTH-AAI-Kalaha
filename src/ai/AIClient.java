package ai;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import kalaha.*;

/**
 * This is the class where the AI bot is defined.
 * It uses the Minimax with iterative deepening and alpha-beta pruning
 * 
 * @author Marvin Uwe Marken
 */
public class AIClient implements Runnable
{
    private int player;
    private JTextArea text;
    
    private PrintWriter out;
    private BufferedReader in;
    private Thread thr;
    private Socket socket;
    private boolean running;
    private boolean connected;
    private int moveCount;
    	
    /**
     * Creates a new client.
     */
    public AIClient()
    {    	
    	player = -1;
        connected = false;
        
        //This is some necessary client stuff. You don't need
        //to change anything here.
        initGUI();
	
        try
        {
            addText("Connecting to localhost:" + KalahaMain.port);
            socket = new Socket("localhost", KalahaMain.port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            addText("Done");
            connected = true;
        }
        catch (Exception ex)
        {
            addText("Unable to connect to server");
            return;
        }
    }
    
    /**
     * Starts the client thread.
     */
    public void start()
    {
        //Don't change this
        if (connected)
        {
            thr = new Thread(this);
            thr.start();
        }
    }
    
    /**
     * Creates the GUI.
     */
    private void initGUI()
    {
        //Client GUI stuff. You don't need to change this.
        JFrame frame = new JFrame("My AI Client");
        frame.setLocation(Global.getClientXpos(), 445);
        frame.setSize(new Dimension(420,250));
        frame.getContentPane().setLayout(new FlowLayout());
        
        text = new JTextArea();
        JScrollPane pane = new JScrollPane(text);
        pane.setPreferredSize(new Dimension(400, 210));
        
        frame.getContentPane().add(pane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setVisible(true);
    }
    
    /**
     * Adds a text string to the GUI textarea.
     * 
     * @param txt The text to add
     */
    public void addText(String txt)
    {
        //Don't change this
        text.append(txt + "\n");
        text.setCaretPosition(text.getDocument().getLength());
    }
    
    /**
     * Thread for server communication. Checks when it is this
     * client's turn to make a move.
     */
    public void run()
    {
        String reply;
        running = true;
        
        try
        {
            while (running)
            {
                //Checks which player you are. No need to change this.
                if (player == -1)
                {
                    out.println(Commands.HELLO);
                    reply = in.readLine();

                    String tokens[] = reply.split(" ");
                    player = Integer.parseInt(tokens[1]);
                    
                    addText("I am player " + player);
                }
                
                //Check if game has ended. No need to change this.
                out.println(Commands.WINNER);
                reply = in.readLine();
                if(reply.equals("1") || reply.equals("2") )
                {
                    int w = Integer.parseInt(reply);
                    if (w == player)
                    {
                        addText("I won!");
                    }
                    else
                    {
                        addText("I lost...");
                    }
                    running = false;
                }
                if(reply.equals("0"))
                {
                    addText("Even game!");
                    running = false;
                }

                //Check if it is my turn. If so, do a move
                out.println(Commands.NEXT_PLAYER);
                reply = in.readLine();
                if (!reply.equals(Errors.GAME_NOT_FULL) && running)
                {
                    int nextPlayer = Integer.parseInt(reply);

                    if(nextPlayer == player)
                    {
                        out.println(Commands.BOARD);
                        String currentBoardStr = in.readLine();
                        boolean validMove = false;
                        while (!validMove)
                        {
                            long startT = System.currentTimeMillis();
                            //This is the call to the function for making a move.
                            //You only need to change the contents in the getMove()
                            //function.
                            GameState currentBoard = new GameState(currentBoardStr);
                            int cMove = getMove(currentBoard);
                            
                            //Timer stuff
                            long tot = System.currentTimeMillis() - startT;
                            double e = (double)tot / (double)1000;
                            
                            out.println(Commands.MOVE + " " + cMove + " " + player);
                            reply = in.readLine();
                            if (!reply.startsWith("ERROR"))
                            {
                                validMove = true;
                                addText("Made move " + cMove + " in " + e + " secs");
                            }
                        }
                    }
                }
                
                //Wait
                Thread.sleep(100);
            }
        }
        catch (Exception ex)
        {
            running = false;
        }
        
        try
        {
            socket.close();
            addText("Disconnected from server");
        }
        catch (Exception ex)
        {
            addText("Error closing connection: " + ex.getMessage());
        }
    }
    
    /**
     * This is the method that makes a move each time it is the bots turn.
     * 
     * @param currentBoard The current board state
     * @return Move to make (1-6)
     */
    public int getMove(GameState currentBoard)
    {
        moveCount++;
        // Saving the start time for later to see how long a "thought" took
        long startTime = System.nanoTime();
        // Initialize the iteration manager with a maximum time of 5 seconds 
    	IterationManager iterationStop = new IterationManager((long) (5*Math.pow(10, 9)));
    	
    	// Starting with a deepening maximum of 2, a best value of "minus infinite" and a best move of 0 (No best move yet) 
    	int maxDeepeningLvl = 2;
        int bestValue = -Integer.MIN_VALUE;
        int bestMove = 0;
        // Now iterate through possible moves as long as the timer or the deepening level doesn't exceed its maximum
    	while(!iterationStop.timeOver() && maxDeepeningLvl < 16)
    	{
    		iterationStop.setMaxDeepeningLvl(maxDeepeningLvl);
            
    		// Initialize the root node of the game tree with the current board and the player number
            Node root = new Node(currentBoard, player);
            // Calculate the utility value with start deepening level of 0, the iteration manager, current best value as alpha and the "plus infinite" as beta
    		int value = root.visit(0, iterationStop, bestValue, Integer.MAX_VALUE);
    		// Calculate the time that passed since the start of the AIs turn
            long diffTime = (System.nanoTime() - startTime) / (1000 * 1000);
            // Check if the calculated value is greater than the current best value. If yes then set it to the best value and best move
            if (value > bestValue) 
            {
                bestValue = value;
                bestMove = root.getBestMove();
                // Debugging text to see when the AI thought which move was the best move for its current turn, 
                // after which deepness, the possible score and how much time has passed
                //addText(moveCount + ". Better Move: " + bestMove + " in Depth: " + maxDeepeningLvl + " and Score: " + bestValue + " after " + diffTime + "ms");
            }
            // Increase the max deepening level and repeat again
    		maxDeepeningLvl++;
    	}
    	return bestMove;
    }
    
    /**
     * Returns a random ambo number (1-6) used when making
     * a random move.
     * 
     * @return Random ambo number
     */
    public int getRandom()
    {
        return 1 + (int)(Math.random() * 6);
    }
}