package ai;

public class IterationStop 
{
	private long startTime;
	private long maxRunningTime;
	private int maxDeepeningLvl;
	
	public IterationStop(long maxRunningTime)
	{
		this.maxRunningTime = maxRunningTime;
		startTime = System.nanoTime();
	}
	
	public boolean stop(int currentDeepeningLvl)
	{
		boolean timeOver = System.nanoTime() - startTime > maxRunningTime;
		boolean depthReached = currentDeepeningLvl > maxDeepeningLvl;
		
		return timeOver || depthReached;
	}
	
	public void setMaxDeepeningLvl(int l)
	{
		maxDeepeningLvl = l;
	}
}
