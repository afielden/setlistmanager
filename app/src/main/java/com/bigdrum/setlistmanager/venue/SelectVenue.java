package com.bigdrum.setlistmanager.venue;

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
import android.widget.Toast;

import com.bigdrum.setlistmanager.R;
import com.bigdrum.setlistmanager.database.Constants;
import com.bigdrum.setlistmanager.database.DataService;

public class SelectVenue extends Activity {

	private List<Venue> venueList;
	private Venue selectedVenue;
	private View selectedView;
	private MenuItem okAction;
    private ArrayAdapter<Venue> arrayAdapter;

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
        final Activity activity = this;
		
		venueList = dbService.readAllVenues();
        arrayAdapter = new ArrayAdapter<Venue>(this, R.layout.select_setlist_row_layout, R.id.select_setlist_name, venueList);
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
                view.setBackground(activity.getDrawable(R.drawable.gradient_vertical_selected));
                okAction.setVisible(true);
            }
        });
	}


	/**
	 *
	 */
	private void addVenue() {
		Intent intent = new Intent(this, AddVenue.class);
		startActivityForResult(intent, Constants.ADD_VENUE);
	}
	
	
	/**
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		intent.putExtra(Constants.VENUE, selectedVenue);
		
		switch (item.getItemId()) {
			case R.id.action_add:
				addVenue();
				return true;

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (Constants.ADD_VENUE): {
                if (resultCode == Activity.RESULT_OK) {
                    addNewVenueToDb((Venue) data.getParcelableExtra(Constants.VENUE));
                }
                else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(this, R.string.cancelled, Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }


    private void addNewVenueToDb(Venue venue) {
        if (venue != null) {
            if (DataService.getDataService(this).getVenueIdByName(venue.getName()) > -1) {
                Toast.makeText(this, R.string.duplicate_set_list_name, Toast.LENGTH_LONG).show();
                return;
            }

            DataService.getDataService(this).addVenue(venue);
            arrayAdapter.clear();
            arrayAdapter.addAll(DataService.getDataService(this).readAllVenues());
            arrayAdapter.notifyDataSetChanged();
        }
    }
}
