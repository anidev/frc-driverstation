package org.anidev.frcds.common;

import org.anidev.utils.LoopControl;

public class MainLoop implements Runnable {
	private final DriverStation ds;
	private LoopControl loop;
	private long startTime=System.currentTimeMillis();

	public MainLoop(DriverStation ds,double hertz) {
		this.ds=ds;
		loop=new LoopControl(hertz);
	}

	@Override
	public void run() {
		loop.startLoop();
		while(!Thread.interrupted()) {
			doBattery();
			ds.doMainLoopImpl();
			doEnabled();
			try {
				loop.loopWait();
			} catch(InterruptedException e) {
				break;
			}
		}
	}

	private void doBattery() {
		ds.refreshBattery();
	}

	private void doEnabled() {
		doElapsedTime();
		ds.doEnabledLoopImpl();
		doSendData();
	}

	private void doElapsedTime() {
		long elapsedTime=System.currentTimeMillis()-startTime;
		ds.setElapsedTime(elapsedTime);
	}

	private void doSendData() {
		ds.sendControlData();
	}
}
