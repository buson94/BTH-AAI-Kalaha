package ai;

import ai.Global;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import kalaha.*;

/**
 * This is the class where the AI bot is defined.
 * It uses the Minimax with iterative deepening.
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
    	Node initialNode = new Node(currentBoard, -1, player);
    	IterationStop iterationStop = new IterationStop((long) (4*Math.pow(10, 9)));
    	
    	int maxDeepeningLvl = 1;
    	while(!iterationStop.stop(-1))
    	{
    		iterationStop.setMaxDeepeningLvl(maxDeepeningLvl);
    		initialNode.visit(0, iterationStop);
    		maxDeepeningLvl++;
    	}
    	System.out.println("After: " + maxDeepeningLvl);
    	return initialNode.getBestMove();
    }
    
    /*public void thinkMoves(Node node)
    {
    	node.createNextNodes();
		Node[] pNodes = node.getNextNodes();
		
    	// Simulating every possible move...
    	for(int pAmbo = 0; pAmbo < 6; pAmbo++)
    	{
    		GameState simBoard = pNodes[pAmbo].getBoard();	//	Here it gets an exception?
    		if(simBoard.moveIsPossible(pAmbo))
    		{
    			simBoard.makeMove(pAmbo+1);
    			pNodes[pAmbo].setBoard(simBoard.clone());
    		}
    	}
    	// ...and then setting the utility value of a node to the difference in score
    	for(int i = 0; i < pNodes.length; i++)
    	{
    		GameState endBoard = pNodes[i].getBoard();
    		int ownScore = 0, enemyScore = 0;
    		for(int a = 0; a < 6; a++)
    		{
    			if(player == 1)
    			{
	    			ownScore += endBoard.getScore(1);
	    			enemyScore += endBoard.getScore(2);
    			}
    			else
    			{
	    			ownScore += endBoard.getScore(2);
	    			enemyScore += endBoard.getScore(1);
    			}
    		}
    		int difference = ownScore - enemyScore;
    		pNodes[i].setValue(difference);
    	}
		// If the game didn't end here, simulate next possible moves.
		for(Node n : pNodes)
			thinkMoves(n);
    }*/
    
    /**
     * A method to get the index of a minimum or maximum of an array.
     * @param max If this boolean is true, the method looks for the maximum and if it's false, it looks for the minimum. 
     * @param utility Is the array with numbers of possible wins in it 
     * @return Index of the highest/lowest value 
     */
    /*public int minimaximize(boolean max)
    {
    	int indexValue = 0, value = max? -1000 : 1000;
    	for(int index = 0; index < utility.length; index++)
    	{
    		if(max && utility[index] > value)
	    	{
    			value = utility[index];
    			indexValue = index;
    		}
    		if(!max && utility[index] < value)
    		{
    			value = utility[index];
    			indexValue = index;
    		}
    	}
    	return indexValue + 1;
    }*/
    
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