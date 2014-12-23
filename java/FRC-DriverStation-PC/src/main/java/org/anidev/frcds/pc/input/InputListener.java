package org.anidev.frcds.pc.input;

/**
 * Add and remove input devices
 */
public abstract class InputListener {
	/**
	 * @param dev the input device to add
	 */
	public void deviceAdded(InputDevice dev) {
	}
	/**
	 * @param dev the input device to remove
	 */
	public void deviceRemoved(InputDevice dev) {
	}
}
