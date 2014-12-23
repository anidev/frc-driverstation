package org.anidev.frcds.pc.input;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import org.anidev.frcds.pc.input.trans.BasicTranslation;
import org.anidev.frcds.pc.input.trans.TranslationProfile;
import org.anidev.frcds.proto.torobot.Joystick;

/**
 * Device that gives input to the driver station
 */
public class InputDevice {
	private final Type type;
	private final Controller controller;
	private TranslationProfile trans;

	/**
	 * @param controller the controller to pass to the super constructor
	 */
	public InputDevice(Controller controller) {
		this(controller,null);
	}

	/**
	 * @param controller the controller that is being used for the driver station
	 * @param trans the translation profile for the input device
	 */
	public InputDevice(Controller controller,TranslationProfile trans) {
		this.controller=controller;
		this.trans=trans;
		this.type=determineType(controller);
		if(trans==null) {
			this.trans=getTranslationForType(this.controller,this.type);
		}
	}

	/**
	 * @return the type of input device
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the controller that is being used for the driver station
	 */
	public Controller getController() {
		return controller;
	}

	/**
	 * @return the translation profile for the input device
	 */
	public TranslationProfile getTranslation() {
		return trans;
	}
	
	/**
	 * @param joy the joystick to populate
	 */
	public void populateJoystick(Joystick joy) {
		trans.populateJoystick(joy);
	}

	/**
	 * @param controller the controller to determine the type of
	 * @return the type of the controller
	 */
	public static Type determineType(Controller controller) {
		if(controller.getType()==Controller.Type.STICK) {
			return Type.JOYSTICK;
		}
		Component[] components=controller.getComponents();
		int numAxes=0;
		for(Component component:components) {
			Component.Identifier id=component.getIdentifier();
			if((id instanceof Component.Identifier.Axis)
					&&id!=Component.Identifier.Axis.POV) {
				numAxes++;
			}
		}
		if(numAxes==4) {
			return Type.GAMEPAD4;
		} else if(numAxes==6) {
			return Type.GAMEPAD6;
		}
		return Type.UNKNOWN;
	}

	/**
	 * @param controller the controller to get a translation profile for
	 * @param type the Type
	 * @return a TranslationProfile for the controller
	 */
	public static TranslationProfile getTranslationForType(Controller controller,Type type) {
		return new BasicTranslation(controller);
	}
}
