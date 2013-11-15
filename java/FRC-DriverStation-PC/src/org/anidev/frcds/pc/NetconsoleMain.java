package org.anidev.frcds.pc;

import javax.swing.JFrame;
import javax.swing.UIManager;
import org.anidev.frcds.pc.gui.nc.NetconsoleFrame;
import org.anidev.frcds.proto.nc.Netconsole;

public class NetconsoleMain {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch(Exception e) {
			System.err.println("Error while setting Nimbus L&F.");
			e.printStackTrace();
		}
		Netconsole netconsole=new Netconsole();
		NetconsoleFrame frame=new NetconsoleFrame(netconsole);
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
