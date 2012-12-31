package org.anidev.frcds.proto.tods;

import java.util.Arrays;
import org.anidev.frcds.proto.CommData;

public class DigitalOutputs extends CommData {
	public static final int SIZE=1;
	private boolean[] outputs=new boolean[] {false,false,false,false,false,
			false,false,false};
	
	public boolean getInput(int index) {
		return outputs[index];
	}
	
	public void setInput(int index,boolean value) {
		outputs[index]=value;
	}
	
	public int getNumInputs() {
		return outputs.length;
	}

	@Override
	public byte[] serialize() {
		int data=bitsToInts(outputs)[0];
		return new byte[] {(byte)data};
	}

	@Override
	public void deserialize(byte[] data) {
		boolean[] inputBits=intsToBits(new int[] {data[0]&0xFF});
		this.outputs=inputBits;
	}

	@Override
	public int hashCode() {
		final int prime=31;
		int result=1;
		result=prime*result+Arrays.hashCode(outputs);
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
		DigitalOutputs other=(DigitalOutputs)obj;
		if(!Arrays.equals(outputs,other.outputs)) {
			return false;
		}
		return true;
	}

}
