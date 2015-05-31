package com.bigdrum.setlistmanager.export;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.bigdrum.setlistmanager.R;
import com.bigdrum.setlistmanager.database.Constants;
import com.bigdrum.setlistmanager.database.DataService;

public class SelectRecipients extends Activity implements OnItemClickListener, OnClickListener {
	  
	private ListAdapter adapter;
	private CustomAdapter listAdapter;
	private Cursor mCursor;
	private DataService dbService;
	private List<Contact> contacts;
	private Button okButton;
	private Button cancelButton;
	

	/** Called when the activity is first created. */
	@Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_select_recipients);
	    
	    dbService = DataService.getDataService(this);
	    contacts = dbService.getNameEmailDetails();
	    createListView();
	    (okButton = (Button)findViewById(R.id.select_recipient_ok_button)).setOnClickListener(this);
	    (cancelButton = (Button)findViewById(R.id.select_recipient_cancel_button)).setOnClickListener(this);
	  }

	  
	/**
	 * 
	 */
	private void createListView() {
		ListView listView = (ListView)findViewById(R.id.select_recipients_listview);
		/*ArrayAdapter<Contact> arrayAdapter = new ArrayAdapter<Contact>(this, android.R.layout.simple_list_item_multiple_choice, 
				contacts);*/
		listAdapter = new CustomAdapter(this, R.layout.checked_item_row_layout, R.id.checked_item_row_edittext, contacts, this);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(this);
	}

	  
	  
	  /**
	   * 
	   */
	  
	  public void onListItemClick(ListView listView, View view, int position, long id) {
		  Object o = adapter.getItem(position);
		  String email = mCursor.getString(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
		  System.out.println(email);
	  }


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		View v = arg1.findViewById(android.R.layout.activity_list_item);
		
	}


	/**
	 * 
	 */
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		String[] emails = null;
		int result = Activity.RESULT_CANCELED;
		
		if (v == okButton) {
			result = Activity.RESULT_OK;
			emails = new String[listAdapter.getNumberOfSelectedItems()];
			int index = 0;
			for (Contact contact : contacts) {
				if (contact.isSelected()) {
					emails[index++] = contact.getEmail();
				}
			}
			intent.putExtra(Constants.SELECTED_CONTACTS, emails);
		}
		
		setResult(result, intent);
		finish();
	}
}
