package com.bigdrum.metronomemate.network;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.bigdrum.metronomemate.R;
import com.bigdrum.metronomemate.ui.setlist.SearchActivity;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.SongParams;

public class FindSongTask extends AsyncTask<SongParams, Void, List<MySong>> {
	
	private EchoNestAPI en;
	private SearchActivity searchSongUI;
	
	public FindSongTask(SearchActivity searchSongUI) {
		
		this.searchSongUI = searchSongUI;
		
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
			return getTempo(searchParams[0]);
		} catch (EchoNestException e) {
			return null;
		}
	}

	
	/**
	 * 
	 */
	private void displayResults(ArrayList<MySong> songs) {
		searchSongUI.showSearchResultsOnUiThread(songs);
	}
	
	
	
	/**
	 * 
	 * @param p
	 * @return
	 * @throws EchoNestException
	 */
	public List<MySong> getTempo(SongParams p)
			throws EchoNestException {
		
		List<Song> songs = en.searchSongs(p);
		if (songs.size() > 0) {
			double tempo = songs.get(0).getTempo();
			ArrayList<MySong> mySongs = new ArrayList<MySong>();
			for (Song song : songs) {
				mySongs.add(new MySong(song, searchSongUI.getResources().obtainTypedArray(R.array.keys_array)));
			}
			displayResults(mySongs);
			return mySongs;
		} else {
			displayResults(null);
			return null;
		}
	}
}
