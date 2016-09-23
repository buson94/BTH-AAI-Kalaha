package ai;

import kalaha.GameState;

public class Node 
{
	private int utilityValue;
	private GameState board;
	private Node[] nextNodes;
	
	public Node(GameState newBoard)
	{
		setValue(0);
		board = newBoard.clone();
		if(!board.gameEnded())
		{
			nextNodes = new Node[6];
			for(Node n : nextNodes)
				n = new Node(board.clone());
		}
	}
	
	public void setValue(int value)
	{
		utilityValue = value;
	}
	
	public int getValue()
	{
		return utilityValue;
	}
	
	public void setBoard(GameState newBoard)
	{
		board = newBoard;
	}
	
	public GameState getBoard()
	{
		return board.clone();
	}
	
	public Node[] getNextNodes()
	{
		return nextNodes;
	}
	
	/**
	 * Method that simply adds a value to the utilityValue of the node.
	 */
	public void addToValue(int value)
	{
		utilityValue += value;
	}
}
