package com.bigdrum.setlistmanager.info;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.bigdrum.setlistmanager.R;

public class AboutFragment extends Fragment {
	
	private View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.activity_about_fragment, container, false);
		WebView wv = (WebView)rootView.findViewById(R.id.webView);
		wv.loadData(getString(R.string.help_welcome), "text/html", "utf-8");
		wv.getSettings().setDefaultTextEncodingName("utf-8");
		
		return rootView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}


}
