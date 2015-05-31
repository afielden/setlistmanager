package com.bigdrum.setlistmanager.venue;

import android.os.Parcel;
import android.os.Parcelable;

public class Venue implements Parcelable {
	
	private long id;
	private String name;
	private String street;
	private String town;
	private String postcode;
	private String country;
	private String contactName;
	private String phone;
	private String email;
	private String lastGigDate = "";
	
	
	public static final Creator<Venue> CREATOR = new Creator<Venue>() {
		public Venue createFromParcel(Parcel in) {
			return new Venue(in);
		}

		public Venue[] newArray(int size) {
			return new Venue[size];
		}
	};
	
	
	/**
	 * 
	 * @param parcel
	 */
	public Venue(Parcel parcel) {
		readFromParcel(parcel);
	}
	
	
	public Venue(long id, String name, String street, String town, String postcode, String country,
			String contactName, String phone, String email, String lastGigDate) {
		this.id = id;
		this.name = name;
		this.street = street;
		this.town = town;
		this.postcode = postcode;
		this.country = country;
		this.contactName = contactName;
		this.phone = phone;
		this.email = email;
		this.lastGigDate = lastGigDate==null?"":lastGigDate;
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
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeString(street);
		dest.writeString(town);
		dest.writeString(postcode);
		dest.writeString(country);
		dest.writeString(contactName);
		dest.writeString(phone);
		dest.writeString(email);
		dest.writeString(lastGigDate);
	}
	
	/**
	 * 
	 * @param parcel
	 */
	public void readFromParcel(Parcel parcel) {
		id = parcel.readLong();
		name = parcel.readString();
		street = parcel.readString();
		town = parcel.readString();
		postcode = parcel.readString();
		country = parcel.readString();
		contactName = parcel.readString();
		phone = parcel.readString();
		email = parcel.readString();
		lastGigDate = parcel.readString();
	}

	
	@Override
	public String toString() {
		return name;
	}
	

	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getStreet() {
		return street;
	}


	public void setStreet(String street) {
		this.street = street;
	}


	public String getTown() {
		return town;
	}


	public void setTown(String town) {
		this.town = town;
	}


	public String getPostcode() {
		return postcode;
	}


	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public String getContactName() {
		return contactName;
	}


	public void setContactName(String contactName) {
		this.contactName = contactName;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getLastGigDate() {
		return lastGigDate;
	}


	public void setLastGigDate(String lastGigDate) {
		this.lastGigDate = lastGigDate;
	}
}
