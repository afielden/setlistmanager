package com.bigdrum.setlistmanager.ui.setlistmanagement;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bigdrum.setlistmanager.R;
import com.bigdrum.setlistmanager.database.Constants;
import com.bigdrum.setlistmanager.network.FindSongTask;
import com.bigdrum.setlistmanager.network.MySong;
import com.echonest.api.v4.SongParams;

public class SearchActivity extends Activity implements OnClickListener {
	
	private Handler handler;
	private EditText artistName;
	private EditText songTitle;
	private EditText songInfoPane;
    private CheckBox setlistSearchCheckbox;
	public static final String SONG_LIST = "songList";
	public static final String SONG_SELECTED = "songSelected";
	public static final int SEARCH_SONG = 1;
	private SongListAdapter listAdapter;
	private ArrayList<MySong> songList = new ArrayList<MySong>();
	private FindSongTask findSongTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		handler = new Handler();
		artistName = (EditText)findViewById(R.id.artistName);
		songTitle = (EditText)findViewById(R.id.songTitle);
		songInfoPane = (EditText)findViewById(R.id.songInfo);
        setlistSearchCheckbox = (CheckBox)findViewById(R.id.localSearchCheckBox);
		songInfoPane.setKeyListener(null);
        songInfoPane.requestFocus();
		
		createUIComponents();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	
	/**
	 * 
	 */
	private void createUIComponents() {
		((Button)findViewById(R.id.searchButton)).setOnClickListener(this);
		((Button)findViewById(R.id.search_cancel_button)).setOnClickListener(this);
		
		ListView songListView = (ListView)findViewById(R.id.searchResults);
		listAdapter = new SongListAdapter(this);
		songListView.setAdapter(listAdapter);

        if (!networkIsAvailable()) {
            songInfoPane.setText(R.string.no_network_connection);
            setlistSearchCheckbox.setChecked(true);
            setlistSearchCheckbox.setEnabled(false);
        }
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
		findSongTask = new FindSongTask(this, setlistSearchCheckbox.isChecked());
		findSongTask.execute(p);
	}
	
	
	
	/**
	 * 
	 */
	public void showSearchResultsOnUiThread(final ArrayList<MySong> songs, final boolean isLocal) {
		
		handler.post(new Runnable() {

			@Override
			public void run() {
				if (songs != null) {
					songInfoPane.setText("");
					songList.clear();
					songList.addAll(songs);
					listAdapter.notifyDataSetChanged();
					if (isLocal) {
                        songInfoPane.setText(R.string.local_songs);
                    }
                    else {
                        songInfoPane.setText(R.string.remote_songs);
                    }
				}
				else {
					songInfoPane.setText(R.string.nothing_found);
				}
			}
			
		});
	}


    /**
     * Detects if there is a WAN connection available
     * @return true if network available
     */
    private boolean networkIsAvailable() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
	
//	
//	@Override 
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
//	  super.onActivityResult(requestCode, resultCode, data); 
//	  switch(requestCode) { 
//	    case (SEARCH_SONG) : { 
//	      if (resultCode == Activity.RESULT_OK) { 
//	    	  MySong selectedSong= (MySong)data.getExtras().get(SONG_SELECTED);
//	    	  songInfoPane.setText(selectedSong.toString());
//	      } 
//	      break; 
//	    } 
//	  } 
//	}
	
	
	/**
	 * 
	 * @author andrew
	 *
	 */
	class SongListAdapter extends ArrayAdapter<MySong> {

		SongListAdapter(Context context) {
			super(context, R.layout.song_row_layout, R.id.songInformation, songList);
		}
		

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = null;
			LayoutInflater inflater = getLayoutInflater();

			row = inflater.inflate(R.layout.song_row_layout, parent,
					false);

			TextView songInformation = (TextView)row.findViewById(R.id.songInformation);
			final MySong thisSong = (MySong)songList.get(position);
			songInformation.setText(thisSong.toString());
			Button selectButton = (Button) row.findViewById(R.id.addToSetList);

			selectButton.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d("setlistManager", "ITEM SELECTED");
					Intent resultIntent = new Intent();
					resultIntent.putExtra(Constants.SONG_SELECTED, thisSong);
					setResult(Activity.RESULT_OK, resultIntent);
					finish();
				}
			});

			return (row);
		}
	}


	@Override
	public void onClick(View v) {
		InputMethodManager inputManager = (InputMethodManager)            
				  getSystemService(Context.INPUT_METHOD_SERVICE); 
				    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),      
				    InputMethodManager.HIDE_NOT_ALWAYS);
				    
		if (v == findViewById(R.id.searchButton)) {
			doSearch();
		}
		else if (v == findViewById(R.id.search_cancel_button)) {
			boolean cancelled = true;
			if (findSongTask != null) {
				cancelled = findSongTask.cancel(true);
			}
			if (cancelled) {
				songInfoPane.setText(R.string.cancelled);
				Intent resultIntent = new Intent();
				resultIntent.putExtra(Constants.SONG_SELECTED, (String)null);
				setResult(Activity.RESULT_CANCELED, resultIntent);
				finish();
			}
		}
	}


    /**
     *
     * @param item the menuItem which was tapped
     * @return true if the menuItem was handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_help:
                displayHelpDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     *
     */
    private void displayHelpDialog() {
        HelpDialogFragment help = new HelpDialogFragment();

        help.setMessageAndTitle(getString(R.string.help_search_song),
                        getString(R.string.help_search_song_title));

//        help.show(getSupportFragmentManager(), "");
    }
}
