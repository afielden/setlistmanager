package com.bigdrum.setlistmanager.gig;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bigdrum.setlistmanager.R;
import com.bigdrum.setlistmanager.database.Constants;
import com.bigdrum.setlistmanager.database.DataService;
import com.bigdrum.setlistmanager.ui.setlistmanagement.SelectSetlist;
import com.bigdrum.setlistmanager.ui.setlistmanagement.Setlist;
import com.bigdrum.setlistmanager.venue.SelectVenue;
import com.bigdrum.setlistmanager.venue.Venue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddGig extends Activity implements OnClickListener, OnDateSetListener, OnTimeSetListener, OnFocusChangeListener {

	private Button selectVenueButton;
	private Button selectSetlistButton;
	private EditText nameEditText;
	private Venue selectedVenue;
	private Gig originalGig;
	private EditText dateEditText;
	private EditText timeEditText;
	private EditText venue;
	private EditText setlist;
	private Calendar myCalendar;
	private Setlist selectedSetlist;
	private String gigName;
	private DataService dbService;
	
	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_gig);
		
		myCalendar = Calendar.getInstance();
		nameEditText = (EditText)findViewById(R.id.gig_name_edit_text);
		(selectVenueButton = (Button)findViewById(R.id.gig_add_select_venue_button)).setOnClickListener(this);
		(selectSetlistButton = (Button)findViewById(R.id.gig_add_select_setlist_button)).setOnClickListener(this);
		venue = (EditText)findViewById(R.id.gig_add_venue);
		setlist = (EditText)findViewById(R.id.gig_add_setlist);
		dateEditText = (EditText)findViewById(R.id.gig_add_date_edittext);
		dateEditText.setInputType(InputType.TYPE_NULL);
		dateEditText.setOnClickListener(this);
		dateEditText.setOnFocusChangeListener(this);
		timeEditText = (EditText)findViewById(R.id.gig_add_time_edittext);
		timeEditText.setInputType(InputType.TYPE_NULL);
		timeEditText.setOnClickListener(this);
		timeEditText.setOnFocusChangeListener(this);
		
		dbService = DataService.getDataService(this);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.getParcelable(Constants.GIG) != null) {
			setGigDetails((Gig)extras.getParcelable(Constants.GIG));
		}
	}

	
	/**
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_gig, menu);
		
		return true;
	}
	

	
	/**
	 * 
	 */
	@Override
	public void onClick(View v) {
		if (v == selectSetlistButton) {
			Intent intent = new Intent(this, SelectSetlist.class);
	    	startActivityForResult(intent, Constants.SELECT_SETLIST);
		}
		else if (v == selectVenueButton) {
			Intent intent = new Intent(this, SelectVenue.class);
	    	startActivityForResult(intent, Constants.SELECT_VENUE);
		}
		else if (v == dateEditText) {
			new DatePickerDialog(AddGig.this, AddGig.this, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
		}
		else if (v == timeEditText) {
			int hour = 0;
			int minute = 0;
			new TimePickerDialog(this, this, hour, minute, true).show();
		}
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
			if (valid()) {
				String gigName = nameEditText.getText().toString();
				long gigId = -1;
				long venueId = -1;
				long setlistId = -1;
				
				if (originalGig != null) {
					gigName = originalGig.getName();
					gigId = originalGig.getId();
					venueId = originalGig.getVenueId();
					setlistId = originalGig.getSetlistId();
				}

				if (gigName != null) {
					gigName = nameEditText.getText().toString();
				}
				if (selectedVenue != null) {
					venueId = selectedVenue.getId();
				}
				if (selectedSetlist != null) {
					setlistId = selectedSetlist.getId();
				}
				
				Gig gig = new Gig(gigId, gigName, venueId, setlistId,
						dateEditText.getText().toString(), timeEditText.getText().toString(), this);
				intent.putExtra(Constants.GIG, gig);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
			return true;
			
		case R.id.action_cancel:
			setResult(Activity.RESULT_CANCELED, intent);
			finish();
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	/***************************************************
	 * 
	 * Handle all results from child activities
	 * 
	 ****************************************************/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Constants.SELECT_SETLIST:
			if (resultCode == Activity.RESULT_OK) {
				selectedSetlist = (Setlist)data.getExtras().get(Constants.SELECTED_SETLIST);
				setlist.setText(selectedSetlist.getName());
			}
			break;
		case Constants.SELECT_VENUE:
			if (resultCode == Activity.RESULT_OK) {
				selectedVenue = (Venue)data.getParcelableExtra(Constants.VENUE);
				venue.setText(selectedVenue.getName());

				if (nameEditText.getText() == null || nameEditText.getText().length() == 0) {
					nameEditText.setText(selectedVenue.getName());
				}
			}
			break;
		}
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	private boolean valid() {
		if (nameEditText.getText() == null || nameEditText.getText().toString().equals("")) {
			Toast.makeText(this, R.string.no_gig_name, Toast.LENGTH_LONG).show();
			return false;
		}
		if (selectedSetlist == null && originalGig == null) {
			Toast.makeText(this, R.string.no_set_list, Toast.LENGTH_LONG).show();
			return false;
		}
		if (selectedVenue == null && originalGig == null) {
			Toast.makeText(this, R.string.no_venue, Toast.LENGTH_LONG).show();
			return false;
		}
		if (dateEditText.getText() == null || dateEditText.getText().toString().equals("")) {
			Toast.makeText(this, R.string.no_date, Toast.LENGTH_LONG).show();
			return false;
		}
		if (timeEditText.getText() == null || timeEditText.getText().toString().equals("")) {
			Toast.makeText(this, R.string.no_time, Toast.LENGTH_LONG).show();
			return false;
		}
		
		return true;
	}
	
	

	/**
	 * 
	 * @param myCalendar
	 */
	private void updateDate(Calendar myCalendar) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String dateFormat = prefs.getString(Constants.prefs_date_format, getResources().getString(R.string.pref_default_date_format));
	    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());

	    dateEditText.setText(sdf.format(myCalendar.getTime()));
	}
	


	/**
	 * 
	 * @param view
	 * @param hourOfDay
	 * @param minute
	 */
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		timeEditText.setText(String.format("%02d", Integer.valueOf(hourOfDay)) + ":" + 
				String.format("%02d", Integer.valueOf(minute)));
	}


	/**
	 * 
	 * @param view
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 */
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateDate(myCalendar);
	}


	/**
	 * 
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (!hasFocus) {
			return;
		}
		
		if (v == dateEditText) {
			new DatePickerDialog(AddGig.this, AddGig.this, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
		}
		else if (v == timeEditText) {
			int hour = 0;
			int minute = 0;
			new TimePickerDialog(this, this, hour, minute, true).show();
		}
	}
	
	
	/**
	 * 
	 */
	public void setGigDetails(Gig gig) {
		
		originalGig = gig;
		
		nameEditText.setText(gig.getName());
		venue.setText(dbService.getVenueNameById(gig.getVenueId()));
		setlist.setText(dbService.getSetlistNameById(gig.getSetlistId()));
		dateEditText.setText(gig.getDate());
		timeEditText.setText(gig.getTime());
	}
	
	


}
