package org.anidev.frcds.pc.input.trans;

import java.util.Arrays;
import java.util.List;
import org.anidev.frcds.protoold.torobot.Joystick;
import net.java.games.input.Component;
import net.java.games.input.Controller;

/**
 * A translation for a controller
 */
public abstract class TranslationProfile {
	protected Controller controller;

	/**
	 * @return the number of supported axes on the controller
	 */
	public abstract int getNumSupportedAxes();
	
	/**
	 * @return the number of supported button on the controller
	 */
	public abstract int getNumSupportedButtons();

	/**
	 * @param axis the axis to get a value from
	 * @return the value of the given axis
	 */
	public abstract float getRawAxis(int axis);

	/**
	 * @param button the button to get a value from
	 * @return a boolean value representing if the button is pressed or not
	 */
	public abstract boolean getButton(int button);

	/**
	 * @param axis the axis to get an adjusted value for
	 * @return an adjusted value of that axis
	 */
	public byte getAdjustedAxis(int axis) {
		float valf=getRawAxis(axis);
		byte valb=(byte)((valf+1.0f)*127.5f);
		return valb;
	}

	/**
	 * Populate the joystick's buttons and axes
	 * @param joy the joystick to populate
	 */
	public void populateJoystick(Joystick joy) {
		for(int i=0;i<Joystick.NUM_AXES;i++) {
			joy.setAxis(i,getAdjustedAxis(i));
		}
		for(int i=0;i<Joystick.NUM_BUTTONS;i++) {
			joy.setButton(i,getButton(i));
		}
	}

	/**
	 * @param idents the component identifiers
	 * @return the number of supported axes
	 */
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
