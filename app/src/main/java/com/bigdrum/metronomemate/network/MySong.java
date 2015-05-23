package com.bigdrum.metronomemate.network;

import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;

import com.bigdrum.metronomemate.ui.setlistmanagement.SongDetailsFragment;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Song;

public class MySong implements Parcelable {

	private Song song;
	private long id;
	private String artist;
	private String songTitle;
	private double tempo;
	private int timeSignature;
	private int key;
	private int setlistCount;
	private int position;
	private TypedArray keyNames;
	private double duration;

	
	public static final Parcelable.Creator<MySong> CREATOR = new Parcelable.Creator<MySong>() {
		public MySong createFromParcel(Parcel in) {
			return new MySong(in);
		}

		public MySong[] newArray(int size) {
			return new MySong[size];
		}
	};
	
	
	/**
	 * 
	 * @param parcel
	 */
	public MySong(Parcel parcel) {
		readFromParcel(parcel);
	}

	/**
	 * 
	 * @param song
	 * @throws EchoNestException
	 */
	public MySong(Song song, TypedArray keyNames) throws EchoNestException {
		this.id = -1;
		this.song = song;
		this.artist = song.getArtistName();
		this.songTitle = song.getTitle();
		this.tempo = song.getTempo();
		this.timeSignature = song.getTimeSignature();
		this.key = song.getKey();
		this.keyNames = keyNames;
		this.duration = song.getDuration();
	}
	
	
	public MySong(long id, String songTitle, String artist, double tempo, int timeSignature, int key, int setlistCount, int position, double duration) {
		this.id = id;
		this.songTitle = songTitle;
		this.artist = artist;
		this.tempo = tempo;
		this.timeSignature = timeSignature;
		this.key = key;
		this.setlistCount = setlistCount;
		this.position = position;
        this.duration = duration;
	}


	/**
	 * 
	 * @return
	 */
	public Song getSong() {
		return song;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getSongTitle() {
		return songTitle;
	}

	public void setSongTitle(String songTitle) {
		this.songTitle = songTitle;
	}

	public double getTempo() {
		return tempo;
	}

	public void setTempo(double tempo) {
		this.tempo = tempo;
	}

	public int getTimeSignature() {
		return timeSignature;
	}

	public void setTimeSignature(int timeSignature) {
		this.timeSignature = timeSignature;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public int getSetlistCount() {
		return setlistCount;
	}

	public void setSetlistCount(int setlistCount) {
		this.setlistCount = setlistCount;
	}

	public int getPosition() {
		return position;
	}

	public double getDuration() { return duration; }

	public void setKeyNames(TypedArray keyNames) { this.keyNames = keyNames; }

	/**
	 * 
	 */
	@Override
	public String toString() {
		StringBuilder songInfo = new StringBuilder();
		songInfo.append("Artist: " + artist)
				.append("\n")
				.append("Song title: " + songTitle)
				.append("\n")
				.append("Tempo: " + Double.valueOf(tempo))
				.append("\n")
				.append("Time signature: "
						+ Integer.valueOf(timeSignature))
				.append("\n")
				.append("Key: " + keyNames.getString(key))
				.append("\n")
				.append("Duration: " + SongDetailsFragment.formatTime(duration))
				.append("\n\n");

		return songInfo.toString();
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
	 * @param parcel
	 */
	public void readFromParcel(Parcel parcel) {
		id = parcel.readLong();
		artist = parcel.readString();
		songTitle = parcel.readString();
		tempo = parcel.readDouble();
		timeSignature = parcel.readInt();
		key = parcel.readInt();
		setlistCount = parcel.readInt();
		position = parcel.readInt();
		duration = parcel.readDouble();
	}
	
	
	/**
	 * 
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(artist);
		dest.writeString(songTitle);
		dest.writeDouble(tempo);
		dest.writeInt(timeSignature);
		dest.writeInt(key);
		dest.writeInt(setlistCount);
		dest.writeInt(position);
		dest.writeDouble(duration);
	}

}
