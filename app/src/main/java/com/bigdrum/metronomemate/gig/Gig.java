package com.bigdrum.metronomemate.gig;

import android.os.Parcel;
import android.os.Parcelable;

public class Gig implements Parcelable {
	
	private String name;
	private long id;
	private long venueId;
	private long setlistId;
	private String date;
	
	
	/**
	 * 
	 */
	public static final Creator<Gig> CREATOR = new Creator<Gig>() {
		public Gig createFromParcel(Parcel in) {
			return new Gig(in);
		}

		public Gig[] newArray(int size) {
			return new Gig[size];
		}
	};
	
	
	/**
	 * 
	 * @param parcel
	 */
	public Gig(Parcel parcel) {
		readFromParcel(parcel);
	}
	
	
	/**
	 * 
	 * @param id
	 * @param venueId
	 * @param setlistId
	 * @param date
	 */
	public Gig(long id, String name, long venueId, long setlistId, String date) {
		this.name = name;
		this.id = id;
		this.venueId = venueId;
		this.setlistId = setlistId;
		this.date = date;
	}
	
	

	/**
	 * 
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	/**
	 * 
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeLong(id);
		dest.writeLong(venueId);
		dest.writeLong(setlistId);
		dest.writeString(date);
	}
	
	
	/**
	 * 
	 * @param parcel
	 */
	public void readFromParcel(Parcel parcel) {
		name = parcel.readString();
		id = parcel.readLong();
		venueId = parcel.readLong();
		setlistId = parcel.readLong();
		date = parcel.readString();
	}
	
	
	public String toString() {
		return name;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public long getVenueId() {
		return venueId;
	}


	public void setVenueId(long venueId) {
		this.venueId = venueId;
	}


	public long getSetlistId() {
		return setlistId;
	}


	public void setSetlistId(long setlistId) {
		this.setlistId = setlistId;
	}


	public String getDate() {
		if (date.length() > 0) {
			return date.substring(0, date.indexOf('-'));
		}
		
		return "";
	}
	
	public String getTime() {
		if (date.length() > 0) {
			return date.substring(date.indexOf('-')+1);
		}
		
		return "";
	}
	
	public String getDateTime() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public String getName() {
		return name;
	}


	public void setVenueName(String name) {
		this.name = name;
	}
	

}
