package org.anidev.frcds.common;

import org.anidev.frcds.common.types.BatteryProvider;
import org.anidev.frcds.common.types.OperationMode;
import org.anidev.frcds.proto.ControlFlags;
import org.anidev.frcds.proto.FRCCommunication;
import org.anidev.frcds.proto.torobot.FRCCommonControl;

public abstract class DriverStation {
	public static final double UPDATE_HERTZ=50.0;
	public static final double SLOW_HERTZ=1.0;
	private FRCCommunication frcComm=new FRCCommunication();
	protected FRCCommonControl dsControl=new FRCCommonControl();
	protected BatteryProvider batteryProvider=null;
	protected Thread enabledLoop=null;
	protected Thread commonLoop=null;
	protected OperationMode mode=OperationMode.TELEOPERATED;
	protected boolean enabled=false;
	protected double elapsedTime=0.0;
	protected double batteryPercent=-1.0;
	protected int teamID=0;

	protected void setEnabledImpl() {
	}

	protected void setElapsedTimeImpl() {
	}
	
	protected void setTeamIDImpl() {
	}
	
	protected void setBatteryPercentImpl() {
	}
	
	protected void setModeImpl() {
	}
	
	protected void doCommonLoop() {
	}
	
	protected void doEnabledLoop() {
	}
	
	protected DriverStation() {
		commonLoop=new Thread(new CommonLoop(this,SLOW_HERTZ));
		commonLoop.start();
	}

	public void setBatteryProvider(BatteryProvider batteryProvider) {
		this.batteryProvider=batteryProvider;
	}
	
	public BatteryProvider getBatteryProvider() {
		return batteryProvider;
	}

	public void sendControlData() {
		dsControl.setPacketIndex(dsControl.getPacketIndex()+1);
		frcComm.sendToRobot(dsControl);
	}

	public OperationMode getMode() {
		return mode;
	}

	public void setMode(OperationMode mode) {
		this.mode=mode;
		ControlFlags flags=dsControl.getControlFlags();
		flags.setAutonomous(OperationMode.AUTONOMOUS.equals(mode));
		flags.setTest(OperationMode.TEST.equals(mode));
		dsControl.setControlFlags(flags);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled=enabled;
		dsControl.getControlFlags().setEnabled(enabled);
		if(enabled) {
			if(enabledLoop!=null&&enabledLoop.isAlive()) {
				return;
			}
			enabledLoop=new Thread(new EnabledLoop(this,UPDATE_HERTZ));
			enabledLoop.start();
		} else {
			if(enabledLoop!=null) {
				enabledLoop.interrupt();
			}
			enabledLoop=null;
			setElapsedTime(0.0);
		}
	}

	public double getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(double elapsedTime) {
		this.elapsedTime=elapsedTime;
		setElapsedTimeImpl();
	}
	
	public int getTeamID() {
		return teamID;
	}
	
	public void setTeamID(int teamID) {
		this.teamID=teamID;
		dsControl.setTeamID(teamID);
		frcComm.setTeamID(teamID);
		setTeamIDImpl();
	}
	
	public void refreshBattery() {
		if(batteryProvider==null) {
			batteryPercent=-1.0;
		} else {
			batteryPercent=batteryProvider.getBatteryPercent();
		}
		setBatteryPercentImpl();
	}
}
