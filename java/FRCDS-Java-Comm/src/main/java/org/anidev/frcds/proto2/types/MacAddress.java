package org.anidev.frcds.proto2.types;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;

/**
 * MAC address on the network
 */
public class MacAddress {
	public static final int SIZE=6;
	private byte[] macAddress=new byte[] {0,0,0,0,0,0};

	public MacAddress() {
		try {
			macAddress=NetworkInterface.getNetworkInterfaces().nextElement()
					.getHardwareAddress();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public byte[] getAddressBytes() {
		return macAddress;
	}

	public void setAddressBytes(byte[] macAddress) {
		this.macAddress=macAddress;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime=31;
		int result=1;
		result=prime*result+Arrays.hashCode(macAddress);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(this==obj) {
			return true;
		}
		if(obj==null) {
			return false;
		}
		if(getClass()!=obj.getClass()) {
			return false;
		}
		MacAddress other=(MacAddress)obj;
		if(!Arrays.equals(macAddress,other.macAddress)) {
			return false;
		}
		return true;
	}

}
