package org.anidev.frcds.protoold;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;

// Format is YYDDMMRR where RR is revision
// Use java.text.DateFormatSymbols for formatting
public class Version extends CommData {
	public static final int SIZE=8;
	private int year;
	private int day;
	private int month;
	private int revision;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year=year;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day=day;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month=month;
	}

	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision=revision;
	}
	
	public Version() {
		Calendar calendar=Calendar.getInstance();
		year=calendar.get(Calendar.YEAR);
		month=calendar.get(Calendar.MONTH)+1;
		day=calendar.get(Calendar.DAY_OF_MONTH);
		revision=0;
	}

	public Version(String versionStr) {
		deserialize(versionStr.getBytes());
	}

	public Version(int year,int day,int month,int revision) {
		this.year=year;
		this.day=day;
		this.month=month;
		this.revision=revision;
	}

	@Override
	public byte[] serialize() {
		Calendar calendar=new GregorianCalendar(year,month-1,day);
		Formatter formatter=new Formatter();
		formatter.format("%1$ty%1$td%1$tm%2$02d",calendar,revision);
		String str=formatter.toString();
		formatter.close();
		return str.getBytes();
	}

	@Override
	public void deserialize(byte[] data) {
		String str=new String(data);
		Calendar calendar=null;
		try {
			Date date=new SimpleDateFormat("yyddMM").parse(str.substring(0,6));
			calendar=new GregorianCalendar();
			calendar.setTime(date);
		} catch(ParseException e) {
			e.printStackTrace();
			calendar=Calendar.getInstance();
		}
		year=calendar.get(Calendar.YEAR);
		month=calendar.get(Calendar.MONTH)+1;
		day=calendar.get(Calendar.DAY_OF_MONTH);
		revision=Integer.parseInt(str.substring(6,8));
	}

	@Override
	public int hashCode() {
		final int prime=31;
		int result=1;
		result=prime*result+day;
		result=prime*result+month;
		result=prime*result+revision;
		result=prime*result+year;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this==obj) {
			return true;
		}
		if(obj==null) {
			return false;
		}
		if(getClass()!=obj.getClass()) {
			return false;
		}
		Version other=(Version)obj;
		if(day!=other.day) {
			return false;
		}
		if(month!=other.month) {
			return false;
		}
		if(revision!=other.revision) {
			return false;
		}
		if(year!=other.year) {
			return false;
		}
		return true;
	}

}
