package org.anidev.frcds.proto;

import org.anidev.frcds.proto.tods.FRCRobotControl;
import org.anidev.frcds.proto.torobot.FRCCommonControl;

/**
 * Listener interface for receiving data processed by the FRCCommunication
 * class. It can be used for receiving data from both the robot and the driver
 * station.
 * 
 * @author Anirudh Bagde
 */
public interface FRCCommunicationListener {
	/**
	 * Called when data is received and deserialized.
	 * 
	 * @param data
	 *            The parsed data object. This will probably be either
	 *            {@link FRCCommonControl} or {@link FRCRobotControl}.
	 */
	public void receivedData(CommData data);
}
