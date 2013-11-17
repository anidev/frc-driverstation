package org.anidev.frcds.pc;

import javax.swing.JFrame;
import org.anidev.frcds.common.types.BatteryProvider;
import org.anidev.frcds.pc.battery.linux.LinuxBatteryProvider;
import org.anidev.frcds.pc.battery.win.WindowsBatteryProvider;
import org.anidev.frcds.pc.gui.DriverStationFrame;
import org.anidev.frcds.proto.nc.Netconsole;
import org.anidev.utils.Utils;

public class DriverStationMain {
	private static DriverStationFrame dsFrame;
	private static PCDriverStation ds;
	private static Netconsole nc;

	public static void main(String[] args) {
		Utils.setLookAndFeel();
		ds=new PCDriverStation();
		nc=new Netconsole();
		dsFrame=new DriverStationFrame();
		dsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dsFrame.setVisible(true);
		ds.setFrame(dsFrame);
		initBatteryProvider();
	}
	
	public static DriverStationFrame getFrame() {
		return dsFrame;
	}
	
	public static PCDriverStation getDS() {
		return ds;
	}
	
	public static Netconsole getNetconsole() {
		return nc;
	}
	
	private static void initBatteryProvider() {
		String os=System.getProperty("os.name");
		BatteryProvider provider=null;
		if(os==null) {
			return;
		}
		os=os.toLowerCase();
		if(os.equals("linux")) {
			provider=new LinuxBatteryProvider();
		} else if(os.contains("windows")) {
			provider=new WindowsBatteryProvider();
		}
		ds.setBatteryProvider(provider);
	}
}
