package com.bigdrum.metronomemate.network;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.bigdrum.metronomemate.R;
import com.bigdrum.metronomemate.database.DataService;
import com.bigdrum.metronomemate.ui.setlistmanagement.SearchActivity;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.SongParams;

public class FindSongTask extends AsyncTask<SongParams, Void, List<MySong>> {
	
	private EchoNestAPI en;
	private SearchActivity searchSongUI;
	private boolean localSearch;
	private DataService dbService;
	
	public FindSongTask(SearchActivity searchSongUI, boolean localSearch) {
		
		this.searchSongUI = searchSongUI;
		this.localSearch = localSearch;

		this.dbService = DataService.getDataService(searchSongUI);
		
		en = new EchoNestAPI("ZNEQOD6XK2TZP2IVK");
		en.setTraceSends(false);
		en.setTraceRecvs(false);
	}
	
	
	/**
	 * 
	 */
	@Override
	protected List<MySong> doInBackground(SongParams... searchParams) {
		try {
            if (localSearch) {
                return findSongsLocal(searchParams[0]);
            }
            else {
                return findSongsRemote(searchParams[0]);
            }
		} catch (EchoNestException e) {
			return null;
		}
	}

	
	/**
	 * 
	 */
	private void displayResults(ArrayList<MySong> songs, boolean isLocal) {
		searchSongUI.showSearchResultsOnUiThread(songs, isLocal);
	}
	
	
	
	/**
	 * 
	 * @param p : Song search parameters
	 * @return List of matching songs in the remote DB
	 * @throws EchoNestException
	 */
	public List<MySong> findSongsRemote(SongParams p)
			throws EchoNestException {
		
		List<Song> songs = en.searchSongs(p);
		if (songs.size() > 0) {
			double tempo = songs.get(0).getTempo();
			ArrayList<MySong> mySongs = new ArrayList<MySong>();
			for (Song song : songs) {
				mySongs.add(new MySong(song, searchSongUI.getResources().obtainTypedArray(R.array.keys_array)));
			}
			displayResults(mySongs, false);
			return mySongs;
		} else {
			displayResults(null, false);
			return null;
		}
	}


	/**
	 *
	 * @param searchParams : Song search parameters
	 * @return List of matching songs in the local DB
	 */
	private List<MySong> findSongsLocal(SongParams searchParams) {

		ArrayList<MySong> songs = dbService.findMatchingSongs(searchParams);

        if (songs.size() > 0) {
            for (MySong song : songs) {
                song.setKeyNames(searchSongUI.getResources().obtainTypedArray(R.array.keys_array));
            }
            displayResults(songs, true);
            return songs;
        }
        else {
            displayResults(null, false);
            return null;
        }

	}
}
