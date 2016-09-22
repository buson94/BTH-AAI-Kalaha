package ai;

public class Node 
{
	private int utilityValue;
	
	public Node()
	{
		setValue(0);
	}
	
	public Node(int value)
	{
		setValue(value);
	}
	
	public void setValue(int value)
	{
		utilityValue = value;
	}
	
	public int getValue()
	{
		return utilityValue;
	}
	
	/**
	 * Method that simply adds a value to the utilityValue of the node.
	 */
	public void addToValue(int value)
	{
		utilityValue += value;
	}
}
