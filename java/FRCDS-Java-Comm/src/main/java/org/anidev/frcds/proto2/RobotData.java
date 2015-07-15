package org.anidev.frcds.proto2;

import org.anidev.frcds.proto2.types.MacAddress;

/**
 * Data sent by the robot to the driver station
 * 
 * @author Anirudh Bagde
 */
public class RobotData extends FRCData {
	public static final int NUM_DIGITAL_OUTPUTS=8;

	private double voltage;
	private boolean[] digitalOutputs=new boolean[NUM_DIGITAL_OUTPUTS];
	private MacAddress address=new MacAddress();

	/**
	 * Returns the currently set battery voltage as a double.
	 */
	public double getBatteryVoltage() {
		return voltage;
	}

	/**
	 * Set the robot's current battery voltage.
	 */
	public void setVoltage(double voltage) {
		this.voltage=voltage;
	}

	/**
	 * Get the current value of all the digital output switches being sent to
	 * the driver station/dashboard by the robot.
	 * 
	 * @return Array of digital output boolean values
	 */
	public boolean[] getDigitalOutputs() {
		return digitalOutputs;
	}

	/**
	 * Set the value of a specific digital output switch.
	 * 
	 * @param value
	 *            New digital output value
	 * @param index
	 *            Index of the digital output to set
	 */
	public void setOutputs(boolean value,int index) {
		digitalOutputs[index]=value;
	}

	/**
	 * Get the currently set MAC address. This is most likely the robot's MAC
	 * address, though that isn't definite. By default it is set to {@code null}
	 * , in which case the protocol will take care of filling in the correct MAC
	 * address for the robot.
	 */
	public MacAddress getMACAddress() {
		return address;
	}

	/**
	 * Set the MAC address, which most likely represents the robot's MAC
	 * address. If set to {@code null}, the protocol will fill in the correct
	 * address before sending.
	 */
	public void setMACAddress(MacAddress address) {
		this.address=address;
	}

}
