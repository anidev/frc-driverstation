package org.anidev.frcds.protoold.nc;

import java.util.Calendar;
import org.anidev.frcds.protoold.DataDir;

public class NetconsoleMessage {
	private final DataDir dir;
	private final String message;
	private final Calendar date;

	public NetconsoleMessage(DataDir dir,String message) {
		this.dir=dir;
		this.message=message;
		this.date=Calendar.getInstance();
		date.setTimeInMillis(System.currentTimeMillis());
	}
	public NetconsoleMessage(DataDir dir,String message,Calendar date) {
		this.dir=dir;
		this.message=message;
		this.date=date;
	}

	public DataDir getDirection() {
		return dir;
	}

	public String getMessage() {
		return message;
	}

	public Calendar getDate() {
		return date;
	}
}
