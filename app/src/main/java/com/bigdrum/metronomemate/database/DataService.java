package com.bigdrum.metronomemate.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import com.bigdrum.metronomemate.R;
import com.bigdrum.metronomemate.export.Contact;
import com.bigdrum.metronomemate.gig.Gig;
import com.bigdrum.metronomemate.network.MySong;
import com.bigdrum.metronomemate.ui.setlistmanagement.Model;
import com.bigdrum.metronomemate.venue.Venue;
import com.echonest.api.v4.SongParams;

import java.util.ArrayList;
import java.util.List;

//import static com.bigdrum.metronomemate.database.Constants.MATCHING_SONG_QUERY;
//import static com.bigdrum.metronomemate.database.Constants.SETLISTNAME;
//import static com.bigdrum.metronomemate.database.Constants.SETLISTPOS;
//import static com.bigdrum.metronomemate.database.Constants.SETLISTPRIMARYKEY;
//import static com.bigdrum.metronomemate.database.Constants.SETLIST_TABLE;
//import static com.bigdrum.metronomemate.database.Constants.SONGLIST_QUERY;
//import static com.bigdrum.metronomemate.database.Constants.SONG_ARTIST;
//import static com.bigdrum.metronomemate.database.Constants.SONG_DURATION;
//import static com.bigdrum.metronomemate.database.Constants.SONG_KEY;
//import static com.bigdrum.metronomemate.database.Constants.SONG_NAME;
//import static com.bigdrum.metronomemate.database.Constants.SONG_PRIMARYKEY;
//import static com.bigdrum.metronomemate.database.Constants.SONG_QUERY;
//import static com.bigdrum.metronomemate.database.Constants.SONG_SETLIST_COUNT;
//import static com.bigdrum.metronomemate.database.Constants.SONG_SET_PRIMARY_KEY;
//import static com.bigdrum.metronomemate.database.Constants.SONG_SET_SETLIST_ID;
//import static com.bigdrum.metronomemate.database.Constants.SONG_SET_SONG_ID;
//import static com.bigdrum.metronomemate.database.Constants.SONG_SET_SONG_POS;
//import static com.bigdrum.metronomemate.database.Constants.SONG_SET_TABLE_NAME;
//import static com.bigdrum.metronomemate.database.Constants.SONG_TABLE;
//import static com.bigdrum.metronomemate.database.Constants.SONG_TEMPO;
//import static com.bigdrum.metronomemate.database.Constants.SONG_TIMESIG;
//import static com.bigdrum.metronomemate.database.Constants.SUBSET_QUERY;
//import static com.bigdrum.metronomemate.database.Constants.VENUE_NAME;
//import static com.bigdrum.metronomemate.database.Constants.VENUE_TABLE;
//import static com.bigdrum.metronomemate.database.Constants.VENUE_PRIMARYKEY;
import static com.bigdrum.metronomemate.database.Constants.*;


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

        db.execSQL("CREATE TABLE " + VENUE_TABLE + " ("
                        + VENUE_PRIMARYKEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + VENUE_CONTACT_NAME + " TEXT, "
                        + VENUE_PHONE + " TEXT, "
                        + VENUE_NAME + " TEXT NOT NULL, "
                        + VENUE_EMAIL + " TEXT, "
                        + VENUE_STREET + " TEXT, "
                        + VENUE_TOWN + " TEXT, "
                        + VENUE_POSTCODE + " TEXT, "
                        + VENUE_COUNTRY + " TEXT, "
                        + VENUE_LAST_GIG_DATE + " TEXT);"
        );

        db.execSQL("CREATE TABLE " + GIG_TABLE + " ("
                        + GIG_PRIMARYKEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + GIG_NAME + " TEXT NOT NULL, "
                        + GIG_VENUE_ID + " INTEGER NOT NULL, "
                        + GIG_DATE_TIME + " TEXT, "
                        + GIG_SETLIST_ID + " INTEGER, "
                        + GIG_NOTES + " TEXT, "
                        + "FOREIGN KEY(" + GIG_VENUE_ID + ") REFERENCES " + VENUE_TABLE + "(" + VENUE_PRIMARYKEY + "), "
                        + "FOREIGN KEY(" + GIG_SETLIST_ID + ") REFERENCES " + SETLIST_TABLE + "(" + SETLISTPRIMARYKEY + "));"
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
     * @param setlistName
     * @param position
     */
    public void addSetlistAtPosition(String setlistName, int position) {
        ContentValues values = new ContentValues();
        values.put(SETLISTNAME, setlistName);
        values.put(SETLISTPOS, position);
        numberOfSetlists = position;
        db.insertOrThrow(SETLIST_TABLE, null, values);
    }


    /**
     *
     */
    public void changeSetlistName(String newSetlistName, Model selectedSetlist) {

        ContentValues values = new ContentValues();
        String origSetlistName = selectedSetlist.getName().replaceAll("'", "''");
        newSetlistName = newSetlistName.replaceAll("'", "''");
        values.put(SETLISTNAME, newSetlistName);
        db.update(SETLIST_TABLE, values, SETLISTNAME + "='" + origSetlistName + "' AND "
                + SETLISTPOS + "=" + selectedSetlist.getPosition(), null);
    }


    /**
     *
     */
    public List<Model> getAllSetlists() {
        List<Model> setLists = new ArrayList<Model>();
        String[] cols = {SETLISTPRIMARYKEY, SETLISTNAME, SETLISTPOS};
        Cursor cursor = db.query(SETLIST_TABLE, cols, null, null, null, null, SETLISTPOS, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Model setList = new Model(cursor.getInt(0), cursor.getString(1), cursor.getInt(2));
                setLists.add(setList);
                cursor.moveToNext();
            }
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
        String[] cols = {SETLISTPRIMARYKEY, SETLISTNAME};
        Cursor cursor = db.query(SETLIST_TABLE, cols, null, null, null, null, null, null);

//		cursor.moveToFirst();
        return cursor;
    }


    /**
     * @return
     */
    public List<Model> getAllSongs() {

        List<Model> songs = new ArrayList<Model>();
        Cursor cursor = db.query(SONG_TABLE, null, null, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                Model song = new Model(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3),
                        cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), cursor.getDouble(7));
                songs.add(song);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return songs;
    }


    /**
     *
     * @return
     */
    public List<Model> getAllSongSetRecords() {

        List<Model> records = new ArrayList<Model>();
        Cursor cursor = db.query(SONG_SET_TABLE_NAME, null, null, null, null, null, null, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                Model song = new Model(cursor.getLong(0), cursor.getLong(1), cursor.getLong(2), cursor.getInt(3));
                records.add(song);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return records;
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
		
		Cursor cursor = db.query(SONG_TABLE, cols, SONG_NAME + " = '" + song.getName().replaceAll("'", "''")
						+ "' AND " + SONG_ARTIST + " = '" + song.getArtist().replaceAll("'", "''") + "' AND " + SONG_ARTIST + " <> '<subset>"
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
     * @param song
     */
    public void addSong(Model song) {

        ContentValues values = new ContentValues();

        values.put(SONG_NAME, song.getName().replaceAll("'","''"));
        values.put(SONG_ARTIST, song.getArtist().replaceAll("'","''"));
        values.put(SONG_TEMPO, song.getTempo());
        values.put(SONG_TIMESIG, song.getTimeSignature());
        values.put(SONG_KEY, song.getKey());
        values.put(SONG_SETLIST_COUNT, song.getSetlistCount());
        values.put(SONG_DURATION, song.getDuration());

        long songId = db.insertOrThrow(SONG_TABLE, null, values);
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


	/*****************************************************************
	 *
	 * Venue data services
	 *
	 *****************************************************************/


	/**
	 *
	 * @param venue
	 */
	public void addVenue(Venue venue) {
		ContentValues values = new ContentValues();
		values.put(VENUE_NAME, venue.getName());
		values.put(VENUE_STREET, venue.getStreet());
		values.put(VENUE_TOWN, venue.getTown());
		values.put(VENUE_POSTCODE, venue.getPostcode());
		values.put(VENUE_COUNTRY, venue.getCountry());
		values.put(VENUE_CONTACT_NAME, venue.getContactName());
		values.put(VENUE_PHONE, venue.getPhone());
		values.put(VENUE_EMAIL, venue.getEmail());

		long id = db.insertOrThrow(VENUE_TABLE, null, values);
		venue.setId(id);
	}



	/**
	 *
	 * @param id
	 * @return
	 */
	public String getVenueNameById(long id) {
		String[] cols = { VENUE_NAME };
		String venueName = null;
		Cursor cursor = db.query(VENUE_TABLE, cols, VENUE_PRIMARYKEY + "=" + id, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			venueName = cursor.getString(0);
		}
		else {
			venueName = "** Unknown Venue name with id " + String.valueOf(id) + " **";
		}

		cursor.close();
		return venueName;
	}


	/**
	 *
	 * @param name
	 * @return
	 */
	public long getVenueIdByName(String name) {

		String[] cols = { VENUE_PRIMARYKEY };
		long venueId = -1;
		Cursor cursor = db.query(VENUE_TABLE, cols, VENUE_NAME + "='" + name.replaceAll("'","''") +"'", null, null, null, null, null);

		if (cursor.moveToFirst()) {
			venueId = cursor.getLong(0);
		}

		cursor.close();
		return venueId;
	}


	/**
	 *
	 */
	public List<Venue> readAllVenues() {
		List<Venue> venues = new ArrayList<Venue>();
		String[] cols = { VENUE_PRIMARYKEY, VENUE_NAME, VENUE_STREET, VENUE_TOWN, VENUE_POSTCODE, VENUE_COUNTRY, VENUE_CONTACT_NAME,
				VENUE_PHONE, VENUE_EMAIL, VENUE_LAST_GIG_DATE};
		Cursor cursor = db.query(VENUE_TABLE, cols, null, null, null, null, VENUE_NAME, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Venue venue = new Venue(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
					cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9));
			venues.add(venue);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return venues;
	}


//	/**
//	 *
//	 * @param venue
//	 * @return
//	 */
//	public void setLastGigDateForVenue(Venue venue) {
//		String[] cols = { GIG_DATE_TIME };
//		Cursor cursor = db.query(GIG_TABLE, cols, GIG_PRIMARYKEY + " = '" + venue.getLastGigId() + "'", null, null, null, null);
//
//		if (cursor.moveToFirst()) {
//			venue.setLastGigDate(cursor.getString(0));
//		}
//
//		cursor.close();
//	}


	/**
	 *
	 * @param venue
	 */
	public void updateVenue(Venue venue) {
		ContentValues values = new ContentValues();
		values.put(VENUE_NAME, venue.getName());
		values.put(VENUE_STREET, venue.getStreet());
		values.put(VENUE_TOWN, venue.getTown());
		values.put(VENUE_COUNTRY, venue.getCountry());
		values.put(VENUE_POSTCODE, venue.getPostcode());
		values.put(VENUE_CONTACT_NAME, venue.getContactName());
		values.put(VENUE_EMAIL, venue.getEmail());
		values.put(VENUE_PHONE, venue.getPhone());

		db.update(VENUE_TABLE, values, VENUE_PRIMARYKEY + "=" + venue.getId(), null);
	}


	/**
	 *
	 * @param venue
	 */
	public void deleteVenue(Venue venue) throws DataServiceException {
		try {
			db.delete(VENUE_TABLE, VENUE_PRIMARYKEY + "=" + venue.getId(), null);
		}
		catch(SQLiteConstraintException e) {
			List<String> gigNames = getGigsWithVenueId(venue.getId());
			StringBuilder msg = new StringBuilder(context.getString(R.string.db_delete_venue_fk_constraint)).append("\n\n");
			for (String gigName : gigNames) {
				msg.append(gigName).append("\n");
			}

			throw new DataServiceException(msg.toString());
		}
	}


	/**
	 *
	 * @param venueId
	 * @return
	 */
	public List<String> getGigsWithVenueId(long venueId) {
		List<String> gigNames = new ArrayList<String>();
		String[] cols = {GIG_NAME};
		Cursor cursor = db.query(GIG_TABLE, cols, GIG_VENUE_ID + "=" + venueId, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			gigNames.add(cursor.getString(0));
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return gigNames;
	}


	/***************************************************************
	 *
	 * Gig Management
	 *
	 ***************************************************************/

	/**
	 *
	 * @return
	 */
	public List<Gig> readAllGigs() {
		List<Gig> gigs = new ArrayList<Gig>();

		Cursor cursor = db.rawQuery(ALLGIGS_QUERY, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Gig gig = new Gig(cursor.getInt(0), cursor.getString(1), cursor.getLong(2), cursor.getLong(4),
					cursor.getString(3));
			gigs.add(gig);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return gigs;
	}


    public List<Gig> findAllGigsOrderById() {

        List<Gig> gigs = new ArrayList<Gig>();
        Cursor cursor = db.query(GIG_TABLE, null, null, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                Gig gig = new Gig(cursor.getInt(0), cursor.getString(1), cursor.getLong(2), cursor.getLong(4),
                        cursor.getString(3));
                gigs.add(gig);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return gigs;
    }


	/**
	 *
	 * @param gig
	 */
	public void addGig(Gig gig) {
		ContentValues values = new ContentValues();
		values.put(GIG_NAME, gig.getName());
		values.put(GIG_VENUE_ID, gig.getVenueId());
		values.put(GIG_SETLIST_ID, gig.getSetlistId());
		values.put(GIG_DATE_TIME, gig.getDateTime());

		long id = db.insertOrThrow(GIG_TABLE, null, values);
		gig.setId(id);
	}


	/**
	 *
	 * @param gig
	 * @throws DataServiceException
	 */
	public void deleteGig(Gig gig) {
		db.delete(GIG_TABLE, GIG_PRIMARYKEY + "=" + gig.getId(), null);
	}



	/**
	 *
	 * @param gig
	 */
	public void updateGig(Gig gig) {
		ContentValues values = new ContentValues();
		values.put(GIG_NAME, gig.getName());
		values.put(GIG_VENUE_ID, gig.getVenueId());
		values.put(GIG_SETLIST_ID, gig.getSetlistId());
		values.put(GIG_DATE_TIME, gig.getDateTime());

		db.update(GIG_TABLE, values, GIG_PRIMARYKEY + "=" + gig.getId(), null);
	}



	/**
	 *
	 * @return
	 */
	public List<Contact> getNameEmailDetails() {
		List<Contact> contacts = new ArrayList<Contact>();

		ContentResolver cr = context.getContentResolver();
		String[] PROJECTION = new String[] { ContactsContract.RawContacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.Contacts.PHOTO_ID,
				ContactsContract.CommonDataKinds.Email.DATA,
				ContactsContract.CommonDataKinds.Photo.CONTACT_ID };
		String order = "CASE WHEN "
				+ ContactsContract.Contacts.DISPLAY_NAME
				+ " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
				+ ContactsContract.Contacts.DISPLAY_NAME
				+ ", "
				+ ContactsContract.CommonDataKinds.Email.DATA
				+ " COLLATE NOCASE";
		String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
		Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
		if (cur.moveToFirst()) {
			do {
				contacts.add(new Contact(cur.getString(1), cur.getString(3)));
			} while (cur.moveToNext());
		}

		cur.close();
		return contacts;
	}
}
