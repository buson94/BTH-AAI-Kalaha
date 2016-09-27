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
	
	public void createNextNodes()
	{
		nextNodes = new Node[6];
		for(Node n : nextNodes)
			n = new Node(board.clone());
	}
}
