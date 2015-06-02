package com.bigdrum.setlistmanager.ui.setlistmanagement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigdrum.setlistmanager.R;
import com.bigdrum.setlistmanager.backup.DatabaseBackup;
import com.bigdrum.setlistmanager.database.Constants;
import com.bigdrum.setlistmanager.database.DataService;
import com.bigdrum.setlistmanager.database.DataServiceException;
import com.bigdrum.setlistmanager.network.MySong;
import com.bigdrum.setlistmanager.ui.setlistmanagement.ConfirmationDialogFragment.ConfirmationDialogCallback;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionItemTarget;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.DropListener;

public class MetronomeFragment extends Fragment implements OnClickListener, OnLongClickListener,
        ConfirmationDialogCallback, OnItemClickListener, AdapterView.OnItemLongClickListener{

	private View rootView;
	private ViewGroup beatsViewgroup;
	private Model selectedSong;
	private List<ImageView> beatViews;
	private int currentBeat;
	private Handler handler;
	private StartBeatsTask beatTask;
	private List<Model> itemList;
//	private List<MySong> songDetails;
	private DataService dbService;
//	private SimpleDragSortCursorAdapter setlistAdapter;
	private CustomAdapter listAdapter;
	private boolean setlistMode = true;
	private DragSortListView listView;
	private Model selectedSetlist;
	private boolean editMode;
	private Menu actionMenu;
	private ImageView playImage;
	private MenuItem copyAction;
	private MenuItem addAction;
	private MenuItem addSectionAction;
	private MenuItem backToSetlistsAction;
	private MenuItem editModeAction;
	private MenuItem editAction;
	private MenuItem deleteAction;
	private MenuItem searchAction;
	private MenuItem playAction;
	private MenuItem emailAction;
	private HelpDialogFragment help;
    private Activity thisActivity;
    private final HelpDialogFragment helpDialogFragment = new HelpDialogFragment();
    private DatabaseBackup dbBackupService;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.metronome_fragment_layout,
				container, false);

		/*if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
		    Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getActivity()));
		}*/
		
		help = new HelpDialogFragment();
		
//		playImage = (ImageView)rootView.findViewById(R.id.beat_play);
//		playImage.setVisibility(View.INVISIBLE);
//		playImage.setOnClickListener(this);
		itemList = new ArrayList<>();
		createUI();
		beatViews = new ArrayList<>();
		currentBeat = 0;
		handler = new Handler();

        this.thisActivity = getActivity();

        populateSetlistView();
        createItemlistListener();

//		rootView.setBackgroundColor(R.drawable.background);


        try {
            String packageName = getActivity().getPackageName();
            dbBackupService = new DatabaseBackup(dbService, getActivity().getPackageManager().getPackageInfo(packageName, 0).versionName, thisActivity);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return rootView;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	
	/**
	 * 
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.main, menu);
		this.actionMenu = menu;
		
		copyAction = actionMenu.findItem(R.id.action_copy);
		copyAction.setTitle(R.string.tooltip_copy_songs);
		addAction = actionMenu.findItem(R.id.action_add);
		addSectionAction = actionMenu.findItem(R.id.action_add_section);
		addSectionAction.setTitle(R.string.tooltip_add_subsection);	
		backToSetlistsAction = actionMenu.findItem(R.id.action_setlists);
		backToSetlistsAction.setTitle(R.string.tooltip_back_to_setlists);
		editModeAction = actionMenu.findItem(R.id.action_editmode);
		editAction = actionMenu.findItem(R.id.action_edit);
		deleteAction = actionMenu.findItem(R.id.action_delete);
		searchAction = actionMenu.findItem(R.id.action_search);
		searchAction.setTitle(R.string.tooltip_search_song);
		playAction = actionMenu.findItem(R.id.action_play);
		emailAction = actionMenu.findItem(R.id.action_email);
		emailAction.setTitle(R.string.tooltip_email_gig);
		
		reset();
		setActionItemVisibility();
	}


//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        view.post(new Runnable() {
//            @Override
//            public void run() {
//                ActionItemTarget target = new ActionItemTarget(thisActivity, R.id.action_editmode);
//                PointTarget pointTarget = new PointTarget(new Point(500,100));
//                new ShowcaseView.Builder(thisActivity)
//                        .setTarget(target)
////                        .setTarget(new ActionViewTarget(thisActivity, ActionViewTarget.Type.TITLE))
//                        .setContentTitle("ShowcaseView")
//                        .setContentText("This is highlighting the Home button")
//                        .hideOnTouchOutside()
//                        .build();
//            }
//        });
//    }




    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            // The user may have added a new setlist via the 'add gig' tab, so repopulate the listview
            if (listView != null) {
                populateSetlistView();
            }

            if (listAdapter != null && listAdapter.getCount() == 0) {
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
                .setContentText(R.string.help_no_setlists)
                .build();


    }

	
	/**
	 * 
	 */
	private void reset() {
		editMode = false;
		for (int itemNumber=0; itemNumber < listAdapter.getCount(); itemNumber++) {
			listAdapter.getItem(itemNumber).setSelected(false);
		}
		
		listAdapter.notifyDataSetChangedAndReset();
	}
	
	
	/**
	 * 
	 */
	private void createUI() {
//		populateSetlistView();
//		createItemlistListener();
		setlistMode(true);
		setSongInformationVisibility(View.INVISIBLE);
//		helpMessage(DataService.firstTimeUser);
		if (DataService.firstTimeUser) {
			displayWelcomeMessage();
			DataService.firstTimeUser = false;
		}
		
		((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
			hideSoftInputFromWindow(rootView.getWindowToken(),0);
		
//		else if (listAdapter.getCount() == 0) {
//			help.setMessageAndTitle(getString(R.string.help_no_setlists), 
//					getString(R.string.help_no_setlists_title));
//			help.show(getActivity().getFragmentManager(), "");
//		}
	}
	
	
	/**
	 * 
	 */
	private void displayWelcomeMessage() {
		help.setMessageAndTitleHtml(getString(R.string.help_welcome),
                getString(R.string.help_welcome_title), getActivity());
		help.show(getActivity().getSupportFragmentManager(), "");
	}
	
	
	/**
	 * 
	 */
	private void removeHelpMessage() {
		TextView help = (TextView)rootView.findViewById(R.id.no_setlists_textview);
		if (help != null) {
			((LinearLayout)help.getParent()).removeView(help);
		}
	}
	
	
	/**
	 * 
	 */
	private void populateSetlistView() {
		dbService = DataService.getDataService(this.getActivity());
		dbService.init();
		
		itemList = dbService.getAllSetlists();
		
		listView = (DragSortListView)rootView.findViewById(R.id.setlist_view);
		listView.setDragListener(null);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		listAdapter = new CustomAdapter(this.getActivity(), R.layout.list_item_handle_left, R.id.setlist_row_name, itemList, this);
		listView.setAdapter(listAdapter);
		
//		if (listAdapter.getCount() == 0) {
//			showHelp();
//		}
		
		DragSortController controller = new MyDragSortController(listView);
	    controller.setDragHandleId(R.id.setlist_row_handler);
	    controller.setRemoveEnabled(false);
	    controller.setSortEnabled(true);
	    controller.setDragInitMode(1);
	    
	    listView.setFloatViewManager(controller);
	    listView.setOnTouchListener(controller);
	    listView.setDragEnabled(true);
//	    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	    listView.setDropListener(createDropListener());
		
//		setlistAdapter = new SimpleDragSortCursorAdapter(this.getActivity(), 
//				R.layout.setlist_row_layout,
//				this.getActivity().getContentResolver().query(tableUri, null, null, null, null),
//				new String[] {Constants.SETLISTNAME},
//				new int[] {R.id.setlist_row_name},
//				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


	}

	
	
	/**
	 * 
	 *
	 */
	private DropListener createDropListener() {
		return new DragSortListView.DropListener()
		{
		    @Override
		    public void drop(int from, int to)
		    {
		        if (from != to)
		        {
		            Model item = listAdapter.getItem(from);
		            listAdapter.remove(item);
		            listAdapter.insert(item, to);

					listAdapter.clearAllCheckedItems();
			        
			        listAdapter.notifyDataSetChanged();
			        
			        // Re-order in the database too..
			        reorderItems();
		        }
		    }
		};
	}
	
	
	/**
	 * 
	 */
	private void reorderItems() {
		
		int index=0;
		for (Model item : itemList) {
			item.setPosition(index);
			index++;
		}
		
		if (setlistMode) {
			dbService.reorderSetlists(itemList);
		}
		else {
			dbService.reorderSongs(itemList, selectedSetlist);
		}
	}

	
	/**
	 * 
	 */
	private void createItemlistListener() {
//		ListView listView = (ListView)rootView.findViewById(R.id.setlist_view);
//		listView.setOnLongClickListener(this);

		listView.setOnItemClickListener(this);


        listView.setOnItemLongClickListener(this);
	}
	
	
	/**
	 * 
	 */
	private void displaySetlists() {
//		TextView heading = (TextView)rootView.findViewById(R.id.items_textview_title);
//		heading.setText(R.string.set_list_label);
//		setSongInformationVisibility(View.INVISIBLE);
//		playImage.setVisibility(View.INVISIBLE);
		listAdapter.clear();
		listAdapter.addAll(dbService.getAllSetlists());
		listAdapter.notifyDataSetChangedAndReset();
		if (listAdapter.getCount() == 0) {
			editAction.setVisible(false);
		}
	}
	
	
	/**
	 * 
	 *
	 */
	private void setSongInformationVisibility(int visibility) {
		if (beatsViewgroup != null) {
			beatsViewgroup.setVisibility(visibility);
			for (int c=0; c < beatsViewgroup.getChildCount(); c++) {
				beatsViewgroup.getChildAt(c).setVisibility(visibility);
			}
		}
	}
	
	
	/**
	 * 
	 */
	private void setlistMode(boolean setlistMode) {
		if (actionMenu != null) {
			backToSetlistsAction.setVisible(!setlistMode);
			
			if (setlistMode) {
				playAction.setVisible(false);
			}
			else {
				listAdapter.setSelectedItems(0);
			}
		}
		
		this.setlistMode = setlistMode;

        helpDialogFragment.setSetlistMode(setlistMode);
	}
	
	
	/**
	 * 
	 *
	 */
	private void setActionItemVisibility() {
		if (!editMode) {
			
			copyAction.setVisible(false);	
			addAction.setVisible(true);			
			addSectionAction.setVisible(false);		
			backToSetlistsAction.setVisible(!setlistMode);		
			editAction.setVisible(false);		
			deleteAction.setVisible(false);
			
    		clearAllSelectedItems();
			if (!setlistMode) {
				searchAction.setVisible(true);
			}
			else {
				searchAction.setVisible(false);
			}
		}
		
    	if (setlistMode) {
	    	addAction.setVisible(!editMode).setTitle(R.string.tooltip_add_setlist);
	    	editAction.setTitle(R.string.tooltip_edit_setlist);
	    	deleteAction.setTitle(R.string.tooltip_delete_setlist);
	    	searchAction.setVisible(false);
    	}
    	else {
    		addAction.setVisible(!editMode).setTitle(R.string.tooltip_add_song);
    		editAction.setTitle(R.string.tooltip_edit_song);
    		deleteAction.setTitle(R.string.tooltip_delete_song);
			addSectionAction.setVisible(!editMode);
	    	searchAction.setVisible(!editMode);
    	}
		
		playAction.setVisible(false);
		emailAction.setVisible(false);

        editModeAction.setTitle(editMode ? R.string.action_done : R.string.action_edit);
        editModeAction.setIcon(editMode?android.R.drawable.ic_menu_close_clear_cancel:android.R.drawable.ic_menu_manage);
		editModeAction.setVisible(listAdapter.getCount() != 0);

        helpDialogFragment.setNumberOfListItems(listAdapter.getCount());
	}
	
	
	/**
	 * 
	 *
	 */
	private void displaySongsInSetlist() {
		try {
			itemList.clear();
//			songDetails.clear();
			itemList.addAll(dbService.getSongsInSetlist(selectedSetlist.getId()));
//			songDetails.addAll(songs);
//			for (Model selectedSong : songs) {
//				itemList.add(new Model(selectedSong.getSongTitle(), selectedSong.getPosition()));
//			}
			
			listAdapter.notifyDataSetChangedAndReset();
//			TextView heading = (TextView)rootView.findViewById(R.id.items_textview_title);
//			heading.setText(selectedSetlist.getName());
			
//			if (listAdapter.getCount() == 0) {
//				help.setMessageAndTitle(getString(R.string.help_no_songs), 
//						getString(R.string.help_no_songs_title));
//				help.show(getActivity().getFragmentManager(), "");
//			}
			
		} catch (DataServiceException e) {
			Log.e(Constants.APP_NAME, "Error getting list of songs for set " + selectedSetlist.getName(), e);
			Toast.makeText(getActivity(), e.getReason(), Toast.LENGTH_SHORT).show();
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
            case R.id.action_copy:
                copySongs();
                return true;

            case R.id.action_search:
                searchForSong();
                return true;

            case R.id.action_add:
                if (setlistMode) {
                    addSetlist();
                }
                else {
                    addSong();
                }
                return true;

            case R.id.action_add_section:
                addSection();
                return true;

            case R.id.action_setlists:
                listAdapter.setSelectedPosition(-1);
                showSetlists();
                editMode = false;
                helpDialogFragment.setEditMode(editMode);
                actionMenu.findItem(R.id.action_editmode).setTitle(R.string.action_edit);
                setActionItemVisibility();
                return true;

            case R.id.action_play:
                controlBeats();
                return true;

            case R.id.action_editmode:
                editMode = !editMode;
                helpDialogFragment.setEditMode(editMode);
                editModeAction.setTitle(editMode?R.string.action_done:R.string.action_edit);
                setActionItemVisibility();
                listAdapter.clearAllCheckedItems();
                listAdapter.notifyDataSetChanged();
                return true;

            case R.id.action_delete:

                if (setlistMode) {
                    showConfirmationDialog(getResources().getString(R.string.confirm_delete_setlists));
                }
                else {
                    showConfirmationDialog(getResources().getString(R.string.confirm_delete_songs));
                }

                return true;
	    	
            case R.id.action_edit:
                if (setlistMode) {
                    editSetlist();
                }
                else {
                    editSong();
                }
                return true;

            case R.id.action_help:
                displayHelpDialog();
                return true;

            case R.id.action_backup:
//                Intent intent = new Intent(getActivity(), BackupActivity.class);
//                startActivityForResult(intent, Constants.BACKUP);
                dbBackupService.backupDatabase();
                return true;

            case R.id.action_restore:
                dbBackupService.restoreDatabase();

	    	
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	
	


	/****************************************************
	 * 
	 * Handle all results from child activities
	 * 
	 ****************************************************/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		
			case (Constants.SELECT_SETLIST) : {
				if (resultCode == Activity.RESULT_OK) {
					Setlist targetSetlist = (Setlist)data.getExtras().get(Constants.SELECTED_SETLIST);
					try {
						if (setlistMode) {
							dbService.copySetlist(itemList, targetSetlist.getId());
						}
						else {
							dbService.copySongs(itemList, targetSetlist.getId());
						}
					} catch (DataServiceException e) {
						Toast.makeText(getActivity(), e.getReason(), Toast.LENGTH_LONG).show();
					}

                    editMode = false;
					displaySetlists();
                    setActionItemVisibility();
//                    editModeAction.setTitle(R.string.action_edit);

                            String message = getString(R.string.copy_to_setlist, targetSetlist);
					Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
				}
				else if (resultCode == Activity.RESULT_CANCELED) {

					displaySetlists();

					Toast.makeText(getActivity(), R.string.cancelled, Toast.LENGTH_LONG).show();
				}
				break;
			}
			
			case (Constants.MANUALLY_ADD_SONG) : {
				if (resultCode == Activity.RESULT_OK) {
					MySong newSong = (MySong) data.getExtras().get(Constants.SONG_ENTERED);
					
					Model newSongModel = new Model(-1, selectedSetlist.getId(), newSong.getSongTitle(), newSong.getArtist(), 
							newSong.getTempo(), newSong.getTimeSignature(), newSong.getKey(), 1, -1, newSong.getDuration());
					
					try {
						dbService.addSongToSetlist(newSongModel, selectedSetlist.getId());
					} catch (DataServiceException e) {
						e.printStackTrace();
						Toast.makeText(getActivity(), e.getReason(), Toast.LENGTH_LONG).show();
					}
					itemList.add(newSongModel);
					listAdapter.notifyDataSetChangedAndReset();
					editModeAction.setVisible(true);
                    helpDialogFragment.setNumberOfListItems(listAdapter.getCount());
				}
				else if (resultCode == Activity.RESULT_CANCELED) {
					Toast.makeText(getActivity(), R.string.cancelled, Toast.LENGTH_LONG).show();
				}
				break;
			}
			
			case (Constants.EDIT_SONG) : {
				if (resultCode == Activity.RESULT_OK) {
					MySong newSong = (MySong) data.getExtras().get(Constants.SONG_ENTERED);
					
					Model newSongModel = new Model(newSong.getId(), selectedSetlist.getId(), newSong.getSongTitle(), newSong.getArtist(), newSong.getTempo(), 
							newSong.getTimeSignature(), newSong.getKey(), newSong.getSetlistCount(), newSong.getPosition(), newSong.getDuration());
					
					dbService.updateSong(newSongModel);
					displaySongsInSetlist();
				}
				else if (resultCode == Activity.RESULT_CANCELED) {
					Toast.makeText(getActivity(), R.string.cancelled, Toast.LENGTH_LONG).show();
				}
				break;
			}
			
			case (Constants.SEARCH_SONG): {
				if (resultCode == Activity.RESULT_OK) {
					MySong newSong = (MySong) data.getExtras().get(Constants.SONG_SELECTED);

					Model newSongModel = new Model(newSong.getId(), selectedSetlist.getId(), newSong.getSongTitle(), newSong.getArtist(),
							newSong.getTempo(), newSong.getTimeSignature(), newSong.getKey(), 1, -1, newSong.getDuration());
					
					try {
						if (dbService.addSongToSetlist(newSongModel, selectedSetlist.getId())) {
							itemList.add(newSongModel);
							listAdapter.notifyDataSetChangedAndReset();
                            Toast.makeText(getActivity(), R.string.song_added, Toast.LENGTH_LONG).show();
						}
                        else {
                            Toast.makeText(getActivity(), R.string.song_already_added, Toast.LENGTH_LONG).show();
                        }
						editModeAction.setVisible(true);
                        helpDialogFragment.setNumberOfListItems(listAdapter.getCount());

					} catch (DataServiceException e) {

						e.printStackTrace();
						Toast.makeText(getActivity(), e.getReason(), Toast.LENGTH_LONG).show();
					}
				}
				else if (resultCode == Activity.RESULT_CANCELED) {
					Toast.makeText(getActivity(), R.string.cancelled, Toast.LENGTH_LONG).show();
				}
				break;
			}
			
			
			case (Constants.ADD_NEW_SETLIST): {
				if (resultCode == Activity.RESULT_OK) {
					addNewSetlistToDb(data.getStringExtra(Constants.NEW_SETLIST_NAME));
					editModeAction.setVisible(true);
					helpDialogFragment.setNumberOfListItems(listAdapter.getCount());
					removeHelpMessage();
				}
				else if (resultCode == Activity.RESULT_CANCELED) {
					Toast.makeText(getActivity(), R.string.cancelled, Toast.LENGTH_LONG).show();
				}
				break;
			}
			
			case (Constants.EDIT_SETLIST): {
				if (resultCode == Activity.RESULT_OK) {
					changeSetlistName(data.getStringExtra(Constants.NEW_SETLIST_NAME));
				}
				else if (resultCode == Activity.RESULT_CANCELED) {
					Toast.makeText(getActivity(), R.string.cancelled, Toast.LENGTH_LONG).show();
				}
				break;
			}
		}
	}

	
	
	/**
	 * 
	 */
	private void displayHelpDialog() {

        if (setlistMode) {
	    	if (!editMode) {
				helpDialogFragment.setMessageAndTitle(getString(R.string.help_setlist_mgmt),
                        getString(R.string.help_setlist_mgmt_title));
	    	}
	    	else {
	    		helpDialogFragment.setMessageAndTitle(getString(R.string.help_setlist_edit),
                        getString(R.string.help_setlist_edit_title));
	    	}
		}
    	else {
    		if (!editMode) {
				helpDialogFragment.setMessageAndTitle(getString(R.string.help_songlist),
                        getString(R.string.help_songlist_title));
	    	}
	    	else {
	    		helpDialogFragment.setMessageAndTitle(getString(R.string.help_songlist_edit),
                        getString(R.string.help_songlist_edit_title));
	    	}
    	}

        helpDialogFragment.setDisplayShowcaseView(true);

		helpDialogFragment.show(getActivity().getSupportFragmentManager(), "");
	}
	
	
	
	/**
	 * 
	 */
	@SuppressWarnings("unused")
    private void createBeatIcons(Model selectedSong) {
		
//		currentBeat = 0;
//		beatsViewgroup = (ViewGroup)rootView.findViewById(R.id.beats_group_layout);
//		beatsViewgroup.removeAllViews();
//
//		beatsViewgroup.setVisibility(View.VISIBLE);
//		beatViews.clear();
//
//		if (!selectedSong.isSubsetItem()) {
//			playImage.setVisibility(View.VISIBLE);
//			beatsViewgroup.addView(playImage);
//		}
//
//		for (int beat = 1; beat <= selectedSong.getTimeSignature(); beat++) {
//			ImageView i = new ImageView(this.getActivity());
//			i.setImageResource(R.drawable.beat_off);
//			i.setAdjustViewBounds(true);
//			i.setVisibility(View.VISIBLE);
//			i.setEnabled(true);
//			beatsViewgroup.addView(i);
//			beatViews.add(i);
//		}
	}
	
	
	
	/**
	 * 
	 */
	private void clearAllSelectedItems() {
		for (Model item : itemList) {
			item.setSelected(false);
		}
		
		listAdapter.setSelectedItems(0);
	}
	
	
	/**
	 * 
	 */
	private void reorderUnselectedItems() {
		int position = 0;
		for (Model item : itemList) {
			if (!item.isSelected()) {
				item.setPosition(position++);
			}
		}
	}
	
	
	/**
	 * 
	 */
	private void addNewSetlistToDb(String setlistName) {
		if (setlistName != null && setlistName.length() > 0) {
			if (dbService.getSetlistIdByName(setlistName) > 0) {
				Toast.makeText(getActivity(), R.string.duplicate_set_list_name, Toast.LENGTH_LONG).show();
				return;
			}
			
			dbService.addSetlistName(setlistName);
			listAdapter.clear();
			itemList.addAll(dbService.getAllSetlists());
			listAdapter.notifyDataSetChangedAndReset();
		}
	}
	
	
	/**
	 * 
	 */
	private void changeSetlistName(String setlistName) {
		if (setlistName != null && setlistName.length() > 0) {
			dbService.changeSetlistName(setlistName, getSelectedSetlist());
		}
		
		listAdapter.clear();
		itemList.addAll(dbService.getAllSetlists());
		listAdapter.notifyDataSetChanged();
	}
	
	
	/**
	 * @throws DataServiceException 
	 * 
	 */
	private void deleteSetlists() throws DataServiceException {

        List<Model> remainingSetlists = dbService.deleteSetlists(itemList);

        reorderUnselectedItems();

        listAdapter.clear();
        listAdapter.addAll(remainingSetlists);
        listAdapter.notifyDataSetChanged();

        if (remainingSetlists.size() == 0) {
            editMode = false;
            setActionItemVisibility();
        }
	}
	
	
	/**
	 * @throws DataServiceException 
	 * 
	 */
	private void deleteSongs() throws DataServiceException {

        List<Model> remainingSongs = dbService.deleteSongs(itemList, selectedSetlist);
        listAdapter.clear();
        listAdapter.addAll(remainingSongs);
        listAdapter.notifyDataSetChangedAndReset();

        reorderUnselectedItems();
        if (remainingSongs.size() == 0) {
            editMode = false;
            setActionItemVisibility();
        }

        setSongInformationVisibility(View.INVISIBLE);
	}


	/**
	 *
	 *
	 */
	private void showConfirmationDialog(String message) {

		ConfirmationDialogFragment confirmDialog = new ConfirmationDialogFragment();

		confirmDialog.setMessageAndTitle(message, getResources().getString(R.string.confirm));
        confirmDialog.setFragmentCallbackClass(this);

		confirmDialog.show(getActivity().getFragmentManager(), "");
	}


    /**
     * Called by the delete confirmation dialog (activity), which is on a separate thread, so
     * we must use a handler to update the UI
     */
    public void positiveButtonClicked() {
        handler.post(new Runnable() {

            @Override
            public void run() {
                try {
                    if (!setlistMode) {
                        deleteSongs();
                    } else {
                        deleteSetlists();
                    }
                } catch (DataServiceException e) {
                    e.printStackTrace();
                }
            }

        });
    }


    /**
     *
     */
    public void negativeButtonClicked() {

    }
	
	
	/**
	 * 
	 */
	private void searchForSong() {
		if (beatTask != null) {
			beatTask.pause();
		}
		
		Intent intent = new Intent(getActivity(), SearchActivity.class);
		startActivityForResult(intent, Constants.SEARCH_SONG);
	}
	
	
	/**
	 * 
	 */
	private void addSong() {
		if (beatTask != null) {
			beatTask.pause();
		}
		
		Intent intent = new Intent(getActivity(), EnterSongDetails.class);
		startActivityForResult(intent, Constants.MANUALLY_ADD_SONG);
	}
	
	
	/**
	 * 
	 */
	private void editSong() {
		Intent intent = new Intent(getActivity(), EnterSongDetails.class);
		for (Model song : itemList) {
			if (song.isSelected()) {
				intent.putExtra(Constants.SONG_ENTERED, new MySong(song.getId(), song.getName(), song.getArtist(), song.getTempo(), 
						song.getTimeSignature(), song.getKey(), song.getSetlistCount(), song.getPosition(), song.getDuration()));
				startActivityForResult(intent, Constants.EDIT_SONG);
			}
		}
	}
	
	
	/**
	 * 
	 */
	private void copySongs() {
		if (beatTask != null) {
			beatTask.pause();
		}
		
		Intent intent = new Intent(getActivity(), SelectSetlist.class);

        if (setlistMode) {
            intent.putExtra(Constants.SETLISTPRIMARYKEY, getSelectedSetlist().getId());
        }
        else {
            intent.putExtra(Constants.SETLISTPRIMARYKEY, selectedSetlist.getId());
        }

		startActivityForResult(intent, Constants.SELECT_SETLIST);
	}
	
	
	/**
	 *
	 * 
	 */
	private void addSection() {
		try {
			Model newSubset = dbService.addSection(selectedSetlist);
			itemList.add(newSubset);
			listAdapter.notifyDataSetChangedAndReset();
		} catch (DataServiceException e) {
			e.printStackTrace();
			Toast.makeText(getActivity(), e.getReason(), Toast.LENGTH_LONG).show();
		}
	}
	
	
	/**
	 * 
	 */
	private void addSetlist() {
		if (beatTask != null) {
			beatTask.pause();
		}
		
		Intent intent = new Intent(getActivity(), AddSetlistActivity.class);
    	startActivityForResult(intent, Constants.ADD_NEW_SETLIST);
	}
	
	
	/**
	 * 
	 */
	private void editSetlist() {
		Model setlist = getSelectedSetlist();
		if (setlist == null) {
			return;
		}
		
		if (beatTask != null) {
			beatTask.pause();
		}
		
		Intent intent = new Intent(getActivity(), AddSetlistActivity.class);
		intent.putExtra(Constants.ORIGINAL_SETLIST_NAME, setlist.getName());
    	startActivityForResult(intent, Constants.EDIT_SETLIST);
	}
	
	
	/**
	 * 
	 *
	 */
	private Model getSelectedSetlist() {
		for (Model setlist : itemList) {
			if (setlist.isSelected()) {
				return setlist;
			}
		}
		
		return null;
	}
	
	
	/**
	 * 
	 */
	private void showSetlists() {
		displaySetlists();
		setlistMode(true);
		setActionItemVisibility();
	}
	
	
	/**
	 * 
	 */
	private void controlBeats() {
		if (beatTask == null || !beatTask.isRunning()) {
			playImage.setImageResource(R.drawable.pause_button);
			if (beatTask == null) {
				if (selectedSong != null) {
					Log.d("beatbuddy", "Starting beats");
					beatTask = new StartBeatsTask();
					beatTask.start();
				}
			}
			else {
				Log.d("beatbuddy", "Resuming beats");
				beatTask.resume();
			}
		}
		else {
			playImage.setImageResource(R.drawable.play_button);
			Log.d("beatbuddy", "Pausing beats");
			beatTask.pause();
		}
	}
	
	
	/**
	 * 
	 */
	@SuppressWarnings("unused")
    private void stopBeats() {
		playImage.setImageResource(R.drawable.play_button);
		Log.d("beatbuddy", "Pausing beats");
		if (beatTask != null) {
			beatTask.pause();
		}
	}
	
	

	/**
	 * 
	 */
	public void incrementBeat() {
		
		handler.post(new Runnable() {

            @Override
            public void run() {

                int prevBeat = beatViews.size() - 1;
                beatViews.get(currentBeat).setImageResource(R.drawable.beat_on);

                if (currentBeat > 0) {
                    prevBeat = currentBeat - 1;
                }
                beatViews.get(prevBeat).setImageResource(R.drawable.beat_off);

                if (++currentBeat >= beatViews.size()) {
                    currentBeat = 0;
                }
            }

        });
	}
	
	
	
	/**
	 * 
	 *
	 */
	public boolean isEditMode() {
		return editMode;
	}


    /**
     *
     * @param editMode
     */
    public void setEditMode(boolean editMode) {

        this.editMode = editMode;

        helpDialogFragment.setEditMode(editMode);
    }


	/**
	 * 
	 *
	 */
	public void setEditItemActions(int numberOfSelectedItems) {
		if (editMode) {

            helpDialogFragment.setNumberOfSelectedItems(numberOfSelectedItems);
			editAction.setVisible(numberOfSelectedItems == 1);
			deleteAction.setVisible(numberOfSelectedItems > 0);
			if (setlistMode) {
				copyAction.setVisible(numberOfSelectedItems == 1);
			}
			else {
				copyAction.setVisible(numberOfSelectedItems > 0);
			}
		}
	}


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        selectedSong = itemList.get(position);
        SongDetailsFragment songDetails = new SongDetailsFragment();
        songDetails.setSelectedSong(selectedSong);
        songDetails.setDbService(dbService);
        songDetails.setSelectedSetlist(selectedSetlist);
        songDetails.show(MetronomeFragment.this.getActivity().getFragmentManager(), "");
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        listAdapter.setSelectedPosition(position);

        if (!setlistMode) {
            selectedSong = itemList.get(position);
//					stopBeats();
//					createBeatIcons(selectedSong);
//					setSongInformationVisibility(View.VISIBLE);
            setActionItemVisibility();

//					if (selectedView != null) {
//						selectedView.setBackgroundColor(Color.TRANSPARENT);
//					}
//					selectedView = view;
//					view.setSelected(true);
            listAdapter.notifyDataSetChanged();
//					view.setBackgroundColor(Color.RED);

//					SongDetailsFragment songDetails = new SongDetailsFragment();
//					songDetails.setSelectedSong(selectedSong);
//					songDetails.show(MetronomeFragment.this.getActivity().getFragmentManager(), "");

            Intent intent = new Intent(getActivity(), EnterSongDetails.class);

            intent.putExtra(Constants.SONG_ENTERED, new MySong(selectedSong.getId(), selectedSong.getName(), selectedSong.getArtist(), selectedSong.getTempo(),
                    selectedSong.getTimeSignature(), selectedSong.getKey(), selectedSong.getSetlistCount(), selectedSong.getPosition(), selectedSong.getDuration()));

            startActivityForResult(intent, Constants.EDIT_SONG);

        } else {
            editMode = false;
            helpDialogFragment.setEditMode(editMode);
            editModeAction.setTitle(R.string.action_edit);
            selectedSetlist = itemList.get(position);
            displaySongsInSetlist();
            setlistMode(false);
            listAdapter.setSelectedPosition(-1);
            setActionItemVisibility();
        }

        return true;
    }


    /**
	 * 
	 * @author andrew
	 *
	 */
	private class StartBeatsTask {
		
		private Thread metronome;
		private Boolean stop = false;
		private int delay;
		private final Integer lock = 0;
		
		public StartBeatsTask() {
			BigDecimal value = BigDecimal.valueOf((60/selectedSong.getTempo()) * 1000);
			delay = value.intValue();
			
			metronome = new Thread(new Runnable() {

				@SuppressWarnings("InfiniteLoopStatement")
                @Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(delay);
							if (!stop) {
								incrementBeat();
							}
							else {
								synchronized(lock) {
									lock.wait();
								}
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
			});
		}
		
		public void start() {
			metronome.start();
		}
		
		public void pause() {
			stop = true;
		}
		
		public void resume() {
			BigDecimal value = BigDecimal.valueOf((60/selectedSong.getTempo()) * 1000);
			stop = false;
			delay = value.intValue();
			synchronized(lock) {
				lock.notify();
			}
		}
		
		public boolean isRunning() {
			return !stop;
		}
	}


	@Override
	public void onClick(View v) {

//		if (v == playImage) {
//			controlBeats();
//		}
	}


	@Override
	public boolean onLongClick(View v) {
		if (!setlistMode) {
			SongDetailsFragment songDetails = new SongDetailsFragment();
			songDetails.setSelectedSong(selectedSong);
			songDetails.show(MetronomeFragment.this.getActivity().getFragmentManager(), "");
		}
		return true;
	}
	
	
	/**
	 * 
	 * @author andrew
	 *
	 */
	private class MyDragSortController extends DragSortController {

		public MyDragSortController(DragSortListView dslv) {
			super(dslv);
			
		}
		
		@Override
		public void onLongPress(MotionEvent e) {

//			int listItemPosition = viewIdHitPosition(e, 0);
//
//			if (listItemPosition > -1) {
//				selectedSong = itemList.get(listItemPosition);
//				SongDetailsFragment songDetails = new SongDetailsFragment();
//				songDetails.setSelectedSong(selectedSong);
//				songDetails.show(MetronomeFragment.this.getActivity().getFragmentManager(), "");
//			}
		}

	}
}
