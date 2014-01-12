package org.anidev.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

public class Utils {
	public static final String TMPDIR;
	static {
		String separator=System.getProperty("file.separator");
		StringBuilder tmpPathBuilder=new StringBuilder();
		tmpPathBuilder.append(System.getProperty("java.io.tmpdir"));
		tmpPathBuilder.append(separator);
		tmpPathBuilder.append("FRCDS-files");
		tmpPathBuilder.append(separator);
		TMPDIR=tmpPathBuilder.toString();
	}

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

	public static Icon getIcon(String name) {
		Icon icon=GTKIconTheme.findIcon(name);
		if(icon!=null) {
			return icon;
		}
		try {
			URL imageUrl=Utils.class.getResource("/"+name);
			if(imageUrl==null) {
				return null;
			}
			Image image=ImageIO.read(imageUrl);
			icon=new ImageIcon(image);
			return icon;
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Preferences getPrefs(Class<?> cls) {
		return Preferences.userNodeForPackage(cls);
	}

	public static Object getNimbusPref(String key,JComponent c) {
		UIDefaults uiValues=UIManager.getLookAndFeelDefaults();
		Object overrides=c.getClientProperty("Nimbus.Overrides");
		Object pref=null;
		if(overrides!=null&&overrides instanceof UIDefaults) {
			pref=((UIDefaults)overrides).get(key);
		}
		if(pref==null) {
			pref=uiValues.get(key);
		}
		return pref;
	}

	public static void setLookAndFeel() {
		String lafStr=System.getProperty("anidev.pcds.laf","<nimbus>");
		switch(lafStr) {
		case "<nimbus>":
			lafStr="com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
			break;
		case "<system>":
			lafStr=UIManager.getSystemLookAndFeelClassName();
			break;
		case "<cross>":
			lafStr=UIManager.getCrossPlatformLookAndFeelClassName();
			break;
		}
		try {
			UIManager.setLookAndFeel(lafStr);
		} catch(Exception e) {
			System.err.println("Error while setting "+lafStr+" L&F.");
			e.printStackTrace();
		}
	}

	public static Font makeMonoFont(Font oldFont) {
		return new Font(Font.MONOSPACED,oldFont.getStyle(),oldFont.getSize());
	}

	public static File extractJarResource(String fileName) throws IOException {
		File file=new File(TMPDIR+fileName);
		file.getParentFile().mkdirs();
		if(file.exists()) {
			return file;
		}
		InputStream fileStream=Utils.class.getResourceAsStream("/"+fileName);
		System.out.println(fileName);
		if(fileStream==null) {
			return null;
		}
		OutputStream outStream=null;
		try {
			outStream=new FileOutputStream(file);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			fileStream.close();
			return null;
		}
		System.out.println("Extracting "+fileName+"...");
		int c=0;
		byte[] buf=new byte[256];
		try {
			while(true) {
				c=fileStream.read(buf);
				outStream.write(buf);
				if(c<buf.length) {
					break;
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			fileStream.close();
			outStream.close();
		}
		return file;
	}
}
