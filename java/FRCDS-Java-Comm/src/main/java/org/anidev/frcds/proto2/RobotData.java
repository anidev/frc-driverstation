package org.anidev.frcds.proto2;

import org.anidev.frcds.proto2.types.DigitalOutputs;
import org.anidev.frcds.proto2.types.MacAddress;

/**
 * Data sent by the robot to the driver station
 * 
 * @author Anirudh Bagde
 */
public class RobotData extends FRCData {
	
	private int voltage;
	private DigitalOutputs outputs;
	private MacAddress address;
	
	/**
	 * @return the voltage
	 */
	public int getVoltage() {
		return voltage;
	}
	
	/**
	 * @param voltage the voltage to set
	 */
	public void setVoltage(int voltage) {
		this.voltage = voltage;
	}
	
	/**
	 * @return the outputs
	 */
	public DigitalOutputs getOutputs() {
		return outputs;
	}
	
	/**
	 * @param outputs the outputs to set
	 */
	public void setOutputs(DigitalOutputs outputs) {
		this.outputs = outputs;
	}
	
	/**
	 * @return the address
	 */
	public MacAddress getAddress() {
		return address;
	}
	
	/**
	 * @param address the address to set
	 */
	public void setAddress(MacAddress address) {
		this.address = address;
	}
	
}
