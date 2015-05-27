package com.bigdrum.metronomemate.venue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.bigdrum.metronomemate.R;
import com.bigdrum.metronomemate.database.Constants;

public class AddVenue extends Activity {

	private Venue originalVenue;

	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_venue);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.getParcelable(Constants.VENUE) != null) {
			setVenueDetails((Venue)extras.getParcelable(Constants.VENUE));
		}
	}
	
	
	/**
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_venue, menu);
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
			long venueId = -1;
			long lastGigId = -1;
			String lastGigDate = "";
			if (originalVenue != null) {
				venueId = originalVenue.getId();
				lastGigId = originalVenue.getLastGigId();
				lastGigDate = originalVenue.getLastGigDate();
			}
			
			Venue venue = new Venue(venueId, ((EditText)findViewById(R.id.add_venue_name)).getText().toString(),
					((EditText)findViewById(R.id.add_venue_street)).getText().toString(),
					((EditText)findViewById(R.id.add_venue_town)).getText().toString(),
					((EditText)findViewById(R.id.add_venue_postcode)).getText().toString(),
					((EditText)findViewById(R.id.add_venue_country)).getText().toString(),
					((EditText)findViewById(R.id.add_venue_contact_name)).getText().toString(),
					((EditText)findViewById(R.id.add_venue_phone)).getText().toString(),
					((EditText)findViewById(R.id.add_venue_email)).getText().toString(),
					lastGigId, lastGigDate);
			
			if (valid(venue)) {
				intent.putExtra(Constants.VENUE, venue);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
			break;
			
		case R.id.action_cancel:
			intent.putExtra(Constants.VENUE, (String)null);
			setResult(Activity.RESULT_CANCELED, intent);
			finish();
			
		default:
			return super.onOptionsItemSelected(item);
		}
		
		return true;
	}

	
	/**
	 * 
	 * @param venue
	 */
	private void setVenueDetails(Venue venue) {
		originalVenue = venue;
		
		((EditText)findViewById(R.id.add_venue_name)).setText(venue.getName());
		((EditText)findViewById(R.id.add_venue_street)).setText(venue.getStreet());
		((EditText)findViewById(R.id.add_venue_town)).setText(venue.getTown());
		((EditText)findViewById(R.id.add_venue_postcode)).setText(venue.getPostcode());
		((EditText)findViewById(R.id.add_venue_country)).setText(venue.getCountry());
		((EditText)findViewById(R.id.add_venue_contact_name)).setText(venue.getContactName());
		((EditText)findViewById(R.id.add_venue_phone)).setText(venue.getPhone());
		((EditText)findViewById(R.id.add_venue_email)).setText(venue.getEmail());
	}
	
	
	/**
	 * 
	 * @param venue
	 * @return
	 */
	private boolean valid(Venue venue) {
		
		if (venue.getName() == null || venue.getName().equals("")) {
			Toast.makeText(this, R.string.missing_venue_name, Toast.LENGTH_LONG).show();
			return false;
		}
		
		return true;
	}

}
