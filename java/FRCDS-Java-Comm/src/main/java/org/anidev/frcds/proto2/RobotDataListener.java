package org.anidev.frcds.proto2;

/**
 * Listener interface for receiving data from the robot that has been
 * deserialized and processed.
 * 
 * @author Anirudh Bagde
 */
public interface RobotDataListener {
	/**
	 * Called when data is received and deserialized.
	 * 
	 * @param data
	 *            The parsed RobotData object.
	 */
	public void receivedRobotData(RobotData data);
}
