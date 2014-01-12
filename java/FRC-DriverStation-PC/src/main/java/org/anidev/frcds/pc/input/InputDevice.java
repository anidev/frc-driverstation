package org.anidev.frcds.pc.input;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import org.anidev.frcds.pc.input.trans.BasicTranslation;
import org.anidev.frcds.pc.input.trans.TranslationProfile;
import org.anidev.frcds.proto.torobot.Joystick;

public class InputDevice {
	private final Type type;
	private final Controller controller;
	private TranslationProfile trans;

	public InputDevice(Controller controller) {
		this(controller,null);
	}

	public InputDevice(Controller controller,TranslationProfile trans) {
		this.controller=controller;
		this.trans=trans;
		this.type=determineType(controller);
		if(trans==null) {
			this.trans=getTranslationForType(this.controller,this.type);
		}
		System.out.println(this.type+"\n");
	}

	public Type getType() {
		return type;
	}

	public Controller getController() {
		return controller;
	}

	public TranslationProfile getTranslation() {
		return trans;
	}
	
	public void populateJoystick(Joystick joy) {
		trans.populateJoystick(joy);
	}

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

	public static TranslationProfile getTranslationForType(Controller controller,Type type) {
		return new BasicTranslation(controller);
	}
}
