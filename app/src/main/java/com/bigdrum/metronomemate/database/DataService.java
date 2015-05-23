package com.bigdrum.metronomemate.database;

import static com.bigdrum.metronomemate.database.Constants.SETLISTNAME;
import static com.bigdrum.metronomemate.database.Constants.SETLISTPOS;
import static com.bigdrum.metronomemate.database.Constants.SETLISTPRIMARYKEY;
import static com.bigdrum.metronomemate.database.Constants.SETLIST_TABLE;
import static com.bigdrum.metronomemate.database.Constants.SONGLIST_QUERY;
import static com.bigdrum.metronomemate.database.Constants.SUBSET_QUERY;
import static com.bigdrum.metronomemate.database.Constants.MATCHING_SONG_QUERY;
import static com.bigdrum.metronomemate.database.Constants.SONG_QUERY;
import static com.bigdrum.metronomemate.database.Constants.SONG_ARTIST;
import static com.bigdrum.metronomemate.database.Constants.SONG_DURATION;
import static com.bigdrum.metronomemate.database.Constants.SONG_KEY;
import static com.bigdrum.metronomemate.database.Constants.SONG_NAME;
import static com.bigdrum.metronomemate.database.Constants.SONG_PRIMARYKEY;
import static com.bigdrum.metronomemate.database.Constants.SONG_SETLIST_COUNT;
import static com.bigdrum.metronomemate.database.Constants.SONG_SET_PRIMARY_KEY;
import static com.bigdrum.metronomemate.database.Constants.SONG_SET_SETLIST_ID;
import static com.bigdrum.metronomemate.database.Constants.SONG_SET_SONG_ID;
import static com.bigdrum.metronomemate.database.Constants.SONG_SET_SONG_POS;
import static com.bigdrum.metronomemate.database.Constants.SONG_SET_TABLE_NAME;
import static com.bigdrum.metronomemate.database.Constants.SONG_TABLE;
import static com.bigdrum.metronomemate.database.Constants.SONG_TEMPO;
import static com.bigdrum.metronomemate.database.Constants.SONG_TIMESIG;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bigdrum.metronomemate.R;
import com.bigdrum.metronomemate.network.MySong;
import com.bigdrum.metronomemate.ui.setlistmanagement.Model;
import com.echonest.api.v4.SongParams;

public class DataService extends SQLiteOpenHelper {

	private static final String DBNAME = "metronomemate.db";
	private static final int DBVERSION = 1;
	private Cursor setlistCursor;
	private Cursor songCursor;
	private Context context;
	private Integer numberOfSetlists;
	private static SQLiteDatabase db;
	private static DataService dataService;
	public static boolean firstTimeUser;
	
	
	/**
	 * 
	 *
	 */
	private DataService(Context context) {
		super(context, DBNAME, null, DBVERSION);
		this.context = context;
		
		
		// Uncomment for unit testing
//		context.deleteDatabase(DBNAME);
	}
	
	
	public static DataService getDataService(Context context) {
		if (dataService == null) {
			dataService = new DataService(context);
		}
		
		return dataService;
	}
	

	/**
	 * 
	 */
	public void init() {
		if (db == null) {
			db = getWritableDatabase();
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	
	/**
	 * 
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + SETLIST_TABLE + " (" 
				+ SETLISTPRIMARYKEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ SETLISTNAME + " TEXT NOT NULL, "
				+ SETLISTPOS + " INTEGER NOT NULL);"
				);
		
		db.execSQL("CREATE TABLE " + SONG_TABLE + " ("
				+ SONG_PRIMARYKEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ SONG_NAME + " TEXT NOT NULL, "
				+ SONG_ARTIST + " TEXT NOT NULL, "
				+ SONG_TEMPO + " REAL NOT NULL, "
				+ SONG_TIMESIG + " INTEGER NOT NULL, "
				+ SONG_KEY + " INTEGER, "
				+ SONG_SETLIST_COUNT + " INTEGER NOT NULL, "
				+ SONG_DURATION + " REAL NOT NULL);"
				);
		
		db.execSQL("CREATE TABLE " + SONG_SET_TABLE_NAME + " ("
				+ SONG_SET_PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ SONG_SET_SETLIST_ID + " INTEGER NOT NULL, "
				+ SONG_SET_SONG_ID + " INTEGER NOT NULL, "
				+ SONG_SET_SONG_POS + " INTEGER NOT NULL, "
				+ "FOREIGN KEY(" + SONG_SET_SETLIST_ID + ") REFERENCES " + SETLIST_TABLE + "(" + SETLISTPRIMARYKEY + "), "
				+ "FOREIGN KEY(" + SONG_SET_SONG_ID + ") REFERENCES " + SONG_TABLE + "(" + SONG_PRIMARYKEY + "));"
				);
		
		db.execSQL("PRAGMA foreign_keys=ON;");
		
		firstTimeUser = true;
	}

	
	/**
	 * 
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	
	/**
	 *
	 */
	public void addSetlistName(String setlistName) {
		
		if (numberOfSetlists == null) {
			List<Model> setlists = getAllSetlists();
			numberOfSetlists = setlists.size();
		}
		
		ContentValues values = new ContentValues();
		values.put(SETLISTNAME, setlistName);
		values.put(SETLISTPOS, numberOfSetlists++);
		db.insertOrThrow(SETLIST_TABLE, null, values);
	}
	
	
	/**
	 *
	 */
	public void changeSetlistName(String newSetlistName, Model selectedSetlist) {
		
		ContentValues values = new ContentValues();
		String origSetlistName = selectedSetlist.getName().replaceAll("'","''");
		newSetlistName=newSetlistName.replaceAll("'", "''");
        values.put(SETLISTNAME, newSetlistName);
        db.update(SETLIST_TABLE, values, SETLISTNAME + "='" + origSetlistName + "' AND "
                + SETLISTPOS + "=" + selectedSetlist.getPosition(), null);
	}
	
	
	/**
	 * 
	 */
	public List<Model> getAllSetlists() {
		List<Model> setLists = new ArrayList<Model>();
		String[] cols = { SETLISTPRIMARYKEY, SETLISTNAME, SETLISTPOS };
		Cursor cursor = db.query(SETLIST_TABLE, cols, null, null, null, null, SETLISTPOS, null);
		
		cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Model setList = new Model(cursor.getInt(0), cursor.getString(1), cursor.getInt(2));
	      setLists.add(setList);
	      cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    numberOfSetlists = setLists.size();
	    return setLists;
	}

	
	/**
	 * 
	 */
	public Cursor getSetlistCursor() {
		String[] cols = { SETLISTPRIMARYKEY, SETLISTNAME };
		Cursor cursor = db.query(SETLIST_TABLE, cols, null, null, null, null, null, null);
		
//		cursor.moveToFirst();
		return cursor;
	}


	
	
	/**
	 * 

	 */
	public List<Model> getSongsInSetlist(long setlistId) throws DataServiceException {
		
		List<Model> songs = new ArrayList<Model>();
//		String[] cols = { SONGNAME, ARTIST, TEMPO, TIMESIG, KEY, SONGPOS };
//		Cursor cursor = db.query(SONG_TABLE, cols, SETLISTID + " = " + Integer.valueOf(setlistId).toString(), null, null, null, SONGPOS, null);

        Cursor cursor = db.rawQuery(SONGLIST_QUERY, new String[]{String.valueOf(setlistId)});
		
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Model song = new Model(cursor.getLong(0), setlistId, cursor.getString(1), cursor.getString(2), cursor.getDouble(3), 
						cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), cursor.getInt(8), cursor.getDouble(7));
				songs.add(song);
				cursor.moveToNext();
		    }
		}
		
		cursor.close();
		return songs;
	}


    /**
     *
     * @param setlistId : setlist id
     * @return list of songs in the setlist (excluding subset items)
     * @throws DataServiceException
     */
    public List<Model> getSongsInSetlistExcludingSubsets(long setlistId) throws DataServiceException {

        List<Model> songs = new ArrayList<Model>();

        Cursor cursor = db.rawQuery(SONG_QUERY, new String[]{String.valueOf(setlistId)});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Model song = new Model(cursor.getLong(0), setlistId, cursor.getString(1), cursor.getString(2), cursor.getDouble(3),
                        cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), cursor.getInt(8), cursor.getDouble(7));
                songs.add(song);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return songs;
    }

	
	/**
	 *
	 */
	public long getSetlistId(Model setlist) throws DataServiceException {
		String[] cols = { SETLISTPRIMARYKEY };
		Cursor cursor = db.query(SETLIST_TABLE, cols, SETLISTNAME + " = '" + setlist.getName() 
				+ "' AND " + SETLISTPOS + " = " + setlist.getPosition(), 
				null, null, null, null, null);
		
		if (!cursor.moveToFirst()) {
			String msg = context.getString(R.string.setlist_non_existant, setlist.getName());
			throw new DataServiceException(msg);
		}
		
		int id = cursor.getInt(0);
		setlist.setId(id);
		cursor.close();
		
		return id;
	}
	
	
	/**
	 *
	 */
	public int getSetlistIdByName(String setlist) {
		String[] cols = { SETLISTPRIMARYKEY };
		Cursor cursor = db.query(SETLIST_TABLE, cols, SETLISTNAME + " = '" + setlist + "'", 
				null, null, null, null, null);
		
		if (!cursor.moveToFirst()) {
			return -1;
		}
		
		int id = cursor.getInt(0);
		cursor.close();
		
		return id;
	}
	
	
	/**
	 *
	 */
	public String getSetlistNameById(long id) {
		String[] cols = { SETLISTNAME };
		String name = null;
		Cursor cursor = db.query(SETLIST_TABLE, cols, SETLISTPRIMARYKEY + "= " + id,
				null, null, null, null, null);
		
		if (cursor.moveToFirst()) {
			name = cursor.getString(0);
		}
		else {
			name = "** Unknown setlist name with id " + String.valueOf(id) + " **";
		}

		cursor.close();
		return name;
	}
	
	
	/**
	 *
	 * 
	 */
	public void addSetlistPosition(Model setlist) {
		String[] cols = { SETLISTPRIMARYKEY, SETLISTPOS };
		Cursor cursor = db.query(SETLIST_TABLE, cols, SETLISTNAME + " = '" + setlist.getName() + "'", 
				null, null, null, null, null);
		
		if (cursor.moveToFirst()) {
			int position = cursor.getInt(1);
			cursor.close();
			
			setlist.setPosition(position);
		}
	}


	
	public boolean isSongInSetlist(Model song, long setlistId) {

        Cursor cursor = db.rawQuery(MATCHING_SONG_QUERY, new String[]{String.valueOf(setlistId),
            song.getArtist(), song.getName()});

        return (cursor.getCount() > 0);
	}
	
	
	/**
	 *
	 */
	private Model getExistingSong(Model song) {
		String cols[] = { SONG_PRIMARYKEY };
		
		Cursor cursor = db.query(SONG_TABLE, cols, SONG_NAME + " = '" + song.getName().replaceAll("'","''")
				+ "' AND " + SONG_ARTIST + " = '" + song.getArtist().replaceAll("'","''")
				+ "' AND " + SONG_TEMPO + " = " + song.getTempo()
				+ " AND " + SONG_TIMESIG + " = " + song.getTimeSignature()
				+ " AND " + SONG_KEY + " = " + song.getKey(), 
				null, null, null, null, null);
		
		if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		
		song.setId(cursor.getLong(0));
		cursor.close();
		return song;
	}
	

	/**
	 *
	 */
	public boolean addSongToSetlist(Model song, long setlistId) throws DataServiceException {
		/*if (setlist.getPosition() == -1) {
			addSetlistPosition(setlist);
		}
		setlistId = getSetlistId(setlist);*/
		
		if (isSongInSetlist(song, setlistId)) {
			return false;
		}
		
		Model existingSong = getExistingSong(song);
		if (existingSong != null) {
			song = existingSong;
			List<Model> songs = getSongsInSetlist(setlistId);
			song.setPosition(songs.size());
		}
		
		ContentValues values = new ContentValues();
		
		if (song.getId() == -1) {
			List<Model> songs = getSongsInSetlist(setlistId);
			song.setPosition(songs.size());
			values.put(SONG_NAME, song.getName().replaceAll("'","''"));
			values.put(SONG_ARTIST, song.getArtist().replaceAll("'","''"));
			values.put(SONG_TEMPO, song.getTempo());
			values.put(SONG_TIMESIG, song.getTimeSignature());
			values.put(SONG_KEY, song.getKey());
			values.put(SONG_SETLIST_COUNT, 1);
			values.put(SONG_DURATION, song.getDuration());
			long songId = db.insertOrThrow(SONG_TABLE, null, values);
			song.setId(songId);
		}
		else {
			values.put(SONG_SETLIST_COUNT, song.incrementSetlistCount());
			db.update(SONG_TABLE, values, SONG_PRIMARYKEY + "=" + song.getId(), null);
		}
		
		createSongSetlistLinkRecord(song, setlistId);

        return true;
	}
	
	
	/**
	 *
	 */
	private void createSongSetlistLinkRecord(Model song, long setlistId) {
		ContentValues values = new ContentValues();
		values.put(SONG_SET_SETLIST_ID, setlistId);
		values.put(SONG_SET_SONG_ID, song.getId());
		values.put(SONG_SET_SONG_POS, song.getPosition());
		
		db.insertOrThrow(SONG_SET_TABLE_NAME, null, values);
	}
	
	
	/**
	 *
	 */
	private int numberOfSubsets(long setlistId) {

		Cursor cursor = db.rawQuery(SUBSET_QUERY, new String[]{String.valueOf(setlistId)});

		return cursor.getCount();

//		String cols[] = { SONG_PRIMARYKEY };
//
//		Cursor cursor = db.query(SONG_TABLE, cols, SONG_ARTIST + " = '<subset>'",
//				null, null, null, null, null);
//
//		int numOfSubsets = cursor.getCount();
//		cursor.close();
//		return numOfSubsets;
	}
	
	
	/**
	 *
	 * @throws DataServiceException 
	 */
	public Model addSection(Model selectedSetlist) throws DataServiceException {
		int nextSubset = numberOfSubsets(selectedSetlist.getId()) + 1;
		Model newSection = new Model(-1, selectedSetlist.getSetlistId(), "Set" + nextSubset, "<subset>", 0, 0, 0, 1, -1, 0);
		addSongToSetlist(newSection, selectedSetlist.getId());
		
		return newSection;
	}
	
	
	/**
	 * 
	 */
	public void reorderSetlists(List<Model> setLists) {
		ContentValues values = new ContentValues();
		String whereClause;
		int position = 0;
		
		for (Model set : setLists) {
			values.put(SETLISTPOS, position++);
			whereClause = SETLISTNAME + " = '" + set.getName() + "'";
			db.update(SETLIST_TABLE, values, whereClause, null);
		}
		
	}
	
	
	/**
	 *
	 */
	public void reorderSongs(List<Model> songs, Model setlist) {
		ContentValues values = new ContentValues();
		String whereClause;
		String songName;
		int position = 0;
		
		for (Model song : songs) {
			values.put(SONG_SET_SONG_POS, position++);
			songName = song.getName();
			songName=songName.replaceAll("'","''");
			whereClause = SONG_SET_SONG_ID + " = " + song.getId() + " AND " + SONG_SET_SETLIST_ID + " = " + setlist.getId();
			db.update(SONG_SET_TABLE_NAME, values, whereClause, null);
		}
	}

	


	/**
	 *
	 * @throws DataServiceException
	 */
	public List<Model> deleteSetlists(List<Model> setLists) throws DataServiceException {
		List<Model> remainingSetlists = new ArrayList<Model>();
		String gigNames = null;
		
		for (Model setlist : setLists) {
			if (!setlist.isSelected()) {
				remainingSetlists.add(setlist);
				continue;
			}
			
			setlist.setId(getSetlistId(setlist));

			deleteSongsInSetlist(setlist);
			
			db.delete(SETLIST_TABLE, SETLISTPRIMARYKEY + "=" + setlist.getId() 
					+ " AND " + SETLISTPOS + "=" + setlist.getPosition(), null);
			
			numberOfSetlists--;
		}
		
		reorderSetlists(remainingSetlists);
		return remainingSetlists;
	}
	
	
	/**
	 *
	 * @throws DataServiceException 
	 */
	public void deleteSongsInSetlist(Model setlist) throws DataServiceException {
		List<Model> songs = getSongsInSetlist(setlist.getId());
		
		for (Model song : songs) {
			song.setSelected(true);
		}
		
		deleteSongs(songs, setlist);
	}
	


	/**
	 *
	 * @throws DataServiceException
	 */
	public List<Model> deleteSongs(List<Model> songs, Model setlist) throws DataServiceException {
//		setlist.setId(getSetlistId(setlist));
		List<Model> remainingSongs = new ArrayList<Model>();
		String songName = null;
		
		for (Model song : songs) {
			if (!song.isSelected()) {
				remainingSongs.add(song);
				continue;
			}
			songName = song.getName();
			songName=songName.replaceAll("'","''");
			
			db.delete(SONG_SET_TABLE_NAME, SONG_SET_SETLIST_ID + "=" + setlist.getId() + " AND " 
					+ SONG_SET_SONG_ID + "=" + song.getId(), null);
			
			deleteSong(song);
			
//			db.delete(SONG_TABLE, SONGNAME + "='" + songName + "' AND " + SETLISTID + "=" + setlist.getId()
//					+ " AND " + SONGPOS + "=" + song.getPosition(), null);
		}
		
		reorderSongs(remainingSongs, setlist);
		return remainingSongs;
	}
	
	
	/**
	 *
	 */
	private void deleteSong(Model song) {
		song.decrementSetlistCount();
		if (song.getSetlistCount() > 0) {
			updateSong(song);
		}
		else {
			db.delete(SONG_TABLE, SONG_PRIMARYKEY + "=" + song.getId(), null);
		}
	}


	/**
	 *
	 */
	public void updateSong(Model song) {
		ContentValues values = new ContentValues();
		
		values.put(SONG_NAME, song.getName());
		values.put(SONG_ARTIST, song.getArtist());
		values.put(SONG_TEMPO, song.getTempo());
		values.put(SONG_TIMESIG, song.getTimeSignature());
		values.put(SONG_KEY, song.getKey());
		values.put(SONG_SETLIST_COUNT, song.getSetlistCount());
        values.put(SONG_DURATION, song.getDuration());
		
		db.update(SONG_TABLE, values, SONG_PRIMARYKEY + "=" + song.getId(), null);
	}


	/**
	 *
	 * @throws DataServiceException
	 */
	public void copySongs(List<Model> songs, long toSetlistId) throws DataServiceException {
		for (Model song : songs) {
			if (song.isSelected()) {
//				addSongToSetlist(song, new Model(0, setlist, -1));
				addSongToSetlist(song, toSetlistId);
			}
		}
	}
	
	
	/**
	 * 
	 *
	 */
	public void copySetlist(List<Model> setlists, long toSetlistId) throws DataServiceException {
		List<Model> songs;
		
		for (Model setlist : setlists) {
			if (setlist.isSelected()) {
				songs = getSongsInSetlist(setlist.getId());
				for (Model song : songs) {
					song.setSelected(true);
				}
				
				copySongs(songs, toSetlistId);
			}
		}
	}


	/**
	 *
	 * @param setlist: The setlist
	 * @return int: Number of songs in this setlist
	 */
	public int findNumberOfSongsInSetlist(Model setlist) {

		String[] cols = { SONG_SET_PRIMARY_KEY };
		String name = null;
		Cursor cursor = db.query(SONG_SET_TABLE_NAME, cols, SONG_SET_SETLIST_ID + "= " + setlist.getId(),
				null, null, null, null, null);

		return cursor.getCount();
	}


	/**
	 * Finds all songs matching the search criteria
	 * @param params : The song search criteria
	 */
	public ArrayList<MySong> findMatchingSongs(SongParams params) {

		ArrayList<MySong> matchingSongs = new ArrayList<MySong>();

		String cols[] = { SONG_PRIMARYKEY, SONG_NAME, SONG_ARTIST, SONG_TEMPO, SONG_TIMESIG, SONG_KEY, SONG_SETLIST_COUNT, SONG_DURATION};
        String searchCriteria = "";

        if (params.getMap().get("title") != null) {
            searchCriteria = SONG_NAME + " like '%" + ((String)params.getMap().get("title")).replaceAll("'","''") + "%'";
        }
        if (params.getMap().get("artist") != null) {
            if (!searchCriteria.equals("")) {
                searchCriteria += " AND ";
            }
            searchCriteria += SONG_ARTIST + " like '%" + ((String)params.getMap().get("artist")).replaceAll("'","''") + "%'";
        }


		Cursor cursor = db.query(SONG_TABLE, cols, searchCriteria, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MySong song = new MySong(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3),
                        cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), -1, cursor.getDouble(7));

                if (!song.getArtist().equals("<subset>")) {
                    matchingSongs.add(song);
                }
                cursor.moveToNext();
            }
        }

        cursor.close();
        return matchingSongs;
	}
}
