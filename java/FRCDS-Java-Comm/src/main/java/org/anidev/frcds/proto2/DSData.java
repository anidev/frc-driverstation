package org.anidev.frcds.proto2;

import org.anidev.frcds.proto2.types.Joystick;
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
