package com.bigdrum.metronomemate.database;

import android.provider.BaseColumns;

public interface Constants extends BaseColumns {
	public static final String APP_NAME = "BeatBuddy";
	
	// Setlist table
	public static final String SETLISTPRIMARYKEY = "_id";
	public static final String SETLIST_TABLE = "setlists";
	public static final String SETLISTNAME = "setlistname";
	public static final String SETLISTPOS = "position";
	
	// Song table
	public static final String SONG_PRIMARYKEY = "_id";
	public static final String SONG_TABLE = "songs";
	public static final String SONG_NAME = "songname";
	public static final String SONG_ARTIST = "artist";
	public static final String SONG_TEMPO = "tempo";
	public static final String SONG_TIMESIG = "timesig";
	public static final String SONG_KEY = "key";
	public static final String SONG_SETLIST_COUNT="setlistcount";
	public static final String SONG_DURATION = "duration";
	
	// Song-Setlist link table
	public static final String SONG_SET_PRIMARY_KEY = "_id";
	public static final String SONG_SET_TABLE_NAME = "songsetlink";
	public static final String SONG_SET_SETLIST_ID = "setlistid";
	public static final String SONG_SET_SONG_ID = "songid";
	public static final String SONG_SET_SONG_POS = "songposition";
	
	
	// Activity constants
	public static final String AUTHORITY = "com.bigdrum.metronomemate";
	public static final String NEW_SETLIST_NAME="setListName";
	public static final String ORIGINAL_SETLIST_NAME = "songName";
	public static final String SONG_ENTERED = "songEntered";
	public static final String SONG_EDITED = "songEdited";
	public static final String SELECTED_SETLIST = "selectedSetlist";
	public static final String SONG_LIST = "songList";
	public static final String SONG_SELECTED = "songSelected";
	public static final String VENUE = "venue";
	public static final String GIG = "gig";
	public static final String SELECTED_CONTACTS = "selectedContacts";
	
	public static final int SEARCH_SONG = 1;
	public static final int ADD_SONG_TO_SETLIST = 2;
	public static final int ADD_NEW_SETLIST = 3;
	public static final int VIEW_SETLISTS = 4;
	public static final int MANUALLY_ADD_SONG = 5;
	public static final int EDIT_SONG = 6;
	public static final int SELECT_SETLIST = 7;
	public static final int EDIT_SETLIST = 8;
	
	public static final String SONGLIST_QUERY = "SELECT s.*, l." + SONG_SET_SONG_POS + " FROM " + SONG_TABLE + " s " 
				+ "INNER JOIN " + SONG_SET_TABLE_NAME + " l ON s." + SONG_PRIMARYKEY + " = l." + SONG_SET_SONG_ID
				+ " WHERE l." + SONG_SET_SETLIST_ID + "=?"
				+ " ORDER BY l." + SONG_SET_SONG_POS;

	public static final String SUBSET_QUERY = "SELECT s.*, l." + SONG_SET_SONG_POS + " FROM " + SONG_TABLE + " s "
			+ "INNER JOIN " + SONG_SET_TABLE_NAME + " l ON s." + SONG_PRIMARYKEY + " = l." + SONG_SET_SONG_ID
			+ " WHERE l." + SONG_SET_SETLIST_ID + "=? AND s." + SONG_ARTIST + " = '<subset>'"
			+ " ORDER BY l." + SONG_SET_SONG_POS;

	public static final String SONG_QUERY = "SELECT s.*, l." + SONG_SET_SONG_POS + " FROM " + SONG_TABLE + " s "
			+ "INNER JOIN " + SONG_SET_TABLE_NAME + " l ON s." + SONG_PRIMARYKEY + " = l." + SONG_SET_SONG_ID
			+ " WHERE l." + SONG_SET_SETLIST_ID + "=? AND s." + SONG_ARTIST + " <> '<subset>'"
			+ " ORDER BY l." + SONG_SET_SONG_POS;

    public static final String MATCHING_SONG_QUERY = "SELECT s.*, l." + SONG_SET_SONG_POS + " FROM " + SONG_TABLE + " s "
            + "INNER JOIN " + SONG_SET_TABLE_NAME + " l ON s." + SONG_PRIMARYKEY + " = l." + SONG_SET_SONG_ID
            + " WHERE l." + SONG_SET_SETLIST_ID + "=? AND s." + SONG_ARTIST + " =? AND s." + SONG_NAME + " = ? AND s." + SONG_ARTIST + " <> '<subset>'"
            + " ORDER BY l." + SONG_SET_SONG_POS;
	
	public static final String SETLIST_HEADING = 
"<html> " +
"<head>" +
"</head>" +
"<body>" +
"<center><h1>[%bandname%] SETLIST</h1></center>" +
"<div align=\"center\"> " +
	"<table style=\"border-style:solid\"> " +
		"<tr> " +
			"<td>Venue: <b>[%venue%]</b></td>" +
			"<td>Date: <b>[%date%]</b></td>" +
			"<td>Time: <b>[%time%]</b></td>" +
		"</tr>" +
	"</table>" +
"</div>" +
"<br>" +
"<br>";
	
	public static final String SETLIST_FOOTER =
"</body>" +
"</html>";
	
}
