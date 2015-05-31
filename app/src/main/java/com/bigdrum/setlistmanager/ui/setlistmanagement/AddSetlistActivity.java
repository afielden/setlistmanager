package com.bigdrum.setlistmanager.ui.setlistmanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigdrum.setlistmanager.R;
import com.bigdrum.setlistmanager.database.Constants;
import com.bigdrum.setlistmanager.database.DataService;

public class AddSetlistActivity extends Activity {
	
	
	private DataService dbService;
	private String originalSetlistName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_setlist);
//		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bundle extras = getIntent().getExtras();
		EditText editText = (EditText)findViewById(R.id.set_list_name);
		TextView textView = (TextView)findViewById(R.id.setlist_name_textview);

		if (extras != null && (originalSetlistName = extras.getString(Constants.ORIGINAL_SETLIST_NAME)) != null) {
			editText.setText(originalSetlistName);
			textView.setText(R.string.edit_setlist_text);
		}
		
		dbService = DataService.getDataService(this);
	}

	
	/**
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_setlist, menu);
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
			String setListName = ((EditText)findViewById(R.id.set_list_name)).getText().toString();
			if (setListName == null || setListName.equals("")) {
				Toast.makeText(this, R.string.null_set_list, Toast.LENGTH_LONG).show();
				return true;
			}
			intent.putExtra(Constants.NEW_SETLIST_NAME, setListName);
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
