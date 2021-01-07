package com.bigdrum.setlistmanager.ui.setlistmanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.bigdrum.setlistmanager.R;
import com.bigdrum.setlistmanager.database.Constants;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionItemTarget;

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
    private int[] songlistEditNoneSelected = {R.id.action_editmode};

    private int[] gigsView = {R.id.action_add};
    private int[] gigsViewSelected = {R.id.action_email, R.id.action_add, R.id.action_edit, R.id.action_delete};

    private int[] venuesView = {R.id.action_add};
    private int[] venuesViewSelected = {R.id.action_add, R.id.action_edit, R.id.action_delete};

    private int[] currentItemList = setlistNoListItems;
    private int currentItem = 0;
    private Activity activity;
    private boolean dismiss = false;
    private boolean setlistMode = true;
    private boolean editMode = false;
    private int numberOfSelectedItems = 0;
    private boolean showcaseHelpAvailable = false;
    private int numberOfListItems = 0;
    private boolean displayShowcaseView;
    private int currentlyDisplayedTab;
	
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

        switch (currentlyDisplayedTab) {
            case Constants.SONG_SETLIST_TAB_INDEX:
                msgId = getSongSetlistTabHelpItemId();
                break;

            case Constants.GIG_TAB_INDEX:
                msgId = getGigTabHelpItemId();
                break;

            case Constants.VENUE_TAB_INDEX:
                msgId = getVenueTabHelpItemId();
                break;

            default:
                showcaseHelpAvailable = false;
        }

        if (displayShowcaseView && msgId != 0) {
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
    private int getSongSetlistTabHelpItemId() {

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
                msgId = editMode?R.string.help_action_setlist_editmode_cancel:R.string.help_action_setlist_editmode;
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
                msgId = editMode?R.string.help_action_songlist_editmode_cancel:R.string.help_action_songlist_editmode;
                break;
        }

        return msgId;
    }


    /**
     *
     * @return
     */
    private int getGigTabHelpItemId() {

        int msgId = 0;

        switch (currentItemList[currentItem]) {
            case R.id.action_add:
                msgId = R.string.help_action_gig_add;
                break;
            case R.id.action_email:
                msgId = R.string.help_action_gig_email;
                break;
            case R.id.action_edit:
                msgId = R.string.help_action_gig_edit;
                break;
            case R.id.action_delete:
                msgId = R.string.help_action_gig_delete;
                break;
        }

        return msgId;
    }


    /**
     *
     * @return
     */
    private int getVenueTabHelpItemId() {

        int msgId = 0;

        switch (currentItemList[currentItem]) {
            case R.id.action_add:
                msgId = R.string.help_action_venue_add;
                break;
            case R.id.action_edit:
                msgId = R.string.help_action_venue_edit;
                break;
            case R.id.action_delete:
                msgId = R.string.help_action_venue_delete;
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

        if (numberOfListItems == 0) {
            currentItemList = setlistEdit ? setlistNoListItems : songlistNoListItems;
        }
        else {
            currentItemList = setlistEdit ? setlist : songlist;
        }
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
     * @param gigSelected
     */
    public void setGigSelected(boolean gigSelected) {

        currentItemList = gigSelected?gigsViewSelected:gigsView;

    }


    /**
     *
     * @param venueSelected
     */
    public void setVenueSelected(boolean venueSelected) {

        currentItemList = venueSelected?venuesViewSelected:venuesView;
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
            if (editMode) {
                currentItemList = setlistMode ? setlistEditNoneSelected : songlistEditNoneSelected;
            }
            else {
                currentItemList = setlistMode ? setlist : songlist;
            }
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


    /**
     *
     * @param tabIndex
     */
    public void setCurrentlyDisplayedTab(int tabIndex) {

        currentlyDisplayedTab = tabIndex;
        currentItem = 0;

        switch(tabIndex) {

            case Constants.SONG_SETLIST_TAB_INDEX:
                currentItemList = numberOfListItems == 0?setlistNoListItems:setlist;
                break;

            case Constants.GIG_TAB_INDEX:
                currentItemList = gigsView;
                break;

            case Constants.VENUE_TAB_INDEX:
                currentItemList = venuesView;
                break;

            case Constants.ABOUT_TAB_INDEX:
                break;

        }
    }
}
