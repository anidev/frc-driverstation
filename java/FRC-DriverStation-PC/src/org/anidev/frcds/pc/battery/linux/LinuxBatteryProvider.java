package org.anidev.frcds.pc.battery.linux;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.anidev.frcds.common.types.BatteryProvider;

public class LinuxBatteryProvider implements BatteryProvider {
	private static final File BATTERY_ROOT=new File("/sys/class/power_supply");
	private File battery=null;

	public LinuxBatteryProvider() {
		scanBatteries();
	}

	@Override
	public double getBatteryPercent() {
		if(battery==null) {
			scanBatteries();
			return -1.0;
		}
		File fullFile=new File(battery.getAbsolutePath()+"/charge_full");
		File nowFile=new File(battery.getAbsolutePath()+"/charge_now");
		int fullCharge=0;
		int nowCharge=0;
		try {
			fullCharge=new Scanner(fullFile).nextInt();
			nowCharge=new Scanner(nowFile).nextInt();
		} catch(FileNotFoundException e) {
			scanBatteries();
			return -1.0;
		} catch(Exception e) {
			return -1.0;
		}
		double batteryPercent=nowCharge*1.0/fullCharge;
		return batteryPercent;
	}
	
	private void scanBatteries() {
		this.battery=null;
		if(!BATTERY_ROOT.exists()) {
			return;
		}
		File[] batteryFiles=BATTERY_ROOT.listFiles();
		if(batteryFiles==null) {
			return;
		}
		for(File battery:batteryFiles) {
			File fullFile=new File(battery.getAbsolutePath()+"/charge_full");
			File nowFile=new File(battery.getAbsolutePath()+"/charge_now");
			if(!fullFile.exists()||!nowFile.exists()) {
				continue;
			}
			this.battery=battery;
			break;
		}
	}
}
