package com.bigdrum.metronomemate.ui.setlistmanagement;

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
import android.widget.LinearLayout;
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
	private EditText hoursEditText;
	private EditText minsEditText;
	private EditText secsEditText;
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
		hoursEditText = (EditText)findViewById(R.id.hours);
		minsEditText = (EditText)findViewById(R.id.mins);
		secsEditText = (EditText)findViewById(R.id.secs);
		
		originalSong = intent.getParcelableExtra(Constants.SONG_ENTERED);
		if (originalSong != null) {
			songName.setText(originalSong.getSongTitle());
			artist.setText(originalSong.getArtist());
			tempo.setText(String.valueOf(originalSong.getTempo()));
			timeSig.setText(String.valueOf(originalSong.getTimeSignature()));
			key = originalSong.getKey();
            initialiseDuration(originalSong.getDuration());
			
			if (originalSong.getArtist().equals("<subset>")) {
				subset = true;
				artist.setVisibility(View.INVISIBLE);
				tempo.setVisibility(View.INVISIBLE);
				timeSig.setVisibility(View.INVISIBLE);
				((TextView)findViewById(R.id.artist_label)).setVisibility(View.INVISIBLE);
				((TextView)findViewById(R.id.tempo_label)).setVisibility(View.INVISIBLE);
				((TextView)findViewById(R.id.timesig_label)).setVisibility(View.INVISIBLE);
                ((TextView)findViewById(R.id.song_key_label)).setVisibility(View.INVISIBLE);
				((Spinner)findViewById(R.id.enter_song_key)).setVisibility(View.INVISIBLE);
                ((LinearLayout)findViewById(R.id.song_duration_layout)).setVisibility(View.INVISIBLE);
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
     * Initialise the selectedSong duration text fields hh:mm:ss
     * @param seconds : total selectedSong time in seconds
     */
    private void initialiseDuration(double seconds) {

        String[] time = SongDetailsFragment.formatTime(seconds).split(":");

        hoursEditText.setText(time[0]);
        minsEditText.setText(time[1]);
        secsEditText.setText(time[2]);
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
				Double.parseDouble(tempo.getText().toString()), Integer.parseInt(timeSig.getText().toString()), key, setlistCount,
                position, convertTimeToSeconds());
		return song;
	}


    /**
     *
     * @return seconds
     */
    private double convertTimeToSeconds() {

        Integer hours = Integer.valueOf(hoursEditText.getText().toString());
        Integer mins = Integer.valueOf(minsEditText.getText().toString());
        Integer secs = Integer.valueOf(secsEditText.getText().toString());

        Integer totalSeconds = (hours * 3600) + (mins * 60) + secs;

        return totalSeconds.doubleValue();

    }

	
	/**
	 * 
	 * @return
	 */
	private boolean isValid() {
		String songNameStr = songName.getText().toString();
		String tempoStr = tempo.getText().toString();
		String timeSigStr = timeSig.getText().toString();
		String hourStr = hoursEditText.getText().toString();
		String minStr = minsEditText.getText().toString();
		String secStr = secsEditText.getText().toString();
		
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
		else if (!subset && (hourStr == null || Integer.valueOf(hourStr) < 0 || Integer.valueOf(hourStr)> 24
                || Integer.valueOf(minStr) < 0 || Integer.valueOf(minStr) > 60
                || Integer.valueOf(secStr) < 0 || Integer.valueOf(secStr) > 60)) {
			Toast.makeText(this, R.string.invalid_duration, Toast.LENGTH_SHORT).show();
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
