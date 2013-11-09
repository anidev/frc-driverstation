package org.anidev.frcds.pc;

import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;

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
	public static ImageIcon getIcon(String name) {
		try {
			URL imageUrl=Utils.class.getResource("/resources/"+name);
			Image image=ImageIO.read(imageUrl);
			ImageIcon icon=new ImageIcon(image);
			return icon;
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static Preferences getPrefs() {
		return Preferences.userNodeForPackage(PCDriverStation.class);
	}
}
