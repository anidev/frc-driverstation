package org.anidev.frcds.common;

public class EnabledLoop implements Runnable {
	private final DriverStation ds; 
	private long startTime=System.currentTimeMillis();
	private double hertz;
	private double delayMs;
	public EnabledLoop(DriverStation ds,double hertz) {
		this.ds=ds;
		this.hertz=hertz;
		this.delayMs=1000.0/this.hertz;
	}
	@Override
	public void run() {
		while(!Thread.interrupted()) {
			doElapsedTime();
			ds.doEnabledLoopImpl();
			doSendData();
			try {
				Thread.sleep((long)delayMs);
			} catch(InterruptedException e) {
				break;
			}
		}
	}
	private void doElapsedTime() {
		long elapsedTime=System.currentTimeMillis()-startTime;
		ds.setElapsedTime(elapsedTime);
	}
	private void doSendData() {
		ds.sendControlData();
	}
}
