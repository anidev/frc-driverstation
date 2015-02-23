package org.anidev.frcds.protoold.torobot;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import org.anidev.frcds.protoold.CommData;

public class AnalogInputs extends CommData {
	public static final int SIZE=8;
	private int[] inputs=new int[] {0,0,0,0};

	public int getInput(int index) {
		return inputs[index];
	}
	
	public void setInput(int index,int value) {
		inputs[index]=value;
	}
	
	public int getNumInputs() {
		return inputs.length;
	}
	
	@Override
	public byte[] serialize() {
		ByteBuffer buffer=ByteBuffer.allocate(inputs.length*2);
		buffer.order(BYTE_ORDER);
		for(int i=0;i<inputs.length;i++) {
			buffer.putShort((short)inputs[i]);
		}
		return buffer.array();
	}

	@Override
	public void deserialize(byte[] data) {
		ByteBuffer byteBuffer=ByteBuffer.wrap(data);
		byteBuffer.order(BYTE_ORDER);
		ShortBuffer shortBuffer=byteBuffer.asShortBuffer();
		int[] inputs=new int[data.length/2];
		for(int i=0;i<inputs.length;i++) {
			inputs[i]=shortBuffer.get(i);
		}
		this.inputs=inputs;
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
		AnalogInputs other=(AnalogInputs)obj;
		if(!Arrays.equals(inputs,other.inputs)) {
			return false;
		}
		return true;
	}

}
