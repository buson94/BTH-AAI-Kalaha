package ai;

import java.util.ArrayList;

import kalaha.GameState;

public class Node 
{
	private int player;
	private int utilityValue = 0;
	private GameState board;
	private ArrayList<Node> nextNodes = new ArrayList<Node>(6);
	private boolean isMaxNode;
	private int bestMove = -1;
	private int madeMove;
	private boolean hasBeenExpanded = false;
	
	public Node(GameState currentBoard, int moveToMake, int player)
	{
		board = currentBoard.clone();
		if(moveToMake > 0 && moveToMake <= 6)
			board.makeMove(moveToMake);
		
		this.madeMove = moveToMake;
		this.player = player;
	}
	
	public int getUtilityValue()
	{
		return utilityValue;
	}
	
	public int getBestMove()
	{
		return bestMove;
	}
	
	public int getMadeMove()
	{
		return madeMove;
	}
	
	public int visit(int deepeningLvl, IterationStop iterationStop)
	{
		if(!iterationStop.stop(deepeningLvl))
		{
			expand();
			for(Node n : nextNodes)
				n.visit(deepeningLvl + 1, iterationStop);
		}
		
		calculateUtilityValue();
		return utilityValue;
	}
	
	private void calculateUtilityValue() 
	{
		// Reset Utility Value
		if(board.getNextPlayer() == player)
			utilityValue = -100000;
		else
			utilityValue = 100000;
		
		// If no child nodes exist then calculate the utility value
		if(nextNodes.size() == 0) 
		{
			utilityValue = calculateBoardUtilityValue();
		}
		else
		{
			for(Node n : nextNodes)
			{
				if(board.getNextPlayer() == player)
				{
					if(n.getUtilityValue() > utilityValue || 
					   n.getUtilityValue() == utilityValue && Math.random() < 0.5)
					{
						utilityValue = n.getUtilityValue();
						bestMove = n.getMadeMove();
					}
				}
				else 
				{
					if(n.getUtilityValue() < utilityValue || 
					   n.getUtilityValue() == utilityValue && Math.random() < 0.5)
					{
						utilityValue = n.getUtilityValue();
						bestMove = n.getMadeMove();
					}
				}
			}
		}
	}

	private void expand() 
	{
		if(!hasBeenExpanded)
		{
			for(int i = 1; i < 7; i++)
			{
				if(board.moveIsPossible(i))
				{
					Node nextNode = new Node(board, i, player);
					nextNodes.add(nextNode);
				}
			}
		}
		
		hasBeenExpanded = true;
	}

	private int calculateBoardUtilityValue()
	{
		int ownScore = 0, enemyScore = 0;
		if(player == 1)
		{
			ownScore += board.getScore(1);
			enemyScore += board.getScore(2);
		}
		else
		{
			ownScore += board.getScore(2);
			enemyScore += board.getScore(1);
		}
		int difference = ownScore - enemyScore;
		return difference;
	}
}
