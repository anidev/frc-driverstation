package org.anidev.frcds.proto.torobot;

import java.util.Arrays;
import org.anidev.frcds.proto.CommData;

public class Joystick extends CommData {
	public static final int SIZE=8;
	// 6 axes of motion, each a signed byte/int8
	byte[] axes=new byte[] {0,0,0,0,0,0};
	// 12 buttons max, stored in 16 bits with left 4 bits ignored
	boolean[] buttons=new boolean[] {false,false,false,false,false,false,false,
			false,false,false,false,false};

	public byte getAxis(int index) {
		return axes[index];
	}

	public void setAxis(int index,byte value) {
		axes[index]=value;
	}
	
	public int getNumAxes() {
		return axes.length;
	}

	public boolean getButton(int index) {
		return buttons[index];
	}

	public void setButton(int index,boolean value) {
		buttons[index]=value;
	}
	
	public int getNumButtons() {
		return buttons.length;
	}

	@Override
	public byte[] serialize() {
		byte[] data=new byte[8];
		for(int i=0;i<axes.length;i++) {
			data[i]=axes[i];
		}
		boolean[] buttons1=Arrays.copyOfRange(buttons,0,4);
		boolean[] buttons2=Arrays.copyOfRange(buttons,4,12);
		data[6]=(byte)(bitsToInts(buttons1)[0]>>4);
		data[7]=(byte)bitsToInts(buttons2)[0];
		return data;
	}

	@Override
	public void deserialize(byte[] data) {
		for(int i=0;i<6;i++) {
			axes[i]=data[i];
		}
		boolean[] buttonBits=intsToBits(new int[] {(int)data[6],(int)data[7]});
		buttonBits=Arrays.copyOfRange(buttonBits,4,16);
		this.buttons=buttonBits;
	}

	@Override
	public int hashCode() {
		final int prime=31;
		int result=1;
		result=prime*result+Arrays.hashCode(axes);
		result=prime*result+Arrays.hashCode(buttons);
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
		Joystick other=(Joystick)obj;
		if(!Arrays.equals(axes,other.axes)) {
			return false;
		}
		if(!Arrays.equals(buttons,other.buttons)) {
			return false;
		}
		return true;
	}
}
