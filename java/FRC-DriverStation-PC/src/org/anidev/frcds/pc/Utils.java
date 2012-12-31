package org.anidev.frcds.pc;

import java.awt.Color;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

public class Utils {
	public static int calcAlpha(double alpha,int newColor,int oldColor) {
		return (int)Math.round(alpha*newColor+(1-alpha)*oldColor);
	}
	public static Color calcAlpha(double alpha,Color newColor,Color oldColor) {
		int calcR=calcAlpha(alpha,newColor.getRed(),oldColor.getRed());
		int calcG=calcAlpha(alpha,newColor.getGreen(),oldColor.getGreen());
		int calcB=calcAlpha(alpha,newColor.getBlue(),oldColor.getBlue());
		return new Color(calcR,calcG,calcB);
	}
	public static AbstractButton getSelectedButton(ButtonGroup group) {
		Enumeration<AbstractButton> buttons=group.getElements();
		while(buttons.hasMoreElements()) {
			AbstractButton button=buttons.nextElement();
			if(button.isSelected()) {
				return button;
			}
		}
		return null;
	}
}
