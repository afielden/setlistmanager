package com.bigdrum.setlistmanager.ui.setlistmanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.webkit.WebView;

import com.bigdrum.setlistmanager.R;

public class ConfirmationDialogFragment extends DialogFragment {

	private String message;
	private String title;
	private WebView wv;
	private boolean ok;
    private ConfirmationDialogCallback callback;


    public interface ConfirmationDialogCallback {
        public void positiveButtonClicked();
        public void negativeButtonClicked();
    }


    /**
	 * 
	 */
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       callback.positiveButtonClicked();
                   }
               });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callback.negativeButtonClicked();
            }
        });
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


	/**
	 *
	 * @param msg
	 * @param title
	 * @param activity
	 */
	public void setMessageAndTitleHtml(String msg, String title, Activity activity) {
		setMessageAndTitle(msg, title);
		
		wv = new WebView (activity.getBaseContext());
		wv.loadData(msg, "text/html", "utf-8");
		wv.setBackgroundColor(Color.WHITE);
		wv.getSettings().setDefaultTextEncodingName("utf-8");
	}


    /**
     *
     * @param callback
     */
    public void setFragmentCallbackClass(ConfirmationDialogCallback callback) {
        this.callback = callback;
    }



    /**
     *
     *
     */
    public static void showConfirmationDialog(String message, String title, ConfirmationDialogCallback fragment) {

        ConfirmationDialogFragment confirmDialog = new ConfirmationDialogFragment();

        confirmDialog.setMessageAndTitle(message, title);
        confirmDialog.setFragmentCallbackClass(fragment);

        confirmDialog.show(((Fragment)fragment).getActivity().getFragmentManager(), "");
    }
}
