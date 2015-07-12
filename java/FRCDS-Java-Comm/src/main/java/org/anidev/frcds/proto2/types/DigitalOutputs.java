package org.anidev.frcds.proto2.types;

import java.util.Arrays;

/**
 * Digital outputs on the robot
 */
public class DigitalOutputs {
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime=31;
		int result=1;
		result=prime*result+Arrays.hashCode(outputs);
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
		DigitalOutputs other=(DigitalOutputs)obj;
		if(!Arrays.equals(outputs,other.outputs)) {
			return false;
		}
		return true;
	}

}
