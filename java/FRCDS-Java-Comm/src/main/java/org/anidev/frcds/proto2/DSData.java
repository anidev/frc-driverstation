package org.anidev.frcds.proto2;

import org.anidev.frcds.proto2.types.Joystick;
import org.anidev.frcds.proto2.types.OperationMode;
import org.anidev.frcds.proto2.types.TeamStation;

/**
 * Data sent by the driver station to the robot.
 * 
 * @author Anirudh Bagde
 */
public class DSData extends FRCData {
	// Match data
	private TeamStation station;

	// Control data
	private Joystick[] joysticks;
	private boolean[] digitalInputs;
	private int[] analogInputs;

	/* (non-Javadoc)
	 * @see org.anidev.frcds.proto2.FRCData#getTeamID()
	 */
	@Override
	public int getTeamID() {
		return super.getTeamID();
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.proto2.FRCData#setTeamID(int)
	 */
	@Override
	public void setTeamID(int teamID) {
		super.setTeamID(teamID);
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.proto2.FRCData#isCodeReset()
	 */
	@Override
	public boolean isCodeReset() {
		return super.isCodeReset();
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.proto2.FRCData#setCodeReset(boolean)
	 */
	@Override
	public void setCodeReset(boolean resetCode) {
		super.setCodeReset(resetCode);
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.proto2.FRCData#isRebootRobot()
	 */
	@Override
	public boolean isRebootRobot() {
		return super.isRebootRobot();
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.proto2.FRCData#setRebootRobot(boolean)
	 */
	@Override
	public void setRebootRobot(boolean rebootRobot) {
		super.setRebootRobot(rebootRobot);
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.proto2.FRCData#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return super.isEnabled();
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.proto2.FRCData#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.proto2.FRCData#isFmsAttached()
	 */
	@Override
	public boolean isFmsAttached() {
		return super.isFmsAttached();
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.proto2.FRCData#setFmsAttached(boolean)
	 */
	@Override
	public void setFmsAttached(boolean fmsAttached) {
		super.setFmsAttached(fmsAttached);
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.proto2.FRCData#iseStop()
	 */
	@Override
	public boolean iseStop() {
		return super.iseStop();
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.proto2.FRCData#seteStop(boolean)
	 */
	@Override
	public void seteStop(boolean eStop) {
		super.seteStop(eStop);
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.proto2.FRCData#getMode()
	 */
	@Override
	public OperationMode getMode() {
		return super.getMode();
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.proto2.FRCData#setMode(org.anidev.frcds.proto2.types.OperationMode)
	 */
	@Override
	public void setMode(OperationMode mode) {
		super.setMode(mode);
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return station.getPosition();
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(int position) {
		this.station=station.withPosition(position);
	}

	/**
	 * @return the joysticks
	 */
	public Joystick[] getJoysticks() {
		return joysticks;
	}

	/**
	 * @param joysticks
	 *            the joysticks to set
	 */
	public void setJoysticks(Joystick[] joysticks) {
		this.joysticks=joysticks;
	}

	/**
	 * @return the digitalInputs
	 */
	public boolean[] getDigitalInputs() {
		return digitalInputs;
	}

	/**
	 * @param digitalInputs
	 *            the digitalInputs to set
	 */
	public void setDigitalInputs(boolean[] digitalInputs) {
		this.digitalInputs=digitalInputs;
	}

	/**
	 * @return the analogInputs
	 */
	public int[] getAnalogInputs() {
		return analogInputs;
	}

	/**
	 * @param analogInputs
	 *            the analogInputs to set
	 */
	public void setAnalogInputs(int[] analogInputs) {
		this.analogInputs=analogInputs;
	}

	/**
	 * @return the station
	 */
	public TeamStation getStation() {
		return station;
	}

	/**
	 * @param station
	 *            the station to set
	 */
	public void setStation(TeamStation station) {
		this.station=station;
	}

}
