package org.anidev.frcds.proto2;

/**
 * Listener interface for receiving data from the driver station that has been
 * deserialized and processed.
 * 
 * @author Anirudh Bagde
 */
public interface DSDataListener {
	/**
	 * Called when data is received and deserialized.
	 * 
	 * @param data
	 *            The parsed DSData object.
	 */
	public void receivedDSData(DSData data);
}
