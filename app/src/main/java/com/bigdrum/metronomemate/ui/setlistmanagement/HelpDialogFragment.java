package com.bigdrum.metronomemate.ui.setlistmanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;

public class HelpDialogFragment extends DialogFragment {
	
	private String message;
	private String title;
	private WebView wv;
	
	/**
	 * 
	 */
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // FIRE ZE MISSILES!
                   }
               });
//               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                   public void onClick(DialogInterface dialog, int id) {
//                       // User cancelled the dialog
//                   }
//               });
        // Create the AlertDialog object and return it
        
        if (wv != null) {
        	builder.setView(wv);
        }
        else {
        	builder.setMessage(message);
        }
        
        return builder.create();
    }
	
	
	/**
	 * 
	 * @param msg
	 */
	public void setMessageAndTitle(String msg, String title) {
		this.message = msg;
		this.title = title;
	}

	
	public void setMessageAndTitleHtml(String msg, String title, Activity activity) {
		setMessageAndTitle(msg, title);
		
		wv = new WebView (activity.getBaseContext());
		wv.loadData(msg, "text/html", "utf-8");
		wv.setBackgroundColor(Color.WHITE);
		wv.getSettings().setDefaultTextEncodingName("utf-8");
	}
}
