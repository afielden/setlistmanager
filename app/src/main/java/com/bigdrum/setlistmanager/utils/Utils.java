package com.bigdrum.setlistmanager.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.bigdrum.setlistmanager.R;

public class Utils {
	
	public static <T> void showSearchBar(Activity activity, Menu actionMenu, List<MenuItem> menuItems, final ArrayAdapter<T> arrayAdapter) {
		ActionBar actionBar = activity.getActionBar();
		final List<T> originalList = new ArrayList<T>();
		for (int i=0; i<arrayAdapter.getCount(); i++) {
			originalList.add(arrayAdapter.getItem(i));
		}
		arrayAdapter.clear();
	    actionBar.setCustomView(R.layout.actionbar_search);
	    EditText search = (EditText) actionBar.getCustomView().findViewById(R.id.search_edittext);
	    
/*	    search.setOnEditorActionListener(new OnEditorActionListener() {

	      @Override
	      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	        return false;
	      }
	    });*/
	    
	    search.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DEL) {
					arrayAdapter.clear();
					arrayAdapter.notifyDataSetChanged();
				}
				
				return true;
			}
	    	
	    });
	    
	    TextWatcher inputTextWatcher = new TextWatcher() {
	        public void afterTextChanged(Editable s) { }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after)
	            { }
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
//	            Log.d(TAG, s.charAt(count-1) + " character to send");;   
	        	for (T item : originalList) {
					if (item.toString().startsWith(s.toString())) {
						if (arrayAdapter.getPosition(item) < 0) {
							arrayAdapter.add(item);
						}
					}
				}
	        	
	        	arrayAdapter.notifyDataSetChanged();
	        }
	    };
	    
	    search.addTextChangedListener(inputTextWatcher);
	    
	    search.setFocusable(true);
	    search.setFocusableInTouchMode(true);
	    
	    for (MenuItem menuItem : menuItems) {
	    	menuItem.setVisible(false);
	    }
	    
	    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
	    
	    
	}

}
