package org.anidev.utils;

/**
 * Used to control the execution rate of a loop. Given a frequency in hertz, it
 * will sleep for as long as necessary to maintain that rate.
 */
public class LoopControl {
	private double hertz;
	private double delay;
	private long lastTime=0;

	public LoopControl(double hertz) {
		setHertz(hertz);
	}

	public void startLoop() {
		lastTime=System.currentTimeMillis();
	}

	public void loopWait() throws InterruptedException {
		long elapsed=System.currentTimeMillis()-lastTime;
		if(elapsed<delay) {
			Thread.sleep((long)(delay-elapsed));
		}
		lastTime=System.currentTimeMillis();
	}

	public double getHertz() {
		return hertz;
	}

	public void setHertz(double hertz) {
		this.hertz=hertz;
		delay=1000.0/hertz;
	}

}
