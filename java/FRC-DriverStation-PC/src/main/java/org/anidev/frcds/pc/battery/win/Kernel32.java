package org.anidev.frcds.pc.battery.win;

import com.sun.jna.Library;

public interface Kernel32 extends Library {
	public boolean GetSystemPowerStatus(PowerStatus status);
}
