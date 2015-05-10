package org.anidev.frcds.pc.nc;

import javax.swing.JFrame;
import org.anidev.frcds.protoold.nc.Netconsole;
import org.anidev.utils.Utils;

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
