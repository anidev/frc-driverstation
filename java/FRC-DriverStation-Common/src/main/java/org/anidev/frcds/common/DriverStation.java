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
	public static final double UPDATE_HERTZ=50.0;
	public static final double SLOW_HERTZ=1.0;
	private FRCCommunication frcComm=new FRCCommunication();
	private FRCCommonControl dsControl=new FRCCommonControl();
	private FRCRobotControl lastRobotControl=null;
	private BatteryProvider batteryProvider=null;
	private Thread enabledLoop=null;
	private Thread commonLoop=null;
	private OperationMode mode=OperationMode.TELEOPERATED;
	private boolean enabled=false;
	private double elapsedTime=0.0;
	private double batteryPercent=-1.0;
	private int teamID=0;

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
		if(enabled) {
			if(enabledLoop!=null&&enabledLoop.isAlive()) {
				return;
			}
			enabledLoop=new Thread(new EnabledLoop(this,UPDATE_HERTZ),
					"FRCDS Enabled Loop");
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
		commonLoop=new Thread(new CommonLoop(this,SLOW_HERTZ),
				"FRCDS Common Loop");
		commonLoop.start();
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

	protected void doCommonLoopImpl() {
	}

	protected void doEnabledLoopImpl() {
	}

	/**
	 * @return the batteryPercent
	 */
	protected double getBatteryPercent() {
		return batteryPercent;
	}

	/**
	 * @param batteryPercent
	 *            the batteryPercent to set
	 */
	protected void setBatteryPercent(double batteryPercent) {
		this.batteryPercent=batteryPercent;
	}

	/**
	 * @return the frcComm
	 */
	protected FRCCommunication getFrcComm() {
		return frcComm;
	}

	/**
	 * @param frcComm
	 *            the frcComm to set
	 */
	protected void setFrcComm(FRCCommunication frcComm) {
		this.frcComm=frcComm;
	}

	/**
	 * @return the dsControl
	 */
	protected FRCCommonControl getDsControl() {
		return dsControl;
	}

	/**
	 * @param dsControl
	 *            the dsControl to set
	 */
	protected void setDsControl(FRCCommonControl dsControl) {
		this.dsControl=dsControl;
	}

	/**
	 * @return the enabledLoop
	 */
	protected Thread getEnabledLoop() {
		return enabledLoop;
	}

	/**
	 * @param enabledLoop
	 *            the enabledLoop to set
	 */
	protected void setEnabledLoop(Thread enabledLoop) {
		this.enabledLoop=enabledLoop;
	}

	/**
	 * @return the commonLoop
	 */
	protected Thread getCommonLoop() {
		return commonLoop;
	}

	/**
	 * @param commonLoop
	 *            the commonLoop to set
	 */
	protected void setCommonLoop(Thread commonLoop) {
		this.commonLoop=commonLoop;
	}
}
