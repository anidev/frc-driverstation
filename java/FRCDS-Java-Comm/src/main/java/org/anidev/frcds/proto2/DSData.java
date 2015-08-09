package org.anidev.frcds.proto2;

import org.anidev.frcds.proto2.types.Alliance;
import org.anidev.frcds.proto2.types.Joystick;
import org.anidev.frcds.proto2.types.TeamStation;

/**
 * Data sent by the driver station to the robot.
 * 
 * @author Anirudh Bagde
 */
public class DSData extends FRCData {
	public static final int NUM_JOYSTICKS=4;
	public static final int NUM_DIGITAL_INPUTS=8;
	public static final int NUM_ANALOG_INPUTS=4;

	// Match data
	private TeamStation station=TeamStation.RED3;

	// Control data
	private Joystick[] joysticks=new Joystick[NUM_JOYSTICKS];
	private boolean[] digitalInputs=new boolean[NUM_DIGITAL_INPUTS];
	private int[] analogInputs=new int[NUM_ANALOG_INPUTS];
	
	private long crioChecksum;
	private int[] fpgaChecksum = {0,0,0,0};

	/**
	 * Get the currently set team station that is being advertised to the robot.
	 * 
	 * @return Team station
	 */
	public TeamStation getStation() {
		return station;
	}

	/**
	 * Set the current robot team station. The team station includes the team's
	 * alliance and the robot's position on that alliance.
	 * 
	 * @param station
	 *            New team station value
	 */
	public void setStation(TeamStation station) {
		this.station=station;
	}

	/**
	 * Get the alliance the robot should be on. Convenience method for
	 * {@link #getStation()}{@code .getAlliance()}.
	 * 
	 * @return The robot team alliance
	 */
	public Alliance getAlliance() {
		return station.getAlliance();
	}

	/**
	 * Set the team alliance. Utilizes {@link TeamStation#withAlliance}.
	 * 
	 * @param alliance
	 *            New alliance to set
	 */
	public void setAlliance(Alliance alliance) {
		this.station=station.withAlliance(alliance);
	}

	/**
	 * Get the robot position on the alliance. Convenience method for
	 * {@link #getStation()}{@code .getPosition()}.
	 * 
	 * @return Current robot position
	 */
	public int getPosition() {
		return station.getPosition();
	}

	/**
	 * Set the robot position on the alliance. Utilizes
	 * {@link TeamStation#withPosition}.
	 * 
	 * @param position
	 *            New robot position on alliance
	 */
	public void setPosition(int position) {
		this.station=station.withPosition(position);
	}

	/**
	 * Get all the joystick control objects that are being sent to the robot.
	 * Changes to joystick data can be made directly on the objects returned.
	 * Normally this should be enough; if it is more convenient to change one of
	 * the joystick reference sin the array, use
	 * {@link #setJoystick(Joystick, int)}.
	 * 
	 * @return Array of Joystick objects
	 */
	public Joystick[] getJoysticks() {
		return joysticks;
	}

	/**
	 * Change the joystick reference for a specific joystick in the control
	 * data. Sometimes it may be more convenient to do this rather than change
	 * the existing joystick's data to match.
	 * 
	 * @param joystick
	 *            The new Joystick object
	 * @param index
	 *            Index of joystick to change
	 */
	public void setJoystick(Joystick joystick,int index) {
		this.joysticks[index]=joystick;
	}

	/**
	 * Get the current value of all the digital input switches on the driver
	 * station/dashboard.
	 * 
	 * @return Array of digital input boolean values
	 */
	public boolean[] getDigitalInputs() {
		return digitalInputs;
	}

	/**
	 * Set the value of a specific digital input switch.
	 * 
	 * @param value
	 *            New digital input value
	 * @param index
	 *            Index of the digital input to set
	 */
	public void setDigitalInput(boolean value,int index) {
		digitalInputs[index]=value;
	}

	/**
	 * Get the current value of all the analog inputs. They are stored as ints
	 * to avoid sign issues.
	 * 
	 * @return Array of analog input numeric values
	 */
	public int[] getAnalogInputs() {
		return analogInputs;
	}

	/**
	 * Set the numeric value of a specific analog input. The max value is not
	 * completely clear, but is most likely the max value of an unsigned 16-bit
	 * (short) integer.
	 * 
	 * @param value
	 *            New analog input value
	 * @param index
	 *            Index of analog input to set
	 */
	public void setAnalogInput(int value,int index) {
		analogInputs[index]=value;
	}

	/**
	 * @return the crioChecksum
	 */
	public long getCrioChecksum() {
		return crioChecksum;
	}

	/**
	 * @param crioChecksum the crioChecksum to set
	 */
	public void setCrioChecksum(long crioChecksum) {
		this.crioChecksum = crioChecksum;
	}

	/**
	 * @return the fpgaChecksum
	 */
	public int[] getFpgaChecksum() {
		return fpgaChecksum;
	}

	/**
	 * @param fpgaChecksum the fpgaChecksum to set
	 */
	public void setFpgaChecksum(int[] fpgaChecksum) {
		this.fpgaChecksum = fpgaChecksum;
	}
}
