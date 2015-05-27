package com.bigdrum.metronomemate.venue;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bigdrum.metronomemate.R;
import com.bigdrum.metronomemate.database.Constants;
import com.bigdrum.metronomemate.database.DataService;

public class SelectVenue extends Activity {

	private List<Venue> venueList;
	private Venue selectedVenue;
	private View selectedView;
	private MenuItem okAction;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_venue);
		
		createVenueView();
	}

	/**
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_venue, menu);
		
		okAction = (MenuItem)menu.findItem(R.id.action_ok);
		okAction.setVisible(false);
		
		return true;
	}
	
	
	/**
	 * 
	 */
	private void createVenueView() {
		DataService dbService = DataService.getDataService(this);
		
		venueList = dbService.readAllVenues();
		ArrayAdapter<Venue> arrayAdapter = new ArrayAdapter<Venue>(this, R.layout.select_setlist_row_layout, R.id.select_setlist_name, venueList);
		ListView listView = (ListView)findViewById(R.id.select_venue_listview);
		listView.setAdapter(arrayAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
				selectedVenue = venueList.get(position);
				if (selectedView != null) {
					selectedView.setBackgroundColor(Color.TRANSPARENT);
				}
				selectedView = view;
				view.setSelected(true);
				view.setBackgroundColor(Color.GREEN);
				okAction.setVisible(true);
			}
		});
	}
	
	
	
	
	/**
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		intent.putExtra(Constants.VENUE, selectedVenue);
		
		switch (item.getItemId()) {
		case R.id.action_ok:
			setResult(Activity.RESULT_OK, intent);
			finish();
			return true;
			
		case R.id.action_cancel:
			setResult(Activity.RESULT_CANCELED, intent);
			finish();
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
