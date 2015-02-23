package org.anidev.frcds.protoold;

import org.anidev.frcds.protoold.tods.FRCRobotControl;
import org.anidev.frcds.protoold.torobot.FRCCommonControl;

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
