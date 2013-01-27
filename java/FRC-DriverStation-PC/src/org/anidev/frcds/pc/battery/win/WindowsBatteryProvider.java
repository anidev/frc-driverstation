package org.anidev.frcds.pc.battery.win;

import org.anidev.frcds.common.types.BatteryProvider;
import com.sun.jna.Native;

public class WindowsBatteryProvider implements BatteryProvider {
	private Kernel32 kernel32=null;

	public WindowsBatteryProvider() {
		try {
			kernel32=(Kernel32)Native.loadLibrary("kernel32",Kernel32.class);
		} catch(UnsatisfiedLinkError e) {
			e.printStackTrace();
			System.err.println("Windows battery status will be unavailable.");
		}
	}

	@Override
	public double getBatteryPercent() {
		if(kernel32==null) {
			return -1;
		}
		PowerStatus status=new PowerStatus();
		kernel32.GetSystemPowerStatus(status);
		int flagByte=status.getBatteryFlag();
		int percentByte=status.getBatteryLifePercent();
		if(flagByte==128/*No battery*/||flagByte==255/*Unknown*/) {
			return -1;
		}
		if(percentByte==255/*Unknown*/) {
			return -1;
		}
		double percent=percentByte*1.0/100.0;
		return percent;
	}
}
