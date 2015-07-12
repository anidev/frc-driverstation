package org.anidev.frcds.proto2.types;

import java.util.Arrays;

public class Joystick {
	public static final int NUM_AXES=6;
	public static final int NUM_BUTTONS=12;

	double[] axes=new double[NUM_AXES];
	boolean[] buttons=new boolean[NUM_BUTTONS];

	public Joystick() {
		Arrays.fill(axes,0.0);
		Arrays.fill(buttons,false);
	}

	public double[] getAxes() {
		return axes;
	}

	public void setAxes(double[] axes) {
		this.axes=Arrays.copyOf(axes,axes.length);
	}

	public boolean[] getButtons() {
		return buttons;
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

	public void setButtons(boolean[] buttons) {
		this.buttons=Arrays.copyOf(buttons,buttons.length);
	}
}
