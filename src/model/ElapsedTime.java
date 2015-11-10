package model;

import java.util.concurrent.BlockingQueue;

//Embed within soundplayer
public class ElapsedTime {
	private long start;
	private long pausedLength;
	private long pauseEnd;
	private boolean isPaused = true;
	private BlockingQueue<String> timeQueue;
	
	public ElapsedTime(BlockingQueue<String> timeQueue) {
		this.timeQueue = timeQueue;
	}
	
	public synchronized void start() {
		if (isPaused) {
			start = System.nanoTime() - pausedLength;
		}
		else {
			start = System.nanoTime();
		}
		
	}
	
	public synchronized void pause() {
		//store how long was played
		pausedLength = System.nanoTime() - start;
		isPaused = true;
	}
	
	public synchronized long getTimestamp() {
		return System.nanoTime() - start;
	}
	
}
