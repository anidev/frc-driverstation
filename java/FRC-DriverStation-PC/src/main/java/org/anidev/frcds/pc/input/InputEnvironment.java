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

	public InputEnvironment() {
		Logger.getLogger("net.java.games.input").setUseParentHandlers(false);
		updateControllers();
	}

	public void addInputListener(InputListener listener) {
		listeners.add(listener);
	}

	public void removeInputListener(InputListener listener) {
		listeners.remove(listener);
	}

	public synchronized Map<String,InputDevice> getDeviceMap() {
		return Collections.unmodifiableMap(deviceMap);
	}

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

	public synchronized InputDevice getDevice(String hash) {
		return deviceMap.get(hash);
	}

	public synchronized String getDeviceHash(int index) {
		if(index<0||index>3) {
			return null;
		}
		return deviceHashes[index];
	}

	public synchronized void swapDevices(int index1,int index2) {
		String hash1=deviceHashes[index1];
		String hash2=deviceHashes[index2];
		deviceHashes[index1]=hash2;
		deviceHashes[index2]=hash1;
	}

	public synchronized void setDevice(int index,String hash) {
		if(!deviceMap.containsKey(hash)) {
			throw new IllegalArgumentException("Device hash "+hash
					+" does not exist");
		}
		deviceHashes[index]=hash;
	}

	public synchronized void unsetDevice(int index) {
		deviceHashes[index]=null;
	}

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

	private static byte[] makeHash(String str) {
		digest.reset();
		return digest.digest(str.getBytes());
	}

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
