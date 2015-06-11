package com.bigdrum.setlistmanager.venue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import com.bigdrum.setlistmanager.R;
import com.bigdrum.setlistmanager.database.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
	private long lastGigDate;
    private String dateFormat;
    private Calendar cal;
	
	
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
			String contactName, String phone, String email, long lastGigDate, Context context) {
		this.id = id;
		this.name = name;
		this.street = street;
		this.town = town;
		this.postcode = postcode;
		this.country = country;
		this.contactName = contactName;
		this.phone = phone;
		this.email = email;
		this.lastGigDate = lastGigDate;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        dateFormat = prefs.getString(Constants.prefs_date_format, context.getResources().getString(R.string.pref_default_date_format));
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
		dest.writeLong(lastGigDate);
        dest.writeString(dateFormat);
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
		lastGigDate = parcel.readLong();
        dateFormat = parcel.readString();
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


	public long getLastGigDate() {
		return lastGigDate;
	}


	public void setLastGigDate(long lastGigDate) {
		this.lastGigDate = lastGigDate;
	}


	public String getDate() {

        if (lastGigDate == 0) {
            return "";
        }

		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());

		return sdf.format(getCal().getTime());
	}

	public String getTime() {

		return getCal().get(Calendar.HOUR_OF_DAY) + ":" + getCal().get(Calendar.MINUTE) + ":"
				+ getCal().get(Calendar.SECOND);
	}

	public String getDateTime() {

		return getDate() + "-" +
				getCal().get(Calendar.HOUR_OF_DAY) + ":" + getCal().get(Calendar.MINUTE) + ":"
				+ getCal().get(Calendar.SECOND);
	}

    /**
     *
     * @return
     */
    private Calendar getCal() {

        if (cal == null) {
            cal = Calendar.getInstance(Locale.getDefault());
            cal.setTimeInMillis(lastGigDate);
        }

        return cal;
    }
}
