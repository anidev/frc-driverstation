package org.anidev.frcds.pc;

import org.anidev.frcds.common.DriverStation;
import org.anidev.frcds.pc.gui.DriverStationFrame;
import org.anidev.frcds.pc.input.InputEnvironment;

public class PCDriverStation extends DriverStation {
	private Thread inputThread=null;
	private DriverStationFrame frame=null;
	private final InputEnvironment inputEnv=new InputEnvironment();

	public PCDriverStation() {
		this.startLoops();
		inputThread=new Thread(new InputLoop(),"Input Loop");
		inputThread.start();
	}

	public void setFrame(DriverStationFrame frame) {
		this.frame=frame;
		setElapsedTimeImpl();
		setTeamIDImpl();
		setBatteryPercentImpl();
		setModeImpl();
	}

	public DriverStationFrame getFrame() {
		return frame;
	}

	public InputEnvironment getInputEnvironment() {
		return inputEnv;
	}

	@Override
	protected void setEnabledImpl() {
	}

	@Override
	protected void setElapsedTimeImpl() {
		if(frame==null) {
			return;
		}
		frame.setElapsedTime(elapsedTime);
	}

	@Override
	protected void setTeamIDImpl() {
		if(frame==null) {
			return;
		}
		frame.setTeamID(teamID);
	}

	@Override
	protected void setBatteryPercentImpl() {
		if(frame==null) {
			return;
		}
		frame.setBatteryPercent(batteryPercent);
	}

	@Override
	protected void setModeImpl() {
	}

	@Override
	protected void setLastRobotControlImpl() {
		if(frame==null) {
			return;
		}
		frame.displayControlData(lastRobotControl);
	}

	private class InputLoop implements Runnable {
		public static final int SLEEP_MS=5000;
		@Override
		public void run() {
			while(!Thread.interrupted()) {
				try {
					Thread.sleep(SLEEP_MS);
				} catch(InterruptedException e) {
					break;
				}
				inputEnv.updateControllers();
			}
		}
	}
}
