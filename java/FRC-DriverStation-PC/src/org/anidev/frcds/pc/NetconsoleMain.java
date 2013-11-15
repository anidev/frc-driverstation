package org.anidev.frcds.pc;

import javax.swing.JFrame;
import org.anidev.frcds.pc.gui.nc.NetconsoleFrame;
import org.anidev.frcds.proto.nc.Netconsole;

public class NetconsoleMain {
	public static void main(String[] args) {
		Utils.setLookAndFeel();
		Netconsole netconsole=new Netconsole();
		NetconsoleFrame frame=new NetconsoleFrame(netconsole);
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
