package com.bigdrum.setlistmanager.venue;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bigdrum.setlistmanager.R;
import com.bigdrum.setlistmanager.database.Constants;
import com.bigdrum.setlistmanager.database.DataService;
import com.bigdrum.setlistmanager.database.DataServiceException;
import com.bigdrum.setlistmanager.ui.setlistmanagement.HelpDialogFragment;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionItemTarget;

public class VenueManagementFragment extends Fragment implements OnItemClickListener{
	
	private DataService dbService;
	private List<Venue> venueList;
	private ArrayAdapter<Venue> arrayAdapter;
	private View rootView;
	private ListView venueListview;
	private EditText venueDetails;
	private StringBuilder details;
	private Menu actionMenu;
	private Venue selectedVenue;
	private HelpDialogFragment help;
	private View selectedView;

	/**
	 * 
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.venue_management_fragment_layout, container, false);
		
		venueListview = (ListView)rootView.findViewById(R.id.venue_listview);
		venueDetails = (EditText)rootView.findViewById(R.id.venue_details_textbox);
		dbService = DataService.getDataService(this.getActivity());
		dbService.init();
		
		help = new HelpDialogFragment();
		populateVenues();
		
		return rootView;
	}
	
	
	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
	}
	
	
	/**
	 * 
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.main, menu);
		actionMenu = menu;
		setActionItemVisibility();
		venueDetails.setText("");
	}




    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            // The user may have added a new venue via the 'add gig' tab, so repopulate the listview
            if (venueListview != null) {
                populateVenues();
            }

            if (arrayAdapter != null && arrayAdapter.getCount() == 0) {
                showHelp();
            }
        }
        else {
        }
    }
	
	
	/**
	 * 
	 */
	private void setActionItemVisibility() {
		actionMenu.findItem(R.id.action_copy).setVisible(false);
		actionMenu.findItem(R.id.action_add).setVisible(true).setTitle(R.string.tooltip_add_venue);
		actionMenu.findItem(R.id.action_add_section).setVisible(false);
		actionMenu.findItem(R.id.action_setlists).setVisible(false);
		actionMenu.findItem(R.id.action_editmode).setVisible(false);
		actionMenu.findItem(R.id.action_edit).setVisible(false);
        actionMenu.findItem(R.id.action_delete).setVisible(false);
		actionMenu.findItem(R.id.action_play).setVisible(false);
		actionMenu.findItem(R.id.action_search).setVisible(false);
		actionMenu.findItem(R.id.action_email).setVisible(false);
	}
	
	
	/**
	 * 
	 */
	private void populateVenues() {


        arrayAdapter = new ArrayAdapter<Venue>(getActivity(), R.layout.venue_row_layout, R.id.artist_label);
        venueListview.setAdapter(arrayAdapter);

        arrayAdapter.addAll(dbService.readAllVenues());
        arrayAdapter.notifyDataSetChanged();

		venueListview.setOnItemClickListener(this);
	}



    /**
     *
     */
    private void showHelp() {
        ActionItemTarget target = new ActionItemTarget(getActivity(), R.id.action_add);

        new ShowcaseView.Builder(getActivity())
                .setTarget(target)
                .setContentTitle("")
                .hideOnTouchOutside()
                .setStyle(R.style.CustomShowcaseTheme3)
                .setTarget(target)
                .setContentText(R.string.help_no_venues)
                .build();


    }
	
	
	/*************************************************
	 * 
	 * Handle action click events
	 * 
	 *************************************************/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    	
	    case R.id.action_add:
	    	addVenue();
            setActionItemVisibility();
            venueListview.setFocusable(true);
	    	return true;
	    	
	    case R.id.action_edit:
	    	editVenue();
	    	return true;
	    	
	    case R.id.action_delete:
	    	if (deleteVenue()) {
                setActionItemVisibility();
            }
	    	return true;
	    	
	    case R.id.action_help:
	    	displayHelpDialog();
	    	return true;
	    	
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	
	
	/****************************************************
	 * 
	 * Handle all results from child activities
	 * 
	 ****************************************************/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Constants.ADD_VENUE:
			if (resultCode == Activity.RESULT_OK) {
				Venue venue = (Venue)data.getParcelableExtra(Constants.VENUE);
                dbService.addVenue(venue);
				venueDetails.setText("");
                if (selectedView != null) {
                    selectedView.setBackgroundColor(Color.TRANSPARENT);
                }

                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                populateVenues();
			}
			else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(this.getActivity(), R.string.cancelled, Toast.LENGTH_LONG).show();
			}
			break;
			
		case Constants.EDIT_VENUE:
			if (resultCode == Activity.RESULT_OK) {
                Venue venue = (Venue)data.getExtras().get(Constants.VENUE);
				setVenueDetails(venue);
				dbService.updateVenue(venue);
                venueDetails.setText("");
                if (selectedView != null) {
                    selectedView.setBackgroundColor(Color.TRANSPARENT);
                }
				populateVenues();
			}
			else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(this.getActivity(), R.string.cancelled, Toast.LENGTH_LONG).show();
			}
			break;
		}
	}
	
	
	/**
	 * 
	 */
	private void displayHelpDialog() {
		HelpDialogFragment help = new HelpDialogFragment();
		
		help.setMessageAndTitle(getString(R.string.help_venue_mgmt),
                getString(R.string.help_venue_mgmt_title));
		
//		help.show(getActivity().getFragmentManager(), "");
	}
	
	
	/**
	 * 
	 */
	private void addVenue() {
		Intent intent = new Intent(getActivity(), AddVenue.class);
    	startActivityForResult(intent, Constants.ADD_VENUE);
	}

	
	/**
	 * 
	 */
	private boolean deleteVenue() {
		try {

			dbService.deleteVenue(selectedVenue);
			if (selectedView != null) {
				selectedView.setBackgroundColor(Color.TRANSPARENT);
			}
			arrayAdapter.clear();
			arrayAdapter.addAll(dbService.readAllVenues());
			arrayAdapter.notifyDataSetChanged();
			venueDetails.setText("");
			
			if (arrayAdapter.getCount() == 0) {
				setActionItemVisibility();
			}
		} catch (DataServiceException e) {
			Toast.makeText(getActivity(), e.getReason(), Toast.LENGTH_LONG).show();
            return false;
		}

        return true;
	}
	
	
	/**
	 * 
	 */
	private void editVenue() {

		Intent intent = new Intent(getActivity(), AddVenue.class);
		intent.putExtra(Constants.VENUE, selectedVenue);
    	startActivityForResult(intent, Constants.EDIT_VENUE);
	}

	
	/**
	 * Handles venue item click event
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		if (selectedView != null) {
			selectedView.setBackgroundColor(Color.TRANSPARENT);
		}
		
		selectedVenue = arrayAdapter.getItem(position);
		setVenueDetails(selectedVenue);
		selectedView = view;
		selectedView.setBackgroundColor(Color.GREEN);
        selectedView.setBackground(getActivity().getDrawable(R.drawable.gradient_vertical_selected));

		actionMenu.findItem(R.id.action_edit).setVisible(true).setTitle(R.string.tooltip_edit_venue);
		actionMenu.findItem(R.id.action_delete).setVisible(true).setTitle(R.string.tooltip_delete_venue);
	}
	
	
	/**
	 * 
	 * @param venue
	 */
	private void setVenueDetails(Venue venue) {
		if (venue == null) {
			venueDetails.setText("");
			return;
		}
		
		details = new StringBuilder();
		details.append("Venue: ").append(venue.getName()).append("\n");
		details.append("Street: ").append(venue.getStreet()).append("\n");
		details.append("Town: ").append(venue.getTown()).append("\n");
		details.append("Country: ").append(venue.getCountry()).append("\n");
		details.append("Postcode: ").append(venue.getPostcode()).append("\n");
		details.append("Contact: ").append(venue.getContactName()).append("\n");
		details.append("Phone: ").append(venue.getPhone()).append("\n");
		details.append("Email: ").append(venue.getEmail()).append("\n");
		details.append("Last gig date: ").append(venue.getDate());
		venueDetails.setText(details.toString());
	}
}
