package org.anidev.frcds.pc.input.trans;

import java.util.Arrays;
import java.util.List;
import org.anidev.frcds.proto.torobot.Joystick;
import net.java.games.input.Component;
import net.java.games.input.Controller;

public abstract class TranslationProfile {
	protected Controller controller;

	public abstract int getNumSupportedAxes();
	
	public abstract int getNumSupportedButtons();

	public abstract float getRawAxis(int axis);

	public abstract boolean getButton(int button);

	public byte getAdjustedAxis(int axis) {
		float valf=getRawAxis(axis);
		byte valb=(byte)((valf+1.0f)*127.5f);
		return valb;
	}

	public void populateJoystick(Joystick joy) {
		for(int i=0;i<Joystick.NUM_AXES;i++) {
			joy.setAxis(i,getAdjustedAxis(i));
		}
		for(int i=0;i<Joystick.NUM_BUTTONS;i++) {
			joy.setButton(i,getButton(i));
		}
	}

	protected int determineSupportedAxes(Component.Identifier[] idents) {
		Component[] components=controller.getComponents();
		List<Component.Identifier> identList=Arrays.asList(idents);
		int total=0;
		for(Component component:components) {
			if(identList.contains(component.getIdentifier())) {
				total++;
			}
		}
		return total;
	}
}
