package com.bigdrum.metronomemate.ui.setlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigdrum.metronomemate.R;
import com.bigdrum.metronomemate.database.Constants;
import com.bigdrum.metronomemate.network.MySong;

public class EnterSongDetails extends Activity implements OnItemSelectedListener {
	
	private MySong originalSong;
	private EditText songName;
	private EditText artist;
	private EditText tempo;
	private EditText timeSig;
	private int key;
	private boolean subset;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_song_details);
		handleInput(getIntent());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.enter_song_details, menu);
		return true;
	}
	
	
	/**
	 * 
	 */
	private void handleInput(Intent intent) {
		songName = (EditText)findViewById(R.id.enter_song_songname);
		artist = (EditText)findViewById(R.id.enter_song_artist);
		tempo = (EditText)findViewById(R.id.enter_song_tempo);
		timeSig = (EditText)findViewById(R.id.enter_song_timesig);
		Spinner spinner = (Spinner) findViewById(R.id.enter_song_key);
		
		originalSong = intent.getParcelableExtra(Constants.SONG_ENTERED);
		if (originalSong != null) {
			songName.setText(originalSong.getSongTitle());
			artist.setText(originalSong.getArtist());
			tempo.setText(String.valueOf(originalSong.getTempo()));
			timeSig.setText(String.valueOf(originalSong.getTimeSignature()));
			key = originalSong.getKey();
			
			if (originalSong.getArtist().equals("<subset>")) {
				subset = true;
				artist.setVisibility(View.INVISIBLE);
				tempo.setVisibility(View.INVISIBLE);
				timeSig.setVisibility(View.INVISIBLE);
				((TextView)findViewById(R.id.artist_label)).setVisibility(View.INVISIBLE);
				((TextView)findViewById(R.id.tempo_label)).setVisibility(View.INVISIBLE);
				((TextView)findViewById(R.id.timesig_label)).setVisibility(View.INVISIBLE);
				((Spinner)findViewById(R.id.enter_song_key)).setVisibility(View.INVISIBLE);
			}
		}
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.keys_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(key);
		spinner.setOnItemSelectedListener(this);
	}
	
	
	/**
	 * 
	 * @return
	 */
	private MySong createSong() {
		int position = -1;
		int setlistCount = 1;
		long id = -1;
		
		if (originalSong != null) {
			position = originalSong.getPosition();
			setlistCount = originalSong.getSetlistCount();
			id = originalSong.getId();
		}
		
		MySong song = new MySong(id, songName.getText().toString(), artist.getText().toString(), 
				Double.parseDouble(tempo.getText().toString()), Integer.parseInt(timeSig.getText().toString()), key, setlistCount, position);
		return song;
	}

	
	/**
	 * 
	 * @return
	 */
	private boolean isValid() {
		String songNameStr = songName.getText().toString();
		String tempoStr = tempo.getText().toString();
		String timeSigStr = timeSig.getText().toString();
		
		if (songNameStr == null || songNameStr.equals("")) {
			Toast.makeText(this, R.string.missing_song_name, Toast.LENGTH_SHORT).show();
			return false;
		}
		else if (!subset && (tempoStr == null || tempoStr.equals(""))) {
			Toast.makeText(this, R.string.missing_tempo, Toast.LENGTH_SHORT).show();
			return false;
		}
		else if (!subset && (timeSigStr == null || timeSigStr.equals(""))) {
			Toast.makeText(this, R.string.missing_timesig, Toast.LENGTH_SHORT).show();
			return false;
		}
		else if (!subset && (Integer.valueOf(timeSigStr) < 1 || Integer.valueOf(timeSigStr) > 12)) {
			Toast.makeText(this, R.string.invalid_timesig, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Intent intent = new Intent();
		
		switch (item.getItemId()) {
		case R.id.action_ok:
			if (isValid()) {
				intent.putExtra(Constants.SONG_ENTERED, createSong());
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
			break;
			
		case R.id.action_cancel:
			intent.putExtra(Constants.SONG_ENTERED, (String)null);
			setResult(Activity.RESULT_CANCELED, intent);
			finish();
			break;
		}
		
		return true;
	}
	

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		key = position;
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
}
