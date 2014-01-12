package org.anidev.frcds.pc.input.trans;

import java.util.ArrayList;
import net.java.games.input.Component;
import net.java.games.input.Controller;

/**
 * @formatter:off
 * Translation for stuff common to all input types.
 * Specifically translates:
 *  - Axes 1,2,3 (which are X,Y,RZ)
 *  TODO testing to figure this translation stuff out
 */
// @formatter:on

// Actually, for now just present axes in the
// order in which they appear?
public class BasicTranslation extends TranslationProfile {
	protected Component[] axes;
	protected Component[] buttons;

	public BasicTranslation(Controller controller) {
		this.controller=controller;
		Component[] components=controller.getComponents();
		ArrayList<Component> axesList=new ArrayList<>();
		ArrayList<Component> buttonList=new ArrayList<>();
		for(Component component:components) {
			Component.Identifier ident=component.getIdentifier();
			if(ident instanceof Component.Identifier.Axis) {
				axesList.add(component);
			} else {
				buttonList.add(component);
			}
		}
		this.axes=axesList.toArray(new Component[axesList.size()]);
		this.buttons=buttonList.toArray(new Component[buttonList.size()]);
	}

	@Override
	public float getRawAxis(int axis) {
		if(axis>=getNumSupportedAxes()) {
			return 0.0f;
		}
		return axes[axis].getPollData();
	}

	@Override
	public boolean getButton(int button) {
		if(button>=getNumSupportedButtons()) {
			return false;
		}
		return buttons[button].getPollData()>0.5f;
	}

	@Override
	public int getNumSupportedAxes() {
		return axes.length;
	}

	@Override
	public int getNumSupportedButtons() {
		// TODO Auto-generated method stub
		return buttons.length;
	}
}
