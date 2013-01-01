package org.anidev.frcds.common;

import org.anidev.frcds.common.types.BatteryProvider;
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
	protected boolean enabled=false;
	protected double elapsedTime=0.0;
	protected double batteryPercent=-1.0;

	protected abstract void setEnabledImpl();

	protected abstract void setElapsedTimeImpl();
	
	protected abstract void setBatteryPercentImpl();
	
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
		frcComm.sendToRobot(dsControl);
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
	
	public void refreshBattery() {
		if(batteryProvider==null) {
			batteryPercent=-1.0;
		} else {
			batteryPercent=batteryProvider.getBatteryPercent();
		}
		setBatteryPercentImpl();
	}
}
