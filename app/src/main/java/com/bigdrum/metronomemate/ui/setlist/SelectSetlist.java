package com.bigdrum.metronomemate.ui.setlist;

import java.util.ArrayList;
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

import com.bigdrum.metronomemate.R;
import com.bigdrum.metronomemate.database.Constants;
import com.bigdrum.metronomemate.database.DataService;

public class SelectSetlist extends Activity {
	
	private List<Model> setlists;
	private Model selectedSetlist;
	private View selectedView;
	private MenuItem okAction;
    private ArrayAdapter<Model> arrayAdapter;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_setlist);
		
		createSetlistView();
	}
	

	
	/**
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_setlist, menu);
		
		okAction = (MenuItem)menu.findItem(R.id.action_ok);
		okAction.setVisible(false);
		
		return true;
	}

	
	/**
	 * 
	 */
	private void createSetlistView() {
		DataService dbService = DataService.getDataService(this);
		
		setlists = filterOutSourceSetlist(dbService.getAllSetlists());
        arrayAdapter = new ArrayAdapter<Model>(this, R.layout.select_setlist_row_layout, R.id.select_setlist_name, setlists);
		ListView listView = (ListView)findViewById(R.id.select_setlist_listview);
		listView.setAdapter(arrayAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
				selectedSetlist = setlists.get(position);
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
	 * Remove the source setlist from the list, if required
	 * @return
	 */
	private List<Model> filterOutSourceSetlist(List<Model> setlists) {

		List<Model> filteredSetlist = new ArrayList<Model>();
		long setlistId = getIntent().getExtras().getLong(Constants.SETLISTPRIMARYKEY);

		for (Model model : setlists) {
			if (model.getId() != setlistId) {
				filteredSetlist.add(model);
			}
		}

		return filteredSetlist;
	}


	/**
	 *
	 */
	private void addSetlist() {
		Intent intent = new Intent(this, AddSetlistActivity.class);
		startActivityForResult(intent, Constants.ADD_NEW_SETLIST);
	}
	

	/**
	 * 
	 * @param v
	 */
/*	@Override
	public void onClick(View v) {
		int result = Activity.RESULT_CANCELED;
		Intent intent = new Intent();
		
		if (v.getId() == R.id.select_setlist_ok) {
			result = Activity.RESULT_OK;
			Setlist setlist = new Setlist(selectedSetlist.getName(), selectedSetlist.getPosition(), selectedSetlist.getId());
			intent.putExtra(Constants.SELECTED_SETLIST, setlist);
		}
		
		setResult(result, intent);
		finish();
	}*/
	
	
	/**
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		
		switch (item.getItemId()) {
			case R.id.action_add:
				addSetlist();
                return true;

            case R.id.action_ok:
                setResult(Activity.RESULT_OK, intent);
                Setlist setlist = new Setlist(selectedSetlist.getName(), selectedSetlist.getPosition(), selectedSetlist.getId());
                intent.putExtra(Constants.SELECTED_SETLIST, setlist);
                finish();
                return true;

            case R.id.action_cancel:
                setResult(Activity.RESULT_CANCELED, intent);
                intent.putExtra(Constants.SELECTED_SETLIST, (String)null);
                finish();

            default:
                return super.onOptionsItemSelected(item);
		}
	}


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (Constants.ADD_NEW_SETLIST): {
                if (resultCode == Activity.RESULT_OK) {
                    addNewSetlistToDb(data.getStringExtra(Constants.NEW_SETLIST_NAME));
                }
                else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(this, R.string.cancelled, Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }


    private void addNewSetlistToDb(String setlistName) {
        if (setlistName != null && setlistName.length() > 0) {
            if (DataService.getDataService(this).getSetlistIdByName(setlistName) > 0) {
                Toast.makeText(this, R.string.duplicate_set_list_name, Toast.LENGTH_LONG).show();
                return;
            }

            DataService.getDataService(this).addSetlistName(setlistName);
            arrayAdapter.clear();
            arrayAdapter.addAll(filterOutSourceSetlist(DataService.getDataService(this).getAllSetlists()));
            arrayAdapter.notifyDataSetChanged();
        }
    }
}
