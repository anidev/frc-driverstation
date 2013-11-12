package org.anidev.frcds.proto.nc;

import java.util.Calendar;

public class NetconsoleMessage {
	private final Type type;
	private final String message;
	private final Calendar date;

	public NetconsoleMessage(Type type,String message) {
		this.type=type;
		this.message=message;
		this.date=Calendar.getInstance();
		date.setTimeInMillis(System.currentTimeMillis());
	}
	public NetconsoleMessage(Type type,String message,Calendar date) {
		this.type=type;
		this.message=message;
		this.date=date;
	}

	public Type getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	public Calendar getDate() {
		return date;
	}

	public enum Type {
		TODS,TOROBOT;
	}
}
