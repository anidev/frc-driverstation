package org.anidev.frcds.common;

import org.anidev.frcds.common.types.BatteryProvider;
import org.anidev.frcds.proto.CommData;
import org.anidev.frcds.proto.ControlFlags;
import org.anidev.frcds.proto.FRCCommunication;
import org.anidev.frcds.proto.FRCCommunicationListener;
import org.anidev.frcds.proto.tods.FRCRobotControl;
import org.anidev.frcds.proto.torobot.FRCCommonControl;
import org.anidev.frcds.proto.torobot.OperationMode;

public abstract class DriverStation {
	public static final double LOOP_FREQ=50.0;
	protected FRCCommunication frcComm=new FRCCommunication();
	protected FRCCommonControl dsControl=new FRCCommonControl();
	protected FRCRobotControl lastRobotControl=null;
	protected BatteryProvider batteryProvider=null;
	protected Thread mainLoop=null;
	protected OperationMode mode=OperationMode.TELEOPERATED;
	protected boolean enabled=false;
	protected double elapsedTime=0.0;
	protected double batteryPercent=-1.0;
	protected int teamID=0;

	protected DriverStation() {
		frcComm.addRobotDataListener(new FRCCommunicationListener() {
			@Override
			public void receivedData(CommData data) {
				if(!(data instanceof FRCRobotControl)) {
					return;
				}
				FRCRobotControl robotControl=(FRCRobotControl)data;
				setLastRobotControl(robotControl);
			}
		});
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
		flags.setOperationMode(mode);
		dsControl.setControlFlags(flags);
		setModeImpl();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled=enabled;
		dsControl.getControlFlags().setEnabled(enabled);
		if(!enabled) {
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
		this.teamID=teamID%10000;
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

	public void setLastRobotControl(FRCRobotControl control) {
		this.lastRobotControl=control;
		setLastRobotControlImpl();
	}

	public FRCRobotControl getLastRobotControl() {
		return lastRobotControl;
	}

	protected void startLoops() {
		mainLoop=new Thread(new MainLoop(this,LOOP_FREQ),"FRCDS Main Loop");
		mainLoop.start();
	}

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

	protected void setLastRobotControlImpl() {
	}

	protected void doMainLoopImpl() {
	}

	protected void doEnabledLoopImpl() {
	}
}
