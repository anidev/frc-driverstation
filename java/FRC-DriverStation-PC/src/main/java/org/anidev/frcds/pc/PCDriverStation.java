package org.anidev.frcds.pc;

import org.anidev.frcds.common.DriverStation;
import org.anidev.frcds.pc.gui.DriverStationFrame;
import org.anidev.frcds.pc.input.InputEnvironment;

public class PCDriverStation extends DriverStation {
	private DriverStationFrame frame=null;
	private InputEnvironment inputEnv=new InputEnvironment();

	public PCDriverStation() {
		this.startLoops();
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

	@Override
	protected void doCommonLoopImpl() {
		inputEnv.updateControllers();
	}
}
