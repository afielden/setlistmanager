package com.bigdrum.setlistmanager.ui.setlistmanagement;

import android.os.Parcel;
import android.os.Parcelable;

public class Setlist implements Parcelable {
	private String name;
	private int position;
	private long id;
	
	
	/**
	 * 
	 */
	public static final Parcelable.Creator<Setlist> CREATOR = new Parcelable.Creator<Setlist>() {
		public Setlist createFromParcel(Parcel in) {
			return new Setlist(in);
		}

		public Setlist[] newArray(int size) {
			return new Setlist[size];
		}
	};
	
	
	/**
	 * 
	 * @param parcel
	 */
	public Setlist(Parcel parcel) {
		readFromParcel(parcel);
	}
	
	
	
	public Setlist(String name, int position, long id) {
		this.name = name;
		this.position = position;
		this.id = id;
	}
	
	
	/**
	 * 
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeInt(position);
		dest.writeLong(id);
	}
	
	/**
	 * 
	 * @param parcel
	 */
	public void readFromParcel(Parcel parcel) {
		name = parcel.readString();
		position = parcel.readInt();
		id = parcel.readLong();
	}

	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public long getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	/**
	 * 
	 */
	@Override
	public String toString() {
		return this.name;
	}
}
