package org.anidev.frcds.pc.input;

import java.lang.reflect.Constructor;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * The environment of the input devices
 */
public class InputEnvironment {
	public static final int NUM_DEVICES=4;
	private static MessageDigest digest=null;
	private String[] deviceHashes=new String[] {null,null,null,null};
	private Map<String,InputDevice> deviceMap=Collections
			.synchronizedMap(new HashMap<String,InputDevice>());
	private Map<String,Integer> oldPositionMap=Collections
			.synchronizedMap(new HashMap<String,Integer>());
	private List<InputListener> listeners=Collections
			.synchronizedList(new ArrayList<InputListener>());
	static {
		try {
			digest=MessageDigest.getInstance("MD5");
		} catch(NoSuchAlgorithmException e) {
			// MD5 should exist in all implementations
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Set up the logger and update the controllers
	 */
	public InputEnvironment() {
		Logger.getLogger("net.java.games.input").setUseParentHandlers(false);
		updateControllers();
	}

	/**
	 * @param listener the listener to add
	 */
	public void addInputListener(InputListener listener) {
		listeners.add(listener);
	}

	/**
	 * @param listener the listener to remove
	 */
	public void removeInputListener(InputListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @return the map of the input devices
	 */
	public synchronized Map<String,InputDevice> getDeviceMap() {
		return Collections.unmodifiableMap(deviceMap);
	}

	/**
	 * @param index the index of the device
	 * @return the device at that index or null if the index is not valid
	 */
	public synchronized InputDevice getDevice(int index) {
		if(index<0||index>3) {
			return null;
		}
		String hash=deviceHashes[index];
		if(hash==null) {
			return null;
		}
		return deviceMap.get(hash);
	}

	/**
	 * @param hash the hash of the input device
	 * @return the input device with that hash
	 */
	public synchronized InputDevice getDevice(String hash) {
		return deviceMap.get(hash);
	}

	/**
	 * @param index the index of the device hash
	 * @return hash the hash at that index
	 */
	public synchronized String getDeviceHash(int index) {
		if(index<0||index>3) {
			return null;
		}
		return deviceHashes[index];
	}

	/**
	 * The two devices switch places
	 * @param index1 the index of the first device
	 * @param index2 the index of the second device
	 */
	public synchronized void swapDevices(int index1,int index2) {
		String hash1=deviceHashes[index1];
		String hash2=deviceHashes[index2];
		deviceHashes[index1]=hash2;
		deviceHashes[index2]=hash1;
	}

	/**
	 * @param index the index to put the device in
	 * @param hash the hash of the device
	 */
	public synchronized void setDevice(int index,String hash) {
		if(!deviceMap.containsKey(hash)) {
			throw new IllegalArgumentException("Device hash "+hash
					+" does not exist");
		}
		deviceHashes[index]=hash;
	}

	/**
	 * @param index the index of the device to unset
	 */
	public synchronized void unsetDevice(int index) {
		deviceHashes[index]=null;
	}

	/**
	 * Force the controllers to be updated because by default,
	 * the ControllerEnvironment only scans controllers once and
	 * caches them for all future instantiations
	 */
	public synchronized void updateControllers() {
		ControllerEnvironment env=null;
		try {
			// The following hack is to force controllers to be updated
			// because by default, the ControllerEnvironment only scans
			// controllers once and caches them for all future instantiations
			Class<?> clazz=Class
					.forName("net.java.games.input.DefaultControllerEnvironment");
			Constructor<?> defaultConstructor=clazz.getDeclaredConstructor();
			defaultConstructor.setAccessible(true); // set visibility to public
			env=(ControllerEnvironment)defaultConstructor.newInstance();
		} catch(Exception e) {
			e.printStackTrace();
		}
		Controller[] controllers=env.getControllers();
		Set<String> oldKeys=new HashSet<>(deviceMap.keySet());
		Set<String> processedKeys=new HashSet<>();
		synchronized(listeners) {
			for(Controller controller:controllers) {
				processController(controller,processedKeys);
			}
		}
		processDevicesRemoved(oldKeys,processedKeys);
	}

	/**
	 * Add an input device with the controller to the environment
	 * @param controller the controller to process
	 * @param procesedKeys the set to add the controller's hash to
	 */
	private void processController(Controller controller,
			Set<String> procesedKeys) {
		String controllerStr=stringifyController(controller);
		String controllerHash=hashToString(makeHash(controllerStr));
		procesedKeys.add(controllerHash);
		if(deviceMap.containsKey(controllerHash)) {
			return;
		}
		InputDevice dev=new InputDevice(controller);
		deviceMap.put(controllerHash,dev);
		if(dev.getType()==Type.UNKNOWN) {
			return;
		}
		if(oldPositionMap.containsKey(controllerHash)) {
			int oldPosition=oldPositionMap.get(controllerHash);
			if(oldPosition>=0&&oldPosition<=3&&deviceHashes[oldPosition]==null) {
				deviceHashes[oldPosition]=controllerHash;
				oldPositionMap.remove(controllerHash);
			}
		} else {
			for(int i=0;i<deviceHashes.length;i++) {
				if(deviceHashes[i]==null) {
					deviceHashes[i]=controllerHash;
					break;
				}
			}
		}
		for(InputListener listener:listeners) {
			listener.deviceAdded(dev);
		}
	}

	/**
	 * Take out the old devices and hashes
	 * @param oldKeys set of old device hashes
	 * @param processedKeys set of processed device hashes
	 */
	private void processDevicesRemoved(Set<String> oldKeys,
			Set<String> processedKeys) {
		oldKeys.removeAll(processedKeys);
		if(oldKeys.size()==0) {
			return;
		}
		synchronized(listeners) {
			for(String hash:oldKeys) {
				for(int i=0;i<deviceHashes.length;i++) {
					if(hash.equals(deviceHashes[i])) {
						deviceHashes[i]=null;
						oldPositionMap.put(hash,i);
					}
				}
				InputDevice dev=deviceMap.get(hash);
				for(InputListener listener:listeners) {
					listener.deviceRemoved(dev);
				}
				deviceMap.remove(hash);
				// TODO properly clear weakreferences to input devices
			}
		}
	}

	/**
	 * @param controller the controller to create a String for
	 * @return a String representation of that controller
	 */
	private static String stringifyController(Controller controller) {
		StringBuilder buffer=new StringBuilder();
		buffer.append(controller.getPortType());
		buffer.append(":");
		buffer.append(controller.getPortNumber());
		buffer.append("|");
		buffer.append(controller.getName());
		buffer.append("|[");
		Component[] components=controller.getComponents();
		for(Component component:components) {
			buffer.append(component.getIdentifier());
			buffer.append(":");
			buffer.append(component.getName());
			buffer.append("|");
		}
		buffer.append("]");
		return buffer.toString();
	}

	/**
	 * @param str bytes to generate a hash for
	 * @return a hash for those bytes
	 */
	private static byte[] makeHash(String str) {
		digest.reset();
		return digest.digest(str.getBytes());
	}

	/**
	 * @param hash the hash to create a string for
	 * @return a hexadecimal string representing the hash
	 */
	private static String hashToString(byte[] hash) {
		StringBuffer hexStr=new StringBuffer();
		for(byte b:hash) {
			String hexByte=Integer.toHexString(0xFF&b);
			if(hexByte.length()==1) {
				hexStr.append("0");
			}
			hexStr.append(hexByte);
		}
		return hexStr.toString();
	}
}
