package com.bigdrum.setlistmanager.gig;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import com.bigdrum.setlistmanager.R;
import com.bigdrum.setlistmanager.database.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.prefs.Preferences;

public class Gig implements Parcelable {
	
	private String name;
	private long id;
	private long venueId;
	private long setlistId;
	private long dateInMilliseconds;
    private Calendar cal;
    private String dateFormat;
	
	
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
     * @param name
     * @param venueId
     * @param dateInMilliseconds
     * @param setlistId
     */
    public Gig(long id, String name, long venueId, long dateInMilliseconds, long setlistId, Context context) {

        this.id = id;
        this.name = name;
        this.venueId = venueId;
        this.dateInMilliseconds = dateInMilliseconds;
        this.setlistId = setlistId;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        dateFormat = prefs.getString(Constants.prefs_date_format, context.getResources().getString(R.string.pref_default_date_format));
    }

	
	/**
	 * 
	 * @param id
	 * @param venueId
	 * @param setlistId
	 */
	public Gig(long id, String name, long venueId, long setlistId, String date, String time, Context context) {
		this.name = name;
		this.id = id;
		this.venueId = venueId;
		this.setlistId = setlistId;

        createDateInMilliseconds(date, time);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        dateFormat = prefs.getString(Constants.prefs_date_format, context.getResources().getString(R.string.pref_default_date_format));
	}


    /**
     *
     *
     */
    private void createDateInMilliseconds(String date, String time) {

        cal = Calendar.getInstance(Locale.getDefault());

        String[] strArray = date.split("/");
        cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(strArray[0]));
        cal.set(Calendar.MONTH, Integer.valueOf(strArray[1]) - 1);
        cal.set(Calendar.YEAR, Integer.valueOf(strArray[2]));

        strArray = time.split(":");
        cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(strArray[0]));
        cal.set(Calendar.MINUTE, Integer.valueOf(strArray[1]));
        cal.set(Calendar.SECOND, 0);

        dateInMilliseconds = cal.getTimeInMillis();
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
		dest.writeLong(dateInMilliseconds);
        dest.writeString(dateFormat);
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
		dateInMilliseconds = parcel.readLong();
        dateFormat = parcel.readString();
	}

	
	
	public String toString() {

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());

        String date = sdf.format(getCal().getTime());

		return String.format("%1$-20s %2$20s", name, date);
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

    public long getDateInMilliseconds() {
        return dateInMilliseconds;
    }


	public String getDate() {

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


//	public void setDate(String date) {
//		this.date = date;
//	}


	public String getName() {
		return name;
	}


	public void setVenueName(String name) {
		this.name = name;
	}


    /**
     *
     * @return
     */
//    public String getDateFormattedForDisplay() {
//
//        StringBuilder dateStrbuilder = new StringBuilder();
//
//        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
//
//        String newDate = sdf.format(cal.getTime());
//        dateStrbuilder = new StringBuilder();
//        dateStrbuilder.append(newDate).append("-").append(getTime());
//
//        return dateStrbuilder.toString();
//    }



    /**
     *
     * @return
     */
    private Calendar getCal() {

        if (cal == null) {
            cal = Calendar.getInstance(Locale.getDefault());
            cal.setTimeInMillis(dateInMilliseconds);
        }

        return cal;
    }
}
