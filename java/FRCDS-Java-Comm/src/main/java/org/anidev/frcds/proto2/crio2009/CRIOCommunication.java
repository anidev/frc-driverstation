package org.anidev.frcds.proto2.crio2009;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.zip.CRC32;

import org.anidev.frcds.proto2.DSData;
import org.anidev.frcds.proto2.FRCCommunication;
import org.anidev.frcds.proto2.RobotData;
import org.anidev.frcds.proto2.types.Joystick;
import org.anidev.frcds.proto2.types.OperationMode;
import org.anidev.frcds.proto2.types.Version;
import org.anidev.utils.Utils;

/**
 * FRC Communication for cRIO robots
 * 
 * @author Michael Murphey
 */
public class CRIOCommunication extends FRCCommunication {
	
	private int packetIndex;
	private boolean resync;
	private boolean checkVersions;
	private Version version;

	/**
	 * Just a normal no-arg constructor
	 */
	public CRIOCommunication() {
		
	}

	/**
	 * @param rRobot the robot
	 * @param rDS the driver station
	 */
	public CRIOCommunication(boolean rRobot, boolean rDS) {
		super(rRobot, rDS);
	}
	
	/**
	 * @param data the DSData to be sent to the robot
	 * @return byte array to be sent to the robot
	 */
	public byte[] serialize(DSData data){
		ByteArrayOutputStream byteStream=new ByteArrayOutputStream(1024);
		byte[] streamData = null;
		ByteBuffer byteBuffer=ByteBuffer.allocate(2);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		byteBuffer.putShort((short)packetIndex);
		byte[] indexBytes = byteBuffer.array();
		try {
			byteStream.write(indexBytes);
			// write control byte
			byteStream.write(Utils.bitsToInts(new boolean[] {data.isCodeReset(),!data.isEStop(),data.isEnabled(),
						data.getMode() == OperationMode.AUTONOMOUS,data.isFmsAttached(),resync,data.getMode() == OperationMode.TEST,checkVersions})[0]);
			byteStream.write(Utils.bitsToInts(data.getDigitalInputs())[0]);
			byteBuffer.putShort(0,(short)data.getTeamID());// write the team ID to the buffer overwriting the control byte
			byteStream.write(byteBuffer.array());
			byteStream.write(data.getAlliance().getId());
			byteStream.write(data.getPosition());
			
			// serialize joysticks
			for(Joystick joystick : data.getJoysticks()){
				byte[] bytes=new byte[8];
				// serialize axes
				for(int i=0;i<joystick.getAxes().length;i++) {
					bytes[i]=(byte) (joystick.getAxes()[i] * Byte.MAX_VALUE);
				}
				// serialize buttons
				boolean[] buttons1=Arrays.copyOfRange(joystick.getButtons(),8,12);
				boolean[] buttons2=Arrays.copyOfRange(joystick.getButtons(),0,8);
				Utils.reverseBits(buttons1);
				Utils.reverseBits(buttons2);
				bytes[6]=(byte)(Utils.bitsToInts(buttons1)[0]>>4);
				bytes[7]=(byte)Utils.bitsToInts(buttons2)[0];
				byteStream.write(bytes);
			}
			
			// serialize analog inputs
			byte[] analogInputs = new byte[data.getAnalogInputs().length];
			for(int i=0;i<analogInputs.length;i++){
				analogInputs[i] = (byte)data.getAnalogInputs()[i];
			}
			byteStream.write(analogInputs);
			
			// serialize version
			Calendar calendar=new GregorianCalendar(version.getYear(),version.getMonth()-1,version.getDay());
			Formatter formatter=new Formatter();
			formatter.format("%1$ty%1$td%1$tm%2$02d",calendar,version.getRevision());
			String str=formatter.toString();
			formatter.close();
			byteStream.write(str.getBytes());
			
			// this is the unknown part of the protocol
			byte[] highEnd = new byte[940];
			byteStream.write(highEnd);
			
			// time for the CRC
			byte[] crcBytes=new byte[4];
			byteStream.write(crcBytes);
			streamData=byteStream.toByteArray();
			CRC32 crc=new CRC32();
			crc.update(streamData);
			ByteBuffer crcBuffer=ByteBuffer.allocate(8);
			crcBuffer.putLong(crc.getValue());
			crcBytes=Arrays.copyOfRange(crcBuffer.array(),4,8);
			System.arraycopy(crcBytes,0,streamData,streamData.length-4,4);
			
			byteStream.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return streamData;
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.proto2.FRCCommunication#initProtocol()
	 */
	@Override
	protected void initProtocol() {
		
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.proto2.FRCCommunication#receiveFromRobot()
	 */
	@Override
	protected RobotData receiveFromRobot() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.proto2.FRCCommunication#receiveFromDS()
	 */
	@Override
	protected DSData receiveFromDS() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.proto2.FRCCommunication#sendToRobot(org.anidev.frcds.proto2.DSData)
	 */
	@Override
	protected boolean sendToRobot(DSData data) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.anidev.frcds.proto2.FRCCommunication#sendToDS(org.anidev.frcds.proto2.RobotData)
	 */
	@Override
	protected boolean sendToDS(RobotData data) {
		return false;
	}

}
