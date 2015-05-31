package com.bigdrum.setlistmanager.ui.setlistmanagement;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bigdrum.setlistmanager.R;
import com.bigdrum.setlistmanager.database.Constants;
import com.bigdrum.setlistmanager.network.MySong;

public class SongListActivity extends Activity {

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		super.setContentView(R.layout.activity_song_list);

		Intent intent = getIntent();
		ArrayList<Parcelable> parcelableSongs = intent
				.getParcelableArrayListExtra(Constants.SONG_LIST);
		// Use your own layout
		ArrayAdapter<Parcelable> adapter = new ArrayAdapter<Parcelable>(this,
				R.layout.list_item_handle_left, R.id.songInformation, parcelableSongs);
		
		ListView songListView = (ListView)findViewById(R.id.songListView);
		
		songListView.setAdapter(new SongListAdapter(this));
	}

//	@Override
//	protected void onListItemClick(ListView l, View v, int position, long id) {
//		String item = (String) getListAdapter().getItem(position);
//		Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
//	}

//	@Override
//	public void onClick(View v) {
//		Log.d("beatbuddy", "SELECT SONG BUTTON CLICKED");
//	}

	
	/**
	 * 
	 * @author andrew
	 *
	 */
	class SongListAdapter extends ArrayAdapter<Parcelable> {
		
		private ArrayList<Parcelable> songs;

		SongListAdapter(Context context) {
			super(context, R.layout.list_item_handle_left, R.id.songInformation);
			this.songs = songs;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = null;
			LayoutInflater inflater = getLayoutInflater();

			row = inflater.inflate(R.layout.list_item_handle_left, parent,
					false);

			TextView songInformation = (TextView)row.findViewById(R.id.songInformation);
			final MySong thisSong = (MySong)songs.get(position);
			songInformation.setText(thisSong.toString());
			Button selectButton = (Button) row.findViewById(R.id.addToSetList);

			selectButton.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d("beatbuddy", "ITEM SELECTED");
					Intent resultIntent = new Intent();
					resultIntent.putExtra(Constants.SONG_SELECTED, thisSong);
					setResult(Activity.RESULT_OK, resultIntent);
					finish();
				}
			});

			return (row);
		}
	}
}
