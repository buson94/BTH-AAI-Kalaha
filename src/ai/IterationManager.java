package ai;

public class IterationManager 
{
	private long startTime;
	private long maxRunningTime;
	private int maxDeepeningLvl;
    public long lastTime;
	
	public IterationManager(long maxRunningTime)
	{
		this.maxRunningTime = maxRunningTime;
		startTime = System.nanoTime();
	}
    
    public boolean stop(int currentDepth) {
        return depthReached(currentDepth) || timeOver();
    }
	
	public boolean depthReached(int currentDeepeningLvl)
	{
		return currentDeepeningLvl >= maxDeepeningLvl;
	}
    
    public boolean timeOver() {
        
        long diff = System.nanoTime() - startTime;
        boolean timeOver = diff > maxRunningTime;
        if (!timeOver)
            lastTime = diff;
        return timeOver;   
    }
	
	public void setMaxDeepeningLvl(int lvl)
	{
		maxDeepeningLvl = lvl;
	}
}
