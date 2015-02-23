package org.anidev.frcds.protoold.torobot;

import java.util.Arrays;
import org.anidev.frcds.protoold.CommData;

public class DigitalInputs extends CommData {
	public static final int SIZE=1;
	private boolean[] inputs=new boolean[] {true,true,true,true,true,true,true,true};

	public boolean getInput(int index) {
		return inputs[index];
	}

	public void setInput(int index,boolean value) {
		inputs[index]=value;
	}

	public int getNumInputs() {
		return inputs.length;
	}

	@Override
	public byte[] serialize() {
		int data=bitsToInts(inputs)[0];
		return new byte[] {(byte)data};
	}

	@Override
	public void deserialize(byte[] data) {
		boolean[] inputBits=intsToBits(new int[] {data[0]&0xFF});
		this.inputs=inputBits;
	}

	@Override
	public int hashCode() {
		final int prime=31;
		int result=1;
		result=prime*result+Arrays.hashCode(inputs);
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
		DigitalInputs other=(DigitalInputs)obj;
		if(!Arrays.equals(inputs,other.inputs)) {
			return false;
		}
		return true;
	}

}
