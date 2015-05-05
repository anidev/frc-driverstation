package org.anidev.frcds.proto2;

import org.anidev.frcds.proto2.types.Alliance;
import org.anidev.frcds.proto2.types.Joystick;
import org.anidev.frcds.proto2.types.OperationMode;

/**
 * Data sent by the driver station to the robot.
 * 
 * @author Anirudh Bagde
 */
public class DSData {
	// Status data
	private int teamID;
	private boolean codeReset;
	private boolean rebootRobot;
	private boolean enabled;
	private boolean fmsAttached;
	private boolean eStop;
	private OperationMode mode;

	// Match data
	private Alliance alliance;
	private int position;

	// Control data
	private Joystick[] joysticks;
	private boolean[] digitalInputs;
	private int[] analogInputs;

	/**
	 * Returns ID of the team operating this driver station.
	 * 
	 * @return The team ID.
	 */
	public int getTeamID() {
		return teamID;
	}

	/**
	 * {@link #getTeamID}
	 * 
	 * @param teamID
	 *            The team ID to set.
	 */
	public void setTeamID(int teamID) {
		this.teamID=teamID;
	}

	/**
	 * Specifies whether the robot code should be reset. This flag should only
	 * be sent until the first response from the robot. See
	 * {@link #setCodeReset} for details on how to use this.
	 * 
	 * @return Whether the code reset flag has been set.
	 */
	public boolean isCodeResetSet() {
		return codeReset;
	}

	/**
	 * Sets the reset robot code flag, which signals the robot to reset the code. 
         * The client should only set a DSData object with this field set once, and
	 * then change it back to false. The protocol will take care of sending true
	 * for as long as necessary.
	 * 
	 * @param resetCode
	 *            Whether the code reset flag should be set.
	 */
	public void setCodeReset(boolean resetCode) {
		this.codeReset=resetCode;
	}

	/**
	 * Specifies whether the robot code should be rebooted. This flag should
	 * only be sent until the first response from the robot. See
	 * {@link #setRebootRobot} for details on how to use this.
	 * 
	 * @return Whether the reboot robot flag has been set.
	 */
	public boolean isRebootRobotSet() {
		return rebootRobot;
	}

	/**
	 * The client should only set a DSData object with this field set once, and
	 * then change it back to false. The protocol will take care of sending true
	 * for as long as necessary.
	 * 
	 * @param rebootRobot
	 *            Whether the reboot robot flag should be set.
	 */
	public void setRebootRobot(boolean rebootRobot) {
		this.rebootRobot=rebootRobot;
	}

	/**
	 * Specifies whether the robot should be enabled or not.
	 * @return 
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets the enabled flag, which should be set for as long as the robot remains
	 * enabled. 
	 * @param enabled
	 *            True to enable the robot, false to disable.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled=enabled;
	}

	/**
	 * @return Whether the robot should be alerted to the presence of an FMS.
	 */
	public boolean isFmsAttached() {
		return fmsAttached;
	}

	/**
	 * @param fmsAttached
	 *            True to alert the robot that an FMS is attached, false if not.
	 */
	public void setFmsAttached(boolean fmsAttached) {
		this.fmsAttached=fmsAttached;
	}

	/**
	 * @return Whether emergency stop has been activated.
	 */
	public boolean iseStop() {
		return eStop;
	}

	/**
	 * @param eStop
	 *            True to activate emergency stop. Once set, the robot might
	 *            need to be physically rebooted in order to deactivate
	 *            emergency stop, even when this is set to false.
	 */
	public void seteStop(boolean eStop) {
		this.eStop=eStop;
	}

	/**
	 * @return The current operation mode of the robot (teleop, autonomous,
	 *         etc).
	 */
	public OperationMode getMode() {
		return mode;
	}

	/**
	 * @param mode
	 *            Change the current operation mode of the robot to teleop,
	 *            autonomous, etc.
	 */
	public void setMode(OperationMode mode) {
		this.mode=mode;
	}

	/**
	 * @return The 
	 */
	public Alliance getAlliance() {
		return alliance;
	}

	/**
	 * @param alliance
	 *            the alliance to set
	 */
	public void setAlliance(Alliance alliance) {
		this.alliance=alliance;
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(int position) {
		this.position=position;
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

}
