package ai;

public class IterationManager 
{
	private long startTime;
	private long maxRunningTime;
	private int maxDeepeningLvl;
	
	public IterationManager(long maxRunningTime)
	{
		this.maxRunningTime = maxRunningTime;
		startTime = System.nanoTime();
	}
	
	public boolean depthReached(int currentDeepeningLvl)
	{
		return currentDeepeningLvl >= maxDeepeningLvl;
	}
    
    public boolean timeOver() 
    {
        long diff = System.nanoTime() - startTime;
        boolean timeOver = diff > maxRunningTime;
        return timeOver;   
    }
	
	public void setMaxDeepeningLvl(int lvl)
	{
		maxDeepeningLvl = lvl;
	}
}
