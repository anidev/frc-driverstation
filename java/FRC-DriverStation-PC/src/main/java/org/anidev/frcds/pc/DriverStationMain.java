package org.anidev.frcds.pc;

import java.io.IOException;
import java.lang.reflect.Field;
import javax.swing.JFrame;
import org.anidev.frcds.common.types.BatteryProvider;
import org.anidev.frcds.pc.battery.linux.LinuxBatteryProvider;
import org.anidev.frcds.pc.battery.win.WindowsBatteryProvider;
import org.anidev.frcds.pc.gui.DriverStationFrame;
import org.anidev.frcds.proto.nc.Netconsole;
import org.anidev.utils.Utils;

/**
 * main-class for PC driver station
 */
public class DriverStationMain {
	private static DriverStationFrame dsFrame;
	private static PCDriverStation ds;
	private static Netconsole nc;

	public static void main(String[] args) {
		// Extract JInput natives if running from jar
		// Will gracefully fail if not being run from jar
		extractJInputNatives();
		Utils.setLookAndFeel();
		ds=new PCDriverStation();
		nc=new Netconsole();
		dsFrame=new DriverStationFrame();
		dsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dsFrame.setVisible(true);
		ds.setFrame(dsFrame);
		initBatteryProvider();
	}

	/**
	 * @return the frame
	 */
	public static DriverStationFrame getFrame() {
		return dsFrame;
	}

	/**
	 * @return the PCDriverStation instance
	 */
	public static PCDriverStation getDS() {
		return ds;
	}

	/**
	 * @return the Netconsole instance
	 */
	public static Netconsole getNetconsole() {
		return nc;
	}

	/**
	 * initialize the battery provider for respective operating systems
	 */
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

	/**
	 * Extract the jinput native files so they can be used.
	 */
	private static void extractJInputNatives() {
		String os=System.getProperty("os.name").toLowerCase();
		String libPath=System.getProperty("java.library.path");
		String[] libNames;
		if(os.startsWith("linux")) {
			libNames=new String[] {"libjinput-linux.so","libjinput-linux64.so"};
		} else if(os.startsWith("mac")) {
			libNames=new String[] {"libjinput-osx.jnilib"};
		} else if(os.startsWith("windows")) {
			libNames=new String[] {"jinput-dx8.dll","jinput-dx8_64.dll",
					"jinput-raw.dll","jinput-raw_64.dll","jinput-wintab.dll"};
		} else {
			libNames=new String[0];
		}
		try {
			for(String libName:libNames) {
				Utils.extractJarResource(libName);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		String separator=System.getProperty("path.separator");
		System.setProperty("java.library.path",libPath+separator+Utils.TMPDIR);
		try {
			Field sysPathField=ClassLoader.class.getDeclaredField("sys_paths");
			sysPathField.setAccessible(true);
			sysPathField.set(null,null);
		} catch(NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
