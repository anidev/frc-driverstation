package org.anidev.frcds.proto2.types;

import java.util.Calendar;

/**
 * Represents the protocol version in terms of year, day, month, and revision.
 * TODO Rewrite this using the new Java 8 date and time API
 * 
 * @author Anirudh Bagde
 */
public class Version {
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

	public Version(int year,int day,int month,int revision) {
		this.year=year;
		this.day=day;
		this.month=month;
		this.revision=revision;
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
