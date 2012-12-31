package org.anidev.frcds.common;

import org.anidev.frcds.proto.FRCCommunication;
import org.anidev.frcds.proto.torobot.FRCCommonControl;

public abstract class DriverStation {
	public static final double UPDATE_HERTZ=50.0;
	private FRCCommunication frcComm=new FRCCommunication();
	protected FRCCommonControl dsControl=new FRCCommonControl();
	protected Thread loopThread=null;
	protected boolean enabled=false;
	protected double elapsedTime=0.0;

	protected abstract void setEnabledImpl();
	protected abstract void setElapsedTimeImpl();
	
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
			if(loopThread!=null&&loopThread.isAlive()) {
				return;
			}
			loopThread=new Thread(new MainLoop(this,UPDATE_HERTZ));
			loopThread.start();
		} else {
			if(loopThread!=null) {
				loopThread.interrupt();
			}
			loopThread=null;
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
}
