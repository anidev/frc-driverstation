package org.anidev.frcds.pc.battery.win;

import java.util.Arrays;
import java.util.List;
import com.sun.jna.Structure;

public class PowerStatus extends Structure {
	public final byte ACLineStatus;
	public final byte BatteryFlag;
	public final byte BatteryLifePercent;
	public final byte Reserved1;
	public final int BatteryLifeTime;
	public final int BatteryFullLifeTime;
	private List<String> fieldOrder=null;
	{
		ACLineStatus=0;
		BatteryFlag=0;
		BatteryLifePercent=0;
		Reserved1=0;
		BatteryLifeTime=0;
		BatteryFullLifeTime=0;
	}

	public PowerStatus() {
	}

	@Override
	protected List<String> getFieldOrder() {
		if(fieldOrder==null) {
			String[] fields=new String[] {"ACLineStatus","BatteryFlag",
					"BatteryLifePercent","Reserved1","BatteryLifeTime",
					"BatteryFullLifeTime"};
			fieldOrder=Arrays.asList(fields);
		}
		return fieldOrder;
	}

	public int getACLineStatus() {
		return ACLineStatus&0xFF;
	}

	public int getBatteryFlag() {
		return BatteryFlag&0xFF;
	}

	public int getBatteryLifePercent() {
		return BatteryLifePercent&0xFF;
	}

	public int getReserved1() {
		return Reserved1&0xFF;
	}

	public long getBatteryLifeTime() {
		return BatteryLifeTime&(1l<<32);
	}

	public long getBatteryFullLifeTime() {
		return BatteryFullLifeTime&(1l<<32);
	}

}
