package ai;

import java.util.ArrayList;

import kalaha.GameState;

public class Node 
{
	private int player;
	private int utilityValue = 0;
	private GameState board;
	private ArrayList<Node> nextNodes = new ArrayList<Node>(6);
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
	
	public int visit(int deepeningLvl, IterationStop iterationStop, PruningManager pruningManager)
	{
		if(iterationStop.stop(deepeningLvl)) {
            return calculateBoardUtilityValue();
        }
        
        if (!hasBeenExpanded)
		{
            resetUtilityValue();
			for(int i = 6; i >= 1; i--)
			{
				if(board.moveIsPossible(i))
				{
					Node nextNode = new Node(board, i, player);
					nextNodes.add(nextNode);
                    int value = nextNode.visit(deepeningLvl + 1, iterationStop, pruningManager);
                    updateUtilityValue(value, nextNode.getMadeMove());
                    if (pruningManager.pruneBranch(value, isMaxNode()))
                        break;
				}
			}
            hasBeenExpanded = true;
            return utilityValue;
		}
        else if (nextNodes.size() > 0) {
            resetUtilityValue();
            for(Node n : nextNodes) {
				int value = n.visit(deepeningLvl + 1, iterationStop, pruningManager);
                updateUtilityValue(value, n.getMadeMove());
            }
            return utilityValue;
        }
        else {
            return calculateBoardUtilityValue();
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
        utilityValue = ownScore - enemyScore;
		return utilityValue;
	}
    
    private boolean isMaxNode() {
        return board.getNextPlayer() == player;
    }
}
