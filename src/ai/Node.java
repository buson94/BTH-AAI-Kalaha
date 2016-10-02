package ai;

import kalaha.GameState;

public class Node 
{
	private final int player;
	private final GameState board;
	private int utilityValue = 0;
	private int bestMove = -1;
	
	public Node(GameState currentBoard, int player)
	{
		board = currentBoard;
		this.player = player;
        resetUtilityValue();
	}
	
	public int getUtilityValue()
	{
		return utilityValue;
	}
	
	public int getBestMove()
	{
		return bestMove;
	}
	
	/**
	 * Method that visits the next node.
	 * 
	 * @param deepeningLvl is the value to see if an maximum of deepness is reached
	 * @param iterationManager is a manager that checks if a maximum in time needed or deepness is reached
	 * @param alpha is the alpha value of Alpha-Beta-Pruning (Best value for the maximizer)
	 * @param beta is the beta value of Alpha-Beta-Pruning (Best value for the minimizer)
	 * @return the utility value of the currently visited node
	 */
	public int visit(int deepeningLvl, IterationManager iterationManager, int alpha, int beta)
	{
		// First check if the iteration exceeded the maximum deepness. 
    	// If yes the directly return the current utility value
		if(iterationManager.depthReached(deepeningLvl)) 
			return calculateBoardUtilityValue();
		
        boolean isUtilityValueAssigned = false;
        // Go through all ambos
        for(int moveIndex = 6; moveIndex >= 1; moveIndex--)
        {
        	// If time maximum is exceeded then stop here
            if(iterationManager.timeOver()) break;
            
            // Check if chosen ambo/move is possible
            if(board.moveIsPossible(moveIndex))
            {
            	// Create a copy of the board and simulate the move on it
                GameState nextBoard = board.clone();
                nextBoard.makeMove(moveIndex);
                
                // Create the next node with the simulated board and the player
                Node nextNode = new Node(nextBoard, player);
                int value = nextNode.visit(deepeningLvl + 1, iterationManager, alpha, beta);
                updateUtilityValue(value, moveIndex);
                
                // Update alpha and beta values depending on max or min node
                if (isMaxNode()) 
                {
                    if (value > beta) break;
                    alpha = Math.max(value, alpha);
                }
                else 
                {
                    if (value < alpha) break;
                    beta = Math.min(value, beta);
                }
                isUtilityValueAssigned = true;
            }
        }
        
        // If the utility value still hasn't been assigned return it here
        if (!isUtilityValueAssigned) 
        {
            return calculateBoardUtilityValue();
        } 
        else 
        {
            return utilityValue;
        }
	}
    
    private void resetUtilityValue () 
    {
		if(isMaxNode())
			utilityValue = Integer.MIN_VALUE;
		else
			utilityValue = Integer.MAX_VALUE;
    }
	
    /**
     * A method that updates the utility value and best move if it's better(if maximizer) or worse(if minimizer)
     * 
     * @param nextNodeUtilityValue is the value of the next visited node
     * @param move is the move that is made to get to this node/value
     */
	private void updateUtilityValue(int nextNodeUtilityValue, int move) 
	{
        if(isMaxNode())
        {
            if(nextNodeUtilityValue > utilityValue)
            {
                utilityValue = nextNodeUtilityValue;
                bestMove = move;
            }
        } 
        else 
        {
            if(nextNodeUtilityValue < utilityValue)
            {
                utilityValue = nextNodeUtilityValue;
                bestMove = move;
            }
        }
	}
    
	/**
	 * Calculates the utility value of the current board out of the players score subtracted with the enemys score
	 * @return the utility value
	 */
	private int calculateBoardUtilityValue()
	{
		// Get the score points of both players
		int ownScore = board.getScore(player);
        int enemyScore = board.getScore((player % 2) + 1);
        
        // To get the utility value, we easily subtract the enemys score from the current players
        utilityValue = ownScore - enemyScore;
        
		return utilityValue;
	}
    
	/**
	 * A method to check if the next node is a maximizer or a minimizer node
	 * @return true if the next player is the original player of this tree
	 */
    private boolean isMaxNode() 
    {
        return board.getNextPlayer() == player;
    }
}
