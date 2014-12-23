package org.anidev.frcds.pc;

import org.anidev.frcds.common.DriverStation;
import org.anidev.frcds.pc.gui.DriverStationFrame;
import org.anidev.frcds.pc.input.InputEnvironment;

/**
 * DriverStation running on a PC
 */
public class PCDriverStation extends DriverStation {
	private Thread inputThread=null;
	private DriverStationFrame frame=null;
	private final InputEnvironment inputEnv=new InputEnvironment();

	/**
	 * Starts the loops and input thread
	 */
	public PCDriverStation() {
		this.startLoops();
		inputThread=new Thread(new InputLoop(),"Input Loop");
		inputThread.start();
	}

	/**
	 * @param frame the frame to set
	 */
	public void setFrame(DriverStationFrame frame) {
		this.frame=frame;
		setElapsedTimeImpl();
		setTeamIDImpl();
		setBatteryPercentImpl();
		setModeImpl();
	}

	/**
	 * @return the DriverStationFrame
	 */
	public DriverStationFrame getFrame() {
		return frame;
	}

	/**
	 * @return the InputEnvironment
	 */
	public InputEnvironment getInputEnvironment() {
		return inputEnv;
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.common.DriverStation#setEnabledImpl()
	 */
	@Override
	protected void setEnabledImpl() {
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.common.DriverStation#setElapsedTimeImpl()
	 */
	@Override
	protected void setElapsedTimeImpl() {
		if(frame==null) {
			return;
		}
		frame.setElapsedTime(elapsedTime);
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.common.DriverStation#setTeamIDImpl()
	 */
	@Override
	protected void setTeamIDImpl() {
		if(frame==null) {
			return;
		}
		frame.setTeamID(teamID);
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.common.DriverStation#setBatteryPercentImpl()
	 */
	@Override
	protected void setBatteryPercentImpl() {
		if(frame==null) {
			return;
		}
		frame.setBatteryPercent(batteryPercent);
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.common.DriverStation#setModeImpl()
	 */
	@Override
	protected void setModeImpl() {
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.common.DriverStation#setLastRobotControlImpl()
	 */
	@Override
	protected void setLastRobotControlImpl() {
		if(frame==null) {
			return;
		}
		frame.displayControlData(lastRobotControl);
	}

	/**
	 * Update the input in a loop for use with threading
	 */
	private class InputLoop implements Runnable {
		public static final int SLEEP_MS=5000;
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
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
