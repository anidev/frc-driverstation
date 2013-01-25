package org.anidev.frcds.common;

public class CommonLoop implements Runnable {
	private final DriverStation ds;
	private double hertz;
	private double delayMs;
	public CommonLoop(DriverStation ds,double hertz) {
		this.ds=ds;
		this.hertz=hertz;
		this.delayMs=1000.0/this.hertz;
	}
	@Override
	public void run() {
		while(!Thread.interrupted()) {
			doBattery();
			try {
				Thread.sleep((long)delayMs);
			} catch(InterruptedException e) {
				break;
			}
		}
	}
	private void doBattery() {
		ds.refreshBattery();
	}
}
