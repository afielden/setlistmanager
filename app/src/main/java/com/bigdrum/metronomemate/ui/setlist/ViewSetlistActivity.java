package com.bigdrum.metronomemate.ui.setlist;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.bigdrum.metronomemate.R;
import com.bigdrum.metronomemate.database.Constants;
import com.bigdrum.metronomemate.database.DataService;

public class ViewSetlistActivity extends ListActivity {

	private Intent intent;
	private List<String> listOfSets;
	private ArrayAdapter<String> setlistAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_view_setlist);
		
		DataService dbService = DataService.getDataService(this);
//		listOfSets = dbService.getAllSetlists();
		setlistAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
				listOfSets);
		setListAdapter(setlistAdapter);
	}
	
	
	/**
	 * 
	 */
	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	finish();
	    	break;
	    case R.id.add_new_set_list:
	    	intent = new Intent(this, AddSetlistActivity.class);
	    	startActivityForResult(intent, Constants.ADD_NEW_SETLIST);
	      break;
	    
	    default:
	      break;
	    }

	    return true;
	  } 

	
	/**
	 * 
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (Constants.ADD_NEW_SETLIST): {
			String setListName = data.getStringExtra(Constants.NEW_SETLIST_NAME);
			if (setListName != null && setListName.length() > 0) {
				listOfSets.add(setListName);
				setlistAdapter.notifyDataSetChanged();
			}
			break;
			}
		}
	}
	
				
				
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_setlist, menu);
		return super.onCreateOptionsMenu(menu);

	}
}
