package ai;

import java.util.ArrayList;
import java.util.Iterator;

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
	
	public int visit(int deepeningLvl, IterationManager iterationManager, int alpha, int beta)
	{
		if(iterationManager.depthReached(deepeningLvl)) {
            return calculateBoardUtilityValue();
        }
        boolean gameEnded = true;
        for(int moveIndex = 6; moveIndex >= 1; moveIndex--)
        {
            if (iterationManager.timeOver()) break;
            if(board.moveIsPossible(moveIndex))
            {
                gameEnded = false;
                GameState nextBoard = board.clone();
                nextBoard.makeMove(moveIndex);
                Node nextNode = new Node(nextBoard, player);
                int value = nextNode.visit(deepeningLvl + 1, iterationManager, alpha, beta);
                updateUtilityValue(value, moveIndex);
                if (isMaxNode()) {
                    if (value > beta) break;
                    alpha = Math.max(value, alpha);
                }
                else {
                    if (value < alpha) break;
                    beta = Math.min(value, beta);
                }
            }
        }
        if (gameEnded) {
            return calculateBoardUtilityValue();
        } else {
            return utilityValue;
        }
	}
    
    private void resetUtilityValue () {
        // Reset Utility Value
		if(isMaxNode())
			utilityValue = Integer.MIN_VALUE;
		else
			utilityValue = Integer.MAX_VALUE;
    }
	
	private void updateUtilityValue(int nextNodeUtilityValue, int move) {
        if(isMaxNode())
        {
            if(nextNodeUtilityValue > utilityValue 
             //  || nextNodeUtilityValue == utilityValue && Math.random() < 0.5
               )
            {
                utilityValue = nextNodeUtilityValue;
                bestMove = move;
            }
        }
        else 
        {
            if(nextNodeUtilityValue < utilityValue 
             //  || nextNodeUtilityValue == utilityValue && Math.random() < 0.5
               )
            {
                utilityValue = nextNodeUtilityValue;
                bestMove = move;
            }
        }
	}
    
	private int calculateBoardUtilityValue()
	{   
		int ownScore = 0, enemyScore = 0;
        ownScore += board.getScore(player);
        enemyScore += board.getScore((player % 2) + 1);
        utilityValue = ownScore - enemyScore;
		return utilityValue;
	}
    
    private boolean isMaxNode() {
        return board.getNextPlayer() == player;
    }
}
