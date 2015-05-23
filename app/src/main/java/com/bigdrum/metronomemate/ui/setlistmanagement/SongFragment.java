package com.bigdrum.metronomemate.ui.setlistmanagement;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.bigdrum.metronomemate.R;
import com.bigdrum.metronomemate.database.Constants;
import com.bigdrum.metronomemate.network.MySong;
import com.echonest.api.v4.SongParams;


public class SongFragment extends Fragment {
	
	private View rootView;
	private Handler handler;
	private EditText artistName;
	private EditText songTitle;
	private EditText songInfoPane;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.fragment_song, container, false);
		
		handler = new Handler();
		artistName = (EditText)rootView.findViewById(R.id.artistName);
		songTitle = (EditText)rootView.findViewById(R.id.songTitle);
		songInfoPane = (EditText)rootView.findViewById(R.id.songInfo);
		songInfoPane.setKeyListener(null);
		
		createUIComponents();

		return rootView;
	}
	
	
	/**
	 * 
	 */
	private void createUIComponents() {
		Button searchButton = (Button)rootView.findViewById(R.id.searchButton);
		searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				InputMethodManager inputManager = (InputMethodManager)            
						  getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); 
						    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),      
						    InputMethodManager.HIDE_NOT_ALWAYS);
				doSearch();
			}
		});
	}
	
	
	
	
	/**
	 * 
	 */
	private void doSearch() {
		SongParams p = new SongParams();
		if (artistName.getText().length() > 0) {
			p.setArtist(artistName.getText().toString());
		}
		if (songTitle.getText().length() > 0) {
			p.setTitle(songTitle.getText().toString());
		}
		p.setResults(20);
		p.includeAudioSummary();

		songInfoPane.setText(R.string.searching);
//		new FindSongTask(this).execute(p);
	}
	
	
	
	/**
	 * 
	 */
	public void showSearchResultsOnUiThread(final ArrayList<MySong> songs) {
		
		handler.post(new Runnable() {

			@Override
			public void run() {
				if (songs != null) {
					displaySearchResults(songs);
				}
				else {
					songInfoPane.setText(R.string.nothing_found);
				}
			}
			
		});
	}
	
	
	/**
	 * 
	 * @param song
	 */
	private void displaySearchResults(ArrayList<MySong> songs) {
		Intent intent = new Intent(this.getActivity(), SongListActivity.class);
		intent.putParcelableArrayListExtra(Constants.SONG_LIST, songs);
		songInfoPane.setText("");
		startActivityForResult(intent, Constants.SEARCH_SONG);
	}
	
	
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
	  super.onActivityResult(requestCode, resultCode, data); 
	  switch(requestCode) { 
	    case (Constants.SEARCH_SONG) : { 
	      if (resultCode == Activity.RESULT_OK) { 
	    	  MySong selectedSong= (MySong)data.getExtras().get(Constants.SONG_SELECTED);
	    	  songInfoPane.setText(selectedSong.toString());
	      } 
	      break; 
	    } 
	  } 
	}
}
