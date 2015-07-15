package org.anidev.frcds.proto2;

import org.anidev.frcds.proto2.types.OperationMode;

/**
 * Data common to DSData and RobotData
 * 
 * @author Michael Murphey
 */
public abstract class FRCData {

	private int teamID;
	private int packetIndex;
	private boolean codeReset;
	private boolean rebootRobot;
	private boolean enabled;
	private boolean fmsAttached;
	private boolean eStop;
	private OperationMode mode;

	/**
	 * Returns ID of the team operating this driver station.
	 */
	protected int getTeamID() {
		return teamID;
	}

	/**
	 * Sets ID of team.
	 * 
	 * @param teamID
	 *            The team ID to set.
	 */
	protected void setTeamID(int teamID) {
		this.teamID=teamID;
	}

	/**
	 * Specifies whether the robot code should be reset. This flag should only
	 * be sent until the first response from the robot. See
	 * {@link #setCodeReset} for details on how to use this.
	 * 
	 * @return Whether the code reset flag has been set.
	 */
	protected boolean isCodeReset() {
		return codeReset;
	}

	/**
	 * Sets the reset robot code flag, which signals the robot to reset the
	 * code. This should only be set to true once. {@link FRCCommunication} will
	 * reset this back to false automatically, and the protocol will take care
	 * of sending true for as long as necessary.
	 * 
	 * @param resetCode
	 *            Whether the code reset flag should be set.
	 */
	protected void setCodeReset(boolean codeReset) {
		this.codeReset=codeReset;
	}

	/**
	 * Specifies whether the robot code should be rebooted. This flag should
	 * only be sent until the first response from the robot. See
	 * {@link #setRebootRobot} for details on how to use this.
	 * 
	 * @return Whether the reboot robot flag has been set.
	 */
	protected boolean isRebootRobot() {
		return rebootRobot;
	}

	/**
	 * The client should only set a DSData object with this field set once.
	 * {@link FRCCommunication} will reset this back to false automatically, and
	 * the protocol will take care of sending true for as long as necessary.
	 * 
	 * @param rebootRobot
	 *            Whether the reboot robot flag should be set.
	 */
	protected void setRebootRobot(boolean rebootRobot) {
		this.rebootRobot=rebootRobot;
	}

	/**
	 * Specifies whether the robot should be enabled or not.
	 */
	protected boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets the enabled flag, which should be set for as long as the robot
	 * remains enabled.
	 * 
	 * @param enabled
	 *            True to enable the robot, false to disable.
	 */
	protected void setEnabled(boolean enabled) {
		this.enabled=enabled;
	}

	/**
	 * Specifies if the robot should be alerted to the presence of an FMS.
	 */
	protected boolean isFmsAttached() {
		return fmsAttached;
	}

	/**
	 * Specify whether an FMS is present for the match.
	 * 
	 * @param fmsAttached
	 *            True to alert the robot that an FMS is attached, false if not.
	 */
	protected void setFmsAttached(boolean fmsAttached) {
		this.fmsAttached=fmsAttached;
	}

	/**
	 * Specifies if the emergency stop has been activated.
	 */
	protected boolean isEStop() {
		return eStop;
	}

	/**
	 * Activate or deactivate emergency stop. Once set, the robot might need to
	 * be physically rebooted in order to deactivate emergency stop, even when
	 * this is set back to false.
	 * 
	 * @param eStop
	 *            True to activate, false to deactivate
	 */
	protected void setEStop(boolean eStop) {
		this.eStop=eStop;
	}

	/**
	 * Get the current operation mode of the robot (teleop, autonomous, etc).
	 */
	protected OperationMode getMode() {
		return mode;
	}

	/**
	 * Change the current operation mode of the robot to teleop, autonomous,
	 * etc.
	 * 
	 * @param mode
	 *            New robot operation mode
	 */
	protected void setMode(OperationMode mode) {
		this.mode=mode;
	}

	/**
	 * @return the packetIndex
	 */
	public int getPacketIndex() {
		return packetIndex;
	}

	/**
	 * @param packetIndex
	 *            the packetIndex to set
	 */
	public void setPacketIndex(int packetIndex) {
		this.packetIndex=packetIndex;
	}

}