package org.anidev.frcds.pc.input;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class InputEnvironment {
	public static final int NUM_DEVICES=4;
	private static MessageDigest digest=null;
	private ControllerEnvironment env=null;
	private String[] deviceHashes=new String[] {null,null,null,null};
	private Map<String,InputDevice> deviceMap=Collections
			.synchronizedMap(new HashMap<String,InputDevice>());
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
		Logger.getLogger("net.java.games.input.ControllerEnvironment")
				.setUseParentHandlers(false);
		env=ControllerEnvironment.getDefaultEnvironment();
		updateControllers();
	}

	public synchronized Map<String,InputDevice> getDeviceMap() {
		return Collections.unmodifiableMap(deviceMap);
	}

	public synchronized InputDevice getDevice(int index) {
		if(index<0||index>3) {
			return null;
		}
		String hash=deviceHashes[index];
		return deviceMap.get(hash);
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
		Controller[] controllers=env.getControllers();
		for(Controller controller:controllers) {
			processController(controller);
		}
	}

	private void processController(Controller controller) {
		String controllerStr=stringifyController(controller);
		String controllerHash=hashToString(makeHash(controllerStr));
		if(deviceMap.containsKey(controllerHash)) {
			return;
		}
		InputDevice dev=new InputDevice(controller);
		deviceMap.put(controllerHash,dev);
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
