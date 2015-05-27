package com.bigdrum.metronomemate.ui.setlistmanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.webkit.WebView;

import com.bigdrum.metronomemate.MainActivity;
import com.bigdrum.metronomemate.R;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionItemTarget;

import java.util.List;

public class HelpDialogFragment extends DialogFragment implements View.OnClickListener, OnShowcaseEventListener {
	
	private String message;
	private String title;
	private WebView wv;
    private ShowcaseView showcaseView;
    private int[] allItems = {R.id.action_setlists, R.id.action_search, R.id.action_add_section, R.id.action_add,
        R.id.action_copy, R.id.action_edit, R.id.action_delete};

    private int[] setlistNoListItems = {R.id.action_add};
    private int[] setlist = {R.id.action_add, R.id.action_editmode};
    private int[] setlistEditMultipleSelected = {R.id.action_delete, R.id.action_editmode};
    private int[] setlistEditSingleSelected = {R.id.action_copy, R.id.action_edit, R.id.action_delete, R.id.action_editmode};
    private int[] setlistEditNoneSelected = {R.id.action_editmode};

    private int[] songlistNoListItems = {R.id.action_setlists, R.id.action_search, R.id.action_add_section, R.id.action_add};
    private int[] songlist = {R.id.action_setlists, R.id.action_search, R.id.action_add_section, R.id.action_add, R.id.action_editmode};
    private int[] songlistEditMultipleSelected = {R.id.action_setlists, R.id.action_copy, R.id.action_delete, R.id.action_editmode};
    private int[] songlistEditSingleSelected = {R.id.action_setlists, R.id.action_copy, R.id.action_edit, R.id.action_delete, R.id.action_editmode};
    private int[] songlistEditNoneSelected = {R.id.action_setlists, R.id.action_editmode};

    private int[] currentItemList = setlist;
    private int currentItem = 0;
    private Activity activity;
    private boolean dismiss = false;
    private boolean setlistMode = true;
    private boolean editMode = false;
    private int numberOfSelectedItems = 0;
    private boolean showcaseHelpAvailable = false;
    private int numberOfListItems = 0;
    private boolean displayShowcaseView;
	
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
                       showcaseView();
                   }
               });
        
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
     */
	private void showcaseView() {

        this.activity = getActivity();
//		ActionItemTarget target = new ActionItemTarget(this.getActivity(), R.id.action_add);
//
//		showcaseView = new ShowcaseView.Builder(this.getActivity())
//				.setTarget(target)
//				.setContentTitle("")
//				.hideOnTouchOutside()
//                .setStyle(R.style.CustomShowcaseTheme3)
//				.build();
//
//        showcaseView.overrideButtonClick(this);
//        showcaseView.setOnShowcaseEventListener(this);

        setShowcaseViewParameters();
	}


    /**
     *
     *
     */
    private void setShowcaseViewParameters() {
        String activityName = activity.getComponentName().getClassName();
        int msgId = 0;
        String helpMessage;

        showcaseHelpAvailable = true;

        if (activityName.contains("MainActivity")) {
            msgId = getMainActivityHelpItemId();
        }
        else {
            showcaseHelpAvailable = false;
        }

        if (displayShowcaseView) {
            ActionItemTarget target = new ActionItemTarget(activity, currentItemList[currentItem]);
            helpMessage = activity.getString(msgId) + "\n" + activity.getString(R.string.help_next_item);

            if (showcaseView == null) {
                showcaseView = new ShowcaseView.Builder(activity)
                        .setTarget(target)
                        .setContentTitle("")
                        .hideOnTouchOutside()
                        .setStyle(R.style.CustomShowcaseTheme3)
                        .build();
            }

            showcaseView.overrideButtonClick(this);
            showcaseView.setOnShowcaseEventListener(this);
            showcaseView.setContentText(helpMessage);
            showcaseView.setTarget(target);
            showcaseView.show();
        }
    }


    /**
     *
     * @return
     */
    private int getMainActivityHelpItemId() {

        int msgId = 0;

        if (setlistMode) {
            msgId = getSetlistModeHelpItem();
        }
        else {
            msgId = getSonglistModeHelpItem();
        }

        return msgId;
    }


    /**
     *
     * @return
     */
    private int getSetlistModeHelpItem() {

        int msgId = 0;

        switch (currentItemList[currentItem]) {
            case R.id.action_add:
                msgId = R.string.help_action_add_setlist;
                break;
            case R.id.action_editmode:
                msgId = R.string.help_action_setlist_editmode;
                break;
            case R.id.action_copy:
                msgId = R.string.help_action_copy;
                break;
            case R.id.action_edit:
                msgId = R.string.help_action_edit_setlist;
                break;
            case R.id.action_delete:
                msgId = R.string.help_action_delete_setlist;
                break;

            default:
                showcaseHelpAvailable = false;
                break;
        }
        return msgId;
    }


    /**
     *
     */
    private int getSonglistModeHelpItem() {

        int msgId = 0;

        switch (currentItemList[currentItem]) {
            case R.id.action_setlists:
                msgId = R.string.help_action_setlists;
                break;
            case R.id.action_search:
                msgId = R.string.help_action_song_search;
                break;
            case R.id.action_add_section:
                msgId = R.string.help_action_add_section;
                break;
            case R.id.action_add:
                msgId = R.string.help_action_manually_add_song;
                break;
            case R.id.action_copy:
                msgId = R.string.help_action_song_copy;
                break;
            case R.id.action_edit:
                msgId = R.string.help_action_song_edit;
                break;
            case R.id.action_delete:
                msgId = R.string.help_action_song_delete;
                break;
            case R.id.action_editmode:
                msgId = R.string.help_action_songlist_editmode_cancel;
                break;
        }

        return msgId;
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
     * @param setlistEdit
     */
    public void setSetlistMode(boolean setlistEdit) {

        this.setlistMode = setlistEdit;
        this.currentItem = 0;

        currentItemList = setlistEdit?setlist:songlist;
    }

    /**
     *
     * @param editMode
     */
    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        this.currentItem = 0;

        if (setlistMode) {
            currentItemList = editMode?setlistEditNoneSelected:setlist;
        }
        else {
            currentItemList = editMode?songlistEditNoneSelected:songlist;
        }
    }


    /**
     *
     */
    public void setNumberOfSelectedItems(int items) {
        this.numberOfSelectedItems = items;
        this.currentItem = 0;

        if (setlistMode) {
            if (numberOfSelectedItems == 0) {
                currentItemList = setlistEditNoneSelected;
            }
            else if (numberOfSelectedItems == 1) {
                currentItemList = setlistEditSingleSelected;
            }
            else {
                currentItemList = setlistEditMultipleSelected;
            }
        }
        else {
            if (numberOfSelectedItems == 0) {
                currentItemList = songlistEditNoneSelected;
            }
            else if (numberOfSelectedItems == 1) {
                currentItemList = songlistEditSingleSelected;
            }
            else {
                currentItemList = songlistEditMultipleSelected;
            }
        }
    }


    /**
     *
     * @param listItems
     */
    public void setNumberOfListItems(int listItems) {
        this.numberOfListItems = listItems;

        if (numberOfListItems == 0) {
            currentItemList = setlistMode?setlistNoListItems:songlistNoListItems;
        }
        else {
            currentItemList = setlistMode?setlist:songlist;
        }
    }


    /**
     *
     * @param show
     */
    public void setDisplayShowcaseView(boolean show) {
        this.displayShowcaseView = show;
    }


    @Override
    public void onClick(View v) {
        dismiss = true;
        showcaseView.hide();
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
        showcaseView.show();
    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

        if (dismiss) {
            dismiss = false;
            return;
        }

        currentItem++;
        if (currentItem >= currentItemList.length) {
            currentItem = 0;
        }


        setShowcaseViewParameters();
        showcaseView.show();
    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {
    }
}
