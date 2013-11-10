package org.anidev.frcds.proto.tods;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import org.anidev.frcds.proto.CommData;
import org.anidev.frcds.proto.ControlFlags;
import org.anidev.frcds.proto.Version;

public class FRCRobotControl extends CommData {
	public static final int SIZE=32;
	public static final Version CURRENT_VERSION=new Version("12191200");
	private static int totalPackets=0;
	private ControlFlags control=new ControlFlags();
	private int batteryVolts=0;
	private int batteryMV=0;
	private DigitalOutputs digitalOutputs=new DigitalOutputs();
	private byte[] unknown1=new byte[] {0,0,0,0};
	private int teamID=0;
	private MacAddress macAddress=new MacAddress();
	private Version version=CURRENT_VERSION;
	private byte[] unknown2=new byte[] {0,0,0,0,0,0};
	private int packetIndex=++totalPackets;

	public static int getTotalPackets() {
		return totalPackets;
	}
	
	public ControlFlags getControlFlags() {
		return control;
	}

	public void setControlFlags(ControlFlags control) {
		this.control=control;
	}

	public int getBatteryVoltsInt() {
		return batteryVolts;
	}

	public void setBatteryVoltsInt(int batteryVolts) {
		this.batteryVolts=batteryVolts;
	}

	public int getBatteryVoltsFraction() {
		return batteryMV;
	}

	public void setBatteryFraction(int batteryMV) {
		this.batteryMV=batteryMV;
	}
	
	public double getBatteryVolts() {
		return batteryVolts+(batteryMV*1.0/1000.0);
	}
	
	public void setBatteryVolts(double volts) {
		this.batteryVolts=(int)volts;
		this.batteryMV=(int)((volts-batteryVolts)*1000);
	}

	public DigitalOutputs getDigitalOutputs() {
		return digitalOutputs;
	}

	public void setDigitalOutputs(DigitalOutputs digitalOutputs) {
		this.digitalOutputs=digitalOutputs;
	}

	public int getTeamID() {
		return teamID;
	}

	public void setTeamID(int teamID) {
		this.teamID=teamID;
	}

	public MacAddress getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(MacAddress macAddress) {
		this.macAddress=macAddress;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version=version;
	}

	public int getPacketIndex() {
		return packetIndex;
	}

	public void setPacketIndex(int packetIndex) {
		this.packetIndex=packetIndex;
	}

	@Override
	public byte[] serialize() {
		ByteArrayOutputStream byteStream=new ByteArrayOutputStream(SIZE);
		byte[] controlBytes=control.serialize();
		byteStream.write(controlBytes,0,controlBytes.length);
		byteStream.write(new byte[] {(byte)batteryVolts},0,1);
		byteStream.write(new byte[] {(byte)batteryMV},0,1);
		byte[] digitalOutputsBytes=digitalOutputs.serialize();
		byteStream.write(digitalOutputsBytes,0,digitalOutputsBytes.length);
		byteStream.write(unknown1,0,unknown1.length);
		ByteBuffer byteBuffer=ByteBuffer.allocate(2);
		byteBuffer.order(BYTE_ORDER);
		byteBuffer.putShort((short)teamID);
		byte[] teamIDBytes=byteBuffer.array();
		byteStream.write(teamIDBytes,0,teamIDBytes.length);
		byte[] macAddressBytes=macAddress.serialize();
		byteStream.write(macAddressBytes,0,macAddressBytes.length);
		byte[] versionBytes=version.serialize();
		byteStream.write(versionBytes,0,versionBytes.length);
		byteStream.write(unknown2,0,unknown2.length);
		byteBuffer.putShort(0,(short)packetIndex);
		byte[] packetIndexBytes=byteBuffer.array();
		byteStream.write(packetIndexBytes,0,packetIndexBytes.length);
		return byteStream.toByteArray();
	}

	@Override
	public void deserialize(byte[] data) {
		int index=0;
		control.deserialize(Arrays.copyOfRange(data,index,
				index+=ControlFlags.SIZE));
		batteryVolts=data[index++]&0xFF;
		batteryMV=data[index++]&0xFF;
		digitalOutputs.deserialize(Arrays.copyOfRange(data,index,
				index+=DigitalOutputs.SIZE));
		unknown1=Arrays.copyOfRange(data,index,index+=unknown1.length);
		ByteBuffer byteBuffer=ByteBuffer.allocate(2);
		byteBuffer.put(data[index++]).put(data[index++]);
		byteBuffer.position(0);
		ShortBuffer shortBuffer=byteBuffer.asShortBuffer();
		teamID=shortBuffer.get(0);
		macAddress.deserialize(Arrays.copyOfRange(data,index,index+=MacAddress.SIZE));
		version.deserialize(Arrays.copyOfRange(data,index,index+=Version.SIZE));
		unknown2=Arrays.copyOfRange(data,index,index+=unknown2.length);
		byteBuffer.put(0,data[index++]).put(1,data[index++]);
		packetIndex=shortBuffer.get(0);
	}

	@Override
	public int hashCode() {
		final int prime=31;
		int result=1;
		result=prime*result+batteryMV;
		result=prime*result+batteryVolts;
		result=prime*result+((control==null)?0:control.hashCode());
		result=prime*result
				+((digitalOutputs==null)?0:digitalOutputs.hashCode());
		result=prime*result+((macAddress==null)?0:macAddress.hashCode());
		result=prime*result+packetIndex;
		result=prime*result+teamID;
		result=prime*result+Arrays.hashCode(unknown1);
		result=prime*result+Arrays.hashCode(unknown2);
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
		FRCRobotControl other=(FRCRobotControl)obj;
		if(batteryMV!=other.batteryMV) {
			return false;
		}
		if(batteryVolts!=other.batteryVolts) {
			return false;
		}
		if(control==null) {
			if(other.control!=null) {
				return false;
			}
		} else if(!control.equals(other.control)) {
			return false;
		}
		if(digitalOutputs==null) {
			if(other.digitalOutputs!=null) {
				return false;
			}
		} else if(!digitalOutputs.equals(other.digitalOutputs)) {
			return false;
		}
		if(macAddress==null) {
			if(other.macAddress!=null) {
				return false;
			}
		} else if(!macAddress.equals(other.macAddress)) {
			return false;
		}
		if(packetIndex!=other.packetIndex) {
			return false;
		}
		if(teamID!=other.teamID) {
			return false;
		}
		if(!Arrays.equals(unknown1,other.unknown1)) {
			return false;
		}
		if(!Arrays.equals(unknown2,other.unknown2)) {
			return false;
		}
		return true;
	}

}
