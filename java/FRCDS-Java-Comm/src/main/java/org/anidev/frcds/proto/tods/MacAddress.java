package org.anidev.frcds.proto.tods;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import org.anidev.frcds.proto.CommData;

public class MacAddress extends CommData {
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

	@Override
	public byte[] serialize() {
		return macAddress;
	}

	@Override
	public void deserialize(byte[] data) {
		macAddress=data;
	}

	@Override
	public int hashCode() {
		final int prime=31;
		int result=1;
		result=prime*result+Arrays.hashCode(macAddress);
		return result;
	}

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
