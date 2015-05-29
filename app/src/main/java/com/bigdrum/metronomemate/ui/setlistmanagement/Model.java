package com.bigdrum.metronomemate.ui.setlistmanagement;

public class Model {
	private long setlistId;
	private String name;
	private int position;
	private boolean selected;
	private long id;
	private String artist;
	private double tempo;
	private int timeSignature;
	private int key;
	private int setlistCount;
	private double duration;
    private long songId;

	
	/**
	 * 
	 * @param str
	 * @param position
	 */
	public Model(int id, String str, int position) {
		this.id = id;
		this.name = str;
		this.position = position;
		this.selected = false;
	}

	/**
	 * 
	 * @param songName
	 * @param artist
	 * @param tempo
	 * @param timeSignature
	 * @param position
	 */
	public Model(long id, long setlistId, String songName, String artist, double tempo, int timeSignature, int key, int setlistCount, int position, double duration) {
		this.id = id;
		this.setlistId = setlistId;
		this.name = songName;
		this.artist = artist;
		this.tempo = tempo;
		this.timeSignature = timeSignature;
		this.key = key;
		this.setlistCount = setlistCount;
		this.position = position;
		this.selected = false;
		this.duration = duration;
	}


	/**
	 *
	 * @param name
	 * @param artist
	 * @param tempo
	 * @param timeSignature
	 * @param key
	 * @param setlistCount
	 * @param duration
	 */
	public Model(long id, String name, String artist, double tempo, int timeSignature, int key, int setlistCount, double duration) {

        this.id = id;
		this.name = name;
		this.artist = artist;
		this.tempo = tempo;
		this.timeSignature = timeSignature;
		this.key = key;
		this.setlistCount = setlistCount;
		this.duration = duration;
	}


    /**
     *
     * @param setlistId
     * @param songId
     * @param position
     */
    public Model(long id, long setlistId, long songId, int position) {

        this.id = id;
        this.setlistId = setlistId;
        this.songId = songId;
        this.position = position;
    }


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
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

	public void setId(long id) {
		this.id = id;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
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

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public void setTimeSignature(int timeSignature) {
		this.timeSignature = timeSignature;
	}
	
	public long getSetlistId() {
		return setlistId;
	}

	public void setSetlistId(int setlistId) {
		this.setlistId = setlistId;
	}
	
	public int getSetlistCount() {
		return setlistCount;
	}
	
	public void setSetlistCount(int setlistCount) {
		this.setlistCount = setlistCount;
	}

	public double getDuration() { return duration; }

	public void setDuration(double duration) { this.duration = duration; }

    public long getSongId() {
        return songId;
    }
	
	public int incrementSetlistCount() {
		this.setlistCount++;
		return setlistCount;
	}
	
	public int decrementSetlistCount() {
		this.setlistCount--;
		return setlistCount;
	}

	@Override
	public String toString() {
		return name;
	}

	public boolean isSubsetItem() {
		return artist != null && artist.equals("<subset>");
	}
	
	public boolean isSetlist() {
		return artist == null;
	}
}
