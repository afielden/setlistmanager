package com.bigdrum.setlistmanager.gig;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigdrum.setlistmanager.MainActivity2;
import com.bigdrum.setlistmanager.R;
import com.bigdrum.setlistmanager.database.Constants;
import com.bigdrum.setlistmanager.database.DataService;
import com.bigdrum.setlistmanager.database.DataServiceException;
import com.bigdrum.setlistmanager.export.Mail;
import com.bigdrum.setlistmanager.export.SelectRecipients;
import com.bigdrum.setlistmanager.ui.setlistmanagement.ConfirmationDialogFragment;
import com.bigdrum.setlistmanager.ui.setlistmanagement.HelpDialogFragment;
import com.bigdrum.setlistmanager.ui.setlistmanagement.Model;
import com.bigdrum.setlistmanager.utils.Utils;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionItemTarget;
//import com.facebook.Request;
//import com.facebook.Response;
//import com.facebook.Session;
//import com.facebook.SessionState;
//import com.facebook.UiLifecycleHelper;
//import com.facebook.model.GraphObject;
//import com.facebook.model.GraphUser;
//import com.facebook.widget.FacebookDialog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GigManagementFragment extends Fragment implements OnItemClickListener, OnClickListener, ConfirmationDialogFragment.ConfirmationDialogCallback {

	private DataService dbService;
	private ArrayAdapter<Gig> arrayAdapter;
	private View rootView;
	private ListView listview;
	private EditText gigDetails;
	private StringBuilder details;
	private Menu actionMenu;
	private Gig selectedGig;
	private File attachmentFile;
	private SharedPreferences pref;
	private ImageButton facebookButton;
//	private UiLifecycleHelper uiHelper;
	private boolean canPresentShareDialog;
//	private GraphUser user;
	private PendingAction pendingAction = PendingAction.NONE;
	private HelpDialogFragment help;
	private View selectedView;
    private TextView gigDetailsTitle;


//	private Session.StatusCallback callback = new Session.StatusCallback() {
//        @Override
//        public void call(Session session, SessionState state, Exception exception) {
//            onSessionStateChange(session, state, exception);
//        }
//    };
	
    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }
    
	/**
	 * 
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.gig_management_fragment_layout, container, false);
//		createSession();
		listview = (ListView)rootView.findViewById(R.id.gig_listview);
		gigDetails = (EditText)rootView.findViewById(R.id.gig_details_textbox);
        gigDetailsTitle = (TextView)rootView.findViewById(R.id.gig_details_title);
        gigDetailsTitle.setVisibility(View.INVISIBLE);
		/*facebookButton = (ImageButton)rootView.findViewById(R.id.facebook_button);
		facebookButton.setOnClickListener(this);*/
		dbService = DataService.getDataService(this.getActivity());
		dbService.init();
		
		help = new HelpDialogFragment();
        help.setDisplayShowcaseView(false);
		populateGigs();
		
		return rootView;
	}
	
	
	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		uiHelper = new UiLifecycleHelper(getActivity(), callback);
//        uiHelper.onCreate(savedInstanceState);
//        canPresentShareDialog = FacebookDialog.canPresentShareDialog(getActivity(),
//                FacebookDialog.ShareDialogFeature.SHARE_DIALOG);
		setHasOptionsMenu(true);
	}
	
	
	/**
	 * 
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.main, menu);
		actionMenu = menu;
		setActionItemVisibility();
		gigDetails.setText("");
	}


	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

        if (listview != null) {
            populateGigs();
        }

		if (isVisibleToUser) {
			if (arrayAdapter != null && arrayAdapter.getCount() == 0) {
				showHelp();
			}
		}
		else {
		}
	}


    /**
     *
     */
    private void showHelp() {
        ActionItemTarget target = new ActionItemTarget(getActivity(), R.id.action_add);

        new ShowcaseView.Builder(getActivity())
                .setTarget(target)
                .setContentTitle("")
                .hideOnTouchOutside()
                .setStyle(R.style.CustomShowcaseTheme3)
                .setTarget(target)
                .setContentText(R.string.help_no_gigs)
                .build();


    }

	
	
	/**
	 * 
	 */
	private void setActionItemVisibility() {
		actionMenu.findItem(R.id.action_copy).setVisible(false);
		actionMenu.findItem(R.id.action_add).setVisible(true).setTitle(R.string.tooltip_add_gig);
		actionMenu.findItem(R.id.action_setlists).setVisible(false);
		actionMenu.findItem(R.id.action_add_section).setVisible(false);
		actionMenu.findItem(R.id.action_editmode).setVisible(false);
		actionMenu.findItem(R.id.action_edit).setVisible(false);
		actionMenu.findItem(R.id.action_delete).setVisible(false);
		actionMenu.findItem(R.id.action_play).setVisible(false);
		actionMenu.findItem(R.id.action_search).setVisible(false);
		actionMenu.findItem(R.id.action_email).setVisible(false);
	}
	
	
	/**
	 * 
	 */
	private void populateGigs() {


        arrayAdapter = new GigCustomAdapter(getActivity(), R.layout.gig_row_layout, R.id.gig_name);
        listview.setAdapter(arrayAdapter);

        arrayAdapter.addAll(dbService.readAllGigs());
        arrayAdapter.notifyDataSetChanged();

		listview.setOnItemClickListener(this);
	}
	
	
	/**
	 * Handles venue item click event
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		if (selectedView != null) {
			selectedView.setBackgroundColor(Color.TRANSPARENT);
		}
		
		selectedGig = arrayAdapter.getItem(position);
		setGigDetails(selectedGig);
		selectedView = view;
		selectedView.setBackgroundColor(Color.GREEN);
        selectedView.setBackground(getActivity().getDrawable(R.drawable.gradient_vertical_selected));
		
		actionMenu.findItem(R.id.action_edit).setVisible(true).setTitle(R.string.tooltip_edit_gig);
		actionMenu.findItem(R.id.action_delete).setVisible(true).setTitle(R.string.tooltip_delete_gig);
        actionMenu.findItem(R.id.action_email).setVisible(true).setTitle(R.string.tooltip_email_gig);
	}
	
	
	/***************************************************
	 * 
	 * Handle all results from child activities
	 * 
	 ****************************************************/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Constants.ADD_GIG:
			if (resultCode == Activity.RESULT_OK) {
				Gig gig = (Gig)data.getExtras().get(Constants.GIG);
				dbService.addGig(gig);
				dbService.updateVenueGigDate(gig);
                populateGigs();
			}
			else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(this.getActivity(), R.string.cancelled, Toast.LENGTH_LONG).show();
			}
			break;
			
		case Constants.EDIT_GIG:
			if (resultCode == Activity.RESULT_OK) {
				Gig gig = (Gig)data.getExtras().get(Constants.GIG);
				dbService.updateGig(gig);
                dbService.updateVenueGigDate(gig);
                populateGigs();
				selectedGig = gig;
				setGigDetails(gig);
				arrayAdapter.notifyDataSetChanged();
			}
			else if (resultCode == Activity.RESULT_CANCELED) {
				arrayAdapter.notifyDataSetChanged();
				Toast.makeText(this.getActivity(), R.string.cancelled, Toast.LENGTH_LONG).show();
			}
			break;
			
		case Constants.SELECT_RECIPIENTS:
			if (resultCode == Activity.RESULT_OK) {
				String[] emails = data.getStringArrayExtra(Constants.SELECTED_CONTACTS);
				new EmailGigDetails().execute(emails);
			}
			else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(this.getActivity(), R.string.cancelled, Toast.LENGTH_LONG).show();
			}
			break;
			
		case Constants.SEND_EMAIL:
			String msg = "Email delivery failure";
			if (resultCode == Activity.RESULT_OK) {
				msg = "Email sent successfully";
			}
			Toast.makeText(this.getActivity(), msg, Toast.LENGTH_LONG).show();
		}
	}
	
	
	/*************************************************
	 * 
	 * Handle action click events
	 * 
	 *************************************************/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    	
	    case R.id.action_search:
	    	showSearchBar();
//	    	getActivity().onSearchRequested();
//	    	getActivity().startSearch("hello", false, null, false);
	    	return true;
	    	
	    case R.id.action_add:
	    	addGig();
            setActionItemVisibility();
	    	return true;
	    	
	    case R.id.action_edit:
	    	editGig();
	    	return true;
	    	
	    case R.id.action_delete:
			ConfirmationDialogFragment.showConfirmationDialog(getResources().getString(R.string.confirm_delete_gig),
					getResources().getString(R.string.confirm), this);
	    	return true;
	    	
	    case R.id.action_email:
	    	if (emailPrefsOk()) {
	    		selectRecipients();
	    	}
	    	return true;
	    	
	    case R.id.action_help:
	    	displayHelpDialog();
	    	return true;
	    	
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	

	
	/**
	 * 
	 */
	private void displayHelpDialog() {
		MainActivity2 activity = (MainActivity2)getActivity();
		HelpDialogFragment help = activity.getHelpDialogFragment();

		help.setMessageAndTitle(getString(R.string.help_gig_mgmt), 
				getString(R.string.help_gig_mgmt_title));

		help.setDisplayShowcaseView(true);
        help.setGigSelected(selectedGig != null);
//        help.setCurrentlyDisplayedTab(Constants.GIG_TAB_INDEX);
		help.show(getActivity().getSupportFragmentManager(), "");
	}


    /**
     *
     *
     */
    public void unSelectGig() {

        gigDetailsTitle.setVisibility(View.INVISIBLE);
        selectedGig = null;
    }
	
	
	/**
	 * 
	 */
	private void showSearchBar() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		menuItems.add(actionMenu.findItem(R.id.action_add));
		menuItems.add(actionMenu.findItem(R.id.action_search));
		Utils.showSearchBar(getActivity(), actionMenu, menuItems, arrayAdapter);
		
/*		ActionBar actionBar = getActivity().getActionBar();
	    // add the custom view to the action bar
	    actionBar.setCustomView(R.layout.actionbar_search);
	    EditText search = (EditText) actionBar.getCustomView().findViewById(R.id.search_edittext);
	    search.setOnEditorActionListener(new OnEditorActionListener() {

	      @Override
	      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	        return false;
	      }
	    });
	    
	    actionMenu.findItem(R.id.action_add).setVisible(false);
	    actionMenu.findItem(R.id.action_search).setVisible(false);
	    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
	    */
	}
	
	
	/**
	 * 
	 */
	private void addGig() {
		Intent intent = new Intent(getActivity(), AddGig.class);
    	startActivityForResult(intent, Constants.ADD_GIG);
	}
	
	
	/**
	 * 
	 */
	private void editGig() {
		
		Intent intent = new Intent(getActivity(), AddGig.class);
		intent.putExtra(Constants.GIG, selectedGig);
    	startActivityForResult(intent, Constants.EDIT_GIG);
	}
	
	
	/**
	 * 
	 */
	private void deleteGig() {

        if (selectedView != null) {
            selectedView.setBackgroundColor(Color.TRANSPARENT);
        }

		gigDetails.setText("");
		dbService.deleteGig(selectedGig);
		arrayAdapter.clear();
		arrayAdapter.addAll(dbService.readAllGigs());
		arrayAdapter.notifyDataSetChanged();
		
		if (arrayAdapter.getCount() == 0) {
			setActionItemVisibility();
		}
	}


	/**
	 *
	 */
	@Override
	public void positiveButtonClicked() {

		deleteGig();
		setActionItemVisibility();

	}


	/**
	 *
	 */
	@Override
	public void negativeButtonClicked() {

	}



	/**
	 * 
	 * @return
	 */
	private boolean emailPrefsOk() {
		pref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
	   	String user = pref.getString(Constants.prefs_email_user, "");
	   	if (user == null || user.equals("")) {
	   		Toast.makeText(getActivity(), R.string.email_user_not_set, Toast.LENGTH_LONG).show();
	   		return false;
	   	}
	   	
	   	return true;
	}
	
	
	/**
	 * 
	 */
	private void selectRecipients() {
		Intent intent = new Intent(getActivity(), SelectRecipients.class);
		intent.putExtra(Constants.GIG, selectedGig);
    	startActivityForResult(intent, Constants.SELECT_RECIPIENTS);
	}
	
	
	/**
	 * 
	 * @param songs
	 * @return
	 */
	private List<Model> createHeadings(List<Model> songs, StringBuilder content) {
		List<Model> subsets = new ArrayList<Model>();
		
		if (!songs.get(0).isSubsetItem()) {
			songs.add(0, new Model(-1, 0, getString(R.string.setlist), "<subset>", 0, 0, 0, 1, -1, 0));
			for (Model song : songs) {
				song.setPosition(song.getPosition() + 1);
			}
		}
		
		for (Model song : songs) {
			if (song.isSubsetItem()) {
				subsets.add(song);
			}
		}
		
		content.append("<table style=\"width:100%\">");
		content.append("	<tr>");
		
		for (Model subset : subsets) {
			content.append("<th align=\"center\">" + subset.getName() + "</th>");
		}
		
		content.append("	</tr>");
		
		return subsets;
	}
	
	
	/**
	 * 
	 * @param songs
	 * @param subsets
	 * @param content
	 */
	private void createRows(List<Model> songs, List<Model> subsets, StringBuilder content) {
		Map<String, List<Model>> subsetMap = new HashMap<String, List<Model>>();
		
		if (subsets.size() == 0) {
			subsetMap.put(getString(R.string.setlist), new ArrayList<Model>());
		}
		
		List<Model> subsetSonglist = null;
		for (Model song : songs) {
			if (song.isSubsetItem()) {
				subsetSonglist = new ArrayList<Model>();
				subsetMap.put(song.getName(), subsetSonglist);
			}
			else {
				subsetSonglist.add(song);
			}
		}
		
		int row = 1;
		boolean done = false;
		
		while (!done) {
			done = true;
			int col = 0;
			content.append("	<tr>");
			for (Model subset : subsets) {
				if (row <= subsetMap.get(subset.getName()).size()) {
					done = false;
					Model song = subsetMap.get(subset.getName()).get(row - 1);
					int position = Integer.valueOf(song.getPosition()).intValue();
					content.append("<td align=\"left\" style=\"width: 30%;\">" + String.valueOf(position-col) + ". " + song.getName() + "</td>");
				}
				else {
					content.append("<td align=\"left\" style=\"width: 30%;\"></td>");
				}
				col++;
			}
			content.append("	</tr>");
			row++;
		}
	}
	
	
	/**
	 * 
	 */
	private File buildEmailContent() {
		try {
			List<Model> songs = dbService.getSongsInSetlist(selectedGig.getSetlistId());
			String bandName = pref.getString(Constants.prefs_band_name, "");
			StringBuilder setlist = new StringBuilder(Constants.SETLIST_HEADING.replace("[%bandname%]", 
					bandName.toUpperCase(Locale.getDefault())).
					replace("[%venue%]", dbService.getVenueNameById(selectedGig.getVenueId())).
					replace("[%date%]", selectedGig.getDate()).
					replace("[%time%]", selectedGig.getTime()));
			List<Model> subsets = createHeadings(songs, setlist);
			createRows(songs, subsets, setlist);
			setlist.append(Constants.SETLIST_FOOTER);
			
			File folder = getActivity().getFilesDir();
			File file = null;
			try {
				file = createTempFile(folder, "temp_file.html", setlist.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return file;
			
		} catch (DataServiceException e) {
			Log.e(Constants.APP_NAME, "Error building gig email content ", e);
			Toast.makeText(getActivity(), e.getReason(), Toast.LENGTH_SHORT).show();
			return null;
		}
	}
	
	
	/**
	 * 
	 * @param folder
	 * @param filename
	 * @param fileContent
	 * @return
	 * @throws IOException
	 */
   private File createTempFile(File folder, String filename, String fileContent) throws IOException {
        File attachmentsDir = new File(getActivity().getFilesDir(), "attachments");
        attachmentsDir.mkdir();
        attachmentFile = new File(attachmentsDir, filename);
        attachmentFile.createNewFile();
        BufferedWriter buf = new BufferedWriter(new FileWriter(attachmentFile));
        buf.append(fileContent);
        buf.close();
        return attachmentFile;
    }
	
   
   /**
    * 
    * @param emails
    */
   private Boolean emailGigDetails(String[] emails) {
	   	pref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
	   	String user = pref.getString(Constants.prefs_email_user, "");
	   	String password = pref.getString(Constants.prefs_email_password, "");
	   	Mail m = new Mail(user, password); 
       
      	m.set_to(emails); 
      	m.set_from(user); 
      	m.set_subject("Gig details: " + dbService.getVenueNameById(selectedGig.getVenueId()) + " " + selectedGig.getDate()); 
      	m.setBody("Email body."); 
 
      try { 
        m.addAttachment(buildEmailContent().getAbsolutePath());
 
        if(m.send()) {
          return Boolean.TRUE;
        } else {
          return Boolean.FALSE;
        } 
      } 
      catch(Exception e) {
          Log.d("SetlistManager", "failed to send email:" + e.toString());
        return Boolean.FALSE;
      } 
   }
	
	/**
	 * 
	 * @param emails
	 */
/*	private void sendGigToRecipients(String[] emails) {
		HtmlFile emailText = buildEmailContent();
		Uri contentUri = FileProvider.getUriForFile(getActivity(), Constants.AUTHORITY, attachmentFile);
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("text/html");
//		emailIntent.setDataAndType(contentUri, "text/html");
//        emailIntent.putExtra(android.content.Intent.EXTRA_CC, emailCc);
		emailIntent.putExtra(Intent.EXTRA_EMAIL, emails);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Gig");
//        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(emailText));
//      emailIntent.putExtra(Intent.EXTRA_TEXT, "Gig details");
//      emailIntent.putExtra(Intent.EXTRA_STREAM, emailText.getFilePath());
//      emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, contentUri);
      emailIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
      emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

//      PackageManager packageManager = getActivity().getPackageManager();
//      List<ResolveInfo> activities = packageManager.queryIntentActivities(emailIntent, 0);
//      boolean isIntentSafe = activities.size() > 0;
      
//      startActivity(emailIntent);
        startActivity(Intent.createChooser(emailIntent, "email"));
	}*/
	
	
	/**
	 * 
	 * @param gig
	 */
	private void setGigDetails(Gig gig) {
		if (gig == null) {
			gigDetails.setText("");
            gigDetailsTitle.setVisibility(View.INVISIBLE);
			return;
		}

        gigDetailsTitle.setVisibility(View.VISIBLE);
		details = new StringBuilder();
		String venueName = dbService.getVenueNameById(gig.getVenueId());
		String setlistName = dbService.getSetlistNameById(gig.getSetlistId());
		details.append("Venue: ").append(venueName).append("\n");
		details.append("Date: ").append(gig.getDate()).append("\n");
		details.append("Time: ").append(gig.getTime()).append("\n");
		details.append("Setlist: ").append(setlistName).append("\n");
		gigDetails.setText(details.toString());
	}
	
	
	
	/**
	 * Class to execute email sending on a separate thread
	 * 
	 * @author andrew
	 *
	 */
	private class EmailGigDetails extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... emails) {
			return emailGigDetails(emails);
		}


        @Override
        protected void onPostExecute(Boolean ok) {

            if (ok) {
                Toast.makeText(getActivity(), R.string.email_success, Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getActivity(), R.string.email_failure, Toast.LENGTH_LONG).show();
            }
        }
	}


	/**
	 * 
	 */
	@Override
	public void onClick(View v) {
		if (v == facebookButton) {
//			postStatusUpdate();
		}
	}
	
	
	/**
	 * 
	 * @return
	 */
//    private FacebookDialog.ShareDialogBuilder createShareDialogBuilder() {
//        return new FacebookDialog.ShareDialogBuilder(getActivity())
//                .setName("Hello Facebook")
//                .setDescription("The 'Hello Facebook' sample application showcases simple Facebook integration")
//                .setLink("http://developers.facebook.com/android");
//    }
	
    
    
	/**
	 * 
	 */
//    private void postStatusUpdate() {
//        if (canPresentShareDialog) {
//            FacebookDialog shareDialog = createShareDialogBuilder().build();
//            uiHelper.trackPendingDialogCall(shareDialog.present());
//        } else if (hasPublishPermission()) {
////        	user.setFirstName("afielden");
//            final String message = "hello";
//            Request request = Request
//                    .newStatusUpdateRequest(Session.getActiveSession(), message, new Request.Callback() {
//                        @Override
//                        public void onCompleted(Response response) {
//                            showPublishResult(message, response.getGraphObject(), response.getError());
//                        }
//                    });
//            request.executeAsync();
//        } else {
//            pendingAction = PendingAction.POST_STATUS_UPDATE;
//        }
//    }
    
    
    /**
     * 
     * @param message
     * @param result
     * @param error
     */
//    private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
//        String title = null;
//        String alertMessage = null;
//        if (error == null) {
//            title = getString(R.string.success);
//            String id = result.cast(GraphObjectWithId.class).getId();
//            alertMessage = getString(R.string.successfully_posted_post, message, id);
//        } else {
//            title = getString(R.string.facebook_error);
//            alertMessage = error.getErrorMessage();
//        }
//
//        new AlertDialog.Builder(getActivity())
//                .setTitle(title)
//                .setMessage(alertMessage)
//                .setPositiveButton(R.string.ok, null)
//                .show();
//    }
    
    /**
     * 
     * @return
     */
//    private boolean hasPublishPermission() {
//        Session session = Session.getActiveSession();
//        return session != null && session.getPermissions().contains("publish_actions");
//    }
//
    
    /**
     * 
     * @param session
     * @param state
     * @param exception
     */
//    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
//        if (pendingAction != PendingAction.NONE &&
//                (exception instanceof FacebookOperationCanceledException ||
//                exception instanceof FacebookAuthorizationException)) {
//                new AlertDialog.Builder(getActivity())
//                    .setTitle(R.string.cancelled)
//                    .setMessage(R.string.facebook_permission_denied)
//                    .setPositiveButton(R.string.ok, null)
//                    .show();
//            pendingAction = PendingAction.NONE;
//        } else if (state == SessionState.OPENED_TOKEN_UPDATED) {
//            handlePendingAction();
//        }
//        updateUI();
//    }
    
    
    /**
     * 
     */
//    @SuppressWarnings("incomplete-switch")
//    private void handlePendingAction() {
//        PendingAction previouslyPendingAction = pendingAction;
//        // These actions may re-set pendingAction if they are still pending, but we assume they
//        // will succeed.
//        pendingAction = PendingAction.NONE;
//
//        switch (previouslyPendingAction) {
//            case POST_STATUS_UPDATE:
//                postStatusUpdate();
//                break;
//        }
//    }

    
    /**
     * 
     */
//    private void updateUI() {
//        Session session = Session.getActiveSession();
//        boolean enableButtons = (session != null && session.isOpened());
//
//        facebookButton.setEnabled(enableButtons || canPresentShareDialog);
//    }
//
//
//    private Session createSession() {
//        Session activeSession = Session.getActiveSession();
//        if (activeSession == null || activeSession.getState().isClosed()) {
//            activeSession = new Session(getActivity());
//            Session.setActiveSession(activeSession);
//        }
//        return activeSession;
//    }
    
    /**
     * 
     * @author andrew
     *
     */
//    private interface GraphObjectWithId extends GraphObject {
//        String getId();
//    }
}
