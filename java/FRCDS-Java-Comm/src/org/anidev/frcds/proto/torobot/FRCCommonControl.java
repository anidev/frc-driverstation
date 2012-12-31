package org.anidev.frcds.proto.torobot;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import org.anidev.frcds.proto.CommData;
import org.anidev.frcds.proto.ControlFlags;
import org.anidev.frcds.proto.Version;

public class FRCCommonControl extends CommData {
	public static final int SIZE=80;
	public static final Version CURRENT_VERSION=new Version("11301100");
	private static int totalPackets=0;
	private int packetIndex=++totalPackets;
	private ControlFlags control=new ControlFlags();
	private DigitalInputs digitalInputs=new DigitalInputs();
	private int teamID=0;
	private Alliance alliance=Alliance.RED;
	private char position='1';
	private Joystick[] joysticks=new Joystick[] {new Joystick(),new Joystick(),
			new Joystick(),new Joystick()};
	private AnalogInputs analogInputs=new AnalogInputs();
	private CRIOChecksum crioChecksum=new CRIOChecksum();
	private FPGAChecksum fpgaChecksum=new FPGAChecksum();
	private Version version=CURRENT_VERSION;
	
	public static int getTotalPackets() {
		return totalPackets;
	}

	public int getPacketIndex() {
		return packetIndex;
	}

	public void setPacketIndex(int packetIndex) {
		this.packetIndex=packetIndex;
	}

	public ControlFlags getControlFlags() {
		return control;
	}

	public void setControlFlags(ControlFlags control) {
		this.control=control;
	}

	public DigitalInputs getDigitalInputs() {
		return digitalInputs;
	}

	public void setDigitalInputs(DigitalInputs digitalInputs) {
		this.digitalInputs=digitalInputs;
	}

	public int getTeamID() {
		return teamID;
	}

	public void setTeamID(int teamID) {
		this.teamID=teamID;
	}

	public Alliance getAlliance() {
		return alliance;
	}

	public void setAlliance(Alliance alliance) {
		this.alliance=alliance;
	}

	public char getPosition() {
		return position;
	}

	public void setPosition(char position) {
		this.position=position;
	}

	public Joystick[] getJoysticks() {
		return joysticks;
	}

	public void setJoysticks(Joystick[] joysticks) {
		this.joysticks=joysticks;
	}
	
	public int getNumJoysticks() {
		return joysticks.length;
	}

	public Joystick getJoystick(int index) {
		return joysticks[index];
	}

	public void setJoystick(int index,Joystick newJoystick) {
		joysticks[index]=newJoystick;
	}

	public AnalogInputs getAnalogInputs() {
		return analogInputs;
	}

	public void setAnalogInputs(AnalogInputs analogInputs) {
		this.analogInputs=analogInputs;
	}

	public CRIOChecksum getCrioChecksum() {
		return crioChecksum;
	}

	public void setCrioChecksum(CRIOChecksum crioChecksum) {
		this.crioChecksum=crioChecksum;
	}

	public FPGAChecksum getFpgaChecksum() {
		return fpgaChecksum;
	}

	public void setFpgaChecksum(FPGAChecksum fpgaChecksum) {
		this.fpgaChecksum=fpgaChecksum;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version=version;
	}

	@Override
	public byte[] serialize() {
		ByteArrayOutputStream byteStream=new ByteArrayOutputStream(SIZE);
		ByteBuffer byteBuffer=ByteBuffer.allocate(2);
		byteBuffer.order(BYTE_ORDER);
		byteBuffer.putShort((short)packetIndex);
		byte[] packetIndexBytes=byteBuffer.array();
		byteStream.write(packetIndexBytes,0,packetIndexBytes.length);
		byte[] controlBytes=control.serialize();
		byteStream.write(controlBytes,0,controlBytes.length);
		byte[] digitalInputsBytes=digitalInputs.serialize();
		byteStream.write(digitalInputsBytes,0,digitalInputsBytes.length);
		byteBuffer.putShort(0,(short)teamID);
		byte[] teamIDBytes=byteBuffer.array();
		byteStream.write(teamIDBytes,0,teamIDBytes.length);
		byteStream.write(new byte[] {(byte)alliance.getId()},0,1);
		byteStream.write(new byte[] {(byte)position},0,1);
		for(int i=0;i<joysticks.length;i++) {
			byte[] joystickBytes=joysticks[i].serialize();
			byteStream.write(joystickBytes,0,joystickBytes.length);
		}
		byte[] analogInputsBytes=analogInputs.serialize();
		byteStream.write(analogInputsBytes,0,analogInputsBytes.length);
		byte[] crioChecksumBytes=crioChecksum.serialize();
		byteStream.write(crioChecksumBytes,0,crioChecksumBytes.length);
		byte[] fpgaChecksumBytes=fpgaChecksum.serialize();
		byteStream.write(fpgaChecksumBytes,0,fpgaChecksumBytes.length);
		byte[] versionBytes=version.serialize();
		byteStream.write(versionBytes,0,versionBytes.length);
		return byteStream.toByteArray();
	}

	@Override
	public void deserialize(byte[] data) {
		int index=0;
		ByteBuffer byteBuffer=ByteBuffer.allocate(2);
		byteBuffer.order(BYTE_ORDER);
		byteBuffer.put(data[index++]).put(data[index++]);
		byteBuffer.position(0);
		ShortBuffer shortBuffer=byteBuffer.asShortBuffer();
		packetIndex=shortBuffer.get(0);
		control.deserialize(Arrays.copyOfRange(data,index,
				index+=ControlFlags.SIZE));
		digitalInputs.deserialize(Arrays.copyOfRange(data,index,
				index+=DigitalInputs.SIZE));
		byteBuffer.put(0,data[index++]).put(1,data[index++]);
		teamID=shortBuffer.get(0);
		alliance=Alliance.find((char)(data[index++]&0xFF));
		position=(char)(data[index++]&0xFF);
		for(int i=0;i<joysticks.length;i++) {
			joysticks[i].deserialize(Arrays.copyOfRange(data,index,
					index+=Joystick.SIZE));
		}
		analogInputs.deserialize(Arrays.copyOfRange(data,index,
				index+=AnalogInputs.SIZE));
		crioChecksum.deserialize(Arrays.copyOfRange(data,index,
				index+=CRIOChecksum.SIZE));
		fpgaChecksum.deserialize(Arrays.copyOfRange(data,index,
				index+=FPGAChecksum.SIZE));
		version.deserialize(Arrays.copyOfRange(data,index,index+=Version.SIZE));
	}

	@Override
	public int hashCode() {
		final int prime=31;
		int result=1;
		result=prime*result+((alliance==null)?0:alliance.hashCode());
		result=prime*result+((analogInputs==null)?0:analogInputs.hashCode());
		result=prime*result+((control==null)?0:control.hashCode());
		result=prime*result+((crioChecksum==null)?0:crioChecksum.hashCode());
		result=prime*result+((digitalInputs==null)?0:digitalInputs.hashCode());
		result=prime*result+((fpgaChecksum==null)?0:fpgaChecksum.hashCode());
		result=prime*result+Arrays.hashCode(joysticks);
		result=prime*result+packetIndex;
		result=prime*result+position;
		result=prime*result+teamID;
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
		FRCCommonControl other=(FRCCommonControl)obj;
		if(alliance!=other.alliance) {
			return false;
		}
		if(analogInputs==null) {
			if(other.analogInputs!=null) {
				return false;
			}
		} else if(!analogInputs.equals(other.analogInputs)) {
			return false;
		}
		if(control==null) {
			if(other.control!=null) {
				return false;
			}
		} else if(!control.equals(other.control)) {
			return false;
		}
		if(crioChecksum==null) {
			if(other.crioChecksum!=null) {
				return false;
			}
		} else if(!crioChecksum.equals(other.crioChecksum)) {
			return false;
		}
		if(digitalInputs==null) {
			if(other.digitalInputs!=null) {
				return false;
			}
		} else if(!digitalInputs.equals(other.digitalInputs)) {
			return false;
		}
		if(fpgaChecksum==null) {
			if(other.fpgaChecksum!=null) {
				return false;
			}
		} else if(!fpgaChecksum.equals(other.fpgaChecksum)) {
			return false;
		}
		if(!Arrays.equals(joysticks,other.joysticks)) {
			return false;
		}
		if(packetIndex!=other.packetIndex) {
			return false;
		}
		if(position!=other.position) {
			return false;
		}
		if(teamID!=other.teamID) {
			return false;
		}
		return true;
	}

}
