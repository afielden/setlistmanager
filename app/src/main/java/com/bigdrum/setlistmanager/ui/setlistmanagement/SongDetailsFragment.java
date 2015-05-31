package com.bigdrum.setlistmanager.ui.setlistmanagement;

import com.bigdrum.setlistmanager.R;
import com.bigdrum.setlistmanager.database.DataService;
import com.bigdrum.setlistmanager.database.DataServiceException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SongDetailsFragment extends DialogFragment {
	
	Model selectedSong;
    Model selectedSetlist;
    DataService dbService;
	
	/**
	 * 
	 */
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String message = "";

        if (selectedSong.isSetlist()) {
            try {
                List<Model> songs = dbService.getSongsInSetlistExcludingSubsets(selectedSong.getId());

                message = getString(R.string.setlist_summary, String.valueOf(songs.size())) + "\n" +
                        getString(R.string.total_set_time, totalTime(songs)) + "\n\n" +
                        getString(R.string.long_click_tip);

            } catch (DataServiceException e) {
                Toast.makeText(getActivity(), e.getReason(), Toast.LENGTH_LONG).show();
            }
        }
        else if (selectedSong.isSubsetItem()) {
            try {
                message = createSubsetInfo();
            } catch (DataServiceException e) {
                Toast.makeText(getActivity(), e.getReason(), Toast.LENGTH_LONG).show();
            }
        }
        else {
        	message = "Song: " + selectedSong.getName() + "\n" +
 				   "Artist: " + selectedSong.getArtist() + "\n" +
		           "Tempo: " + selectedSong.getTempo() + "\n" +
                    "Duration: " + formatTime(selectedSong.getDuration()) + "\n" +
 				   "Key: " + getResources().getStringArray(R.array.keys_array)[selectedSong.getKey()] + "\n\n" +
                    getString(R.string.long_click_tip);
        }
        
        builder.setMessage(message)
               .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // FIRE ZE MISSILES!
                   }
               });

        return builder.create();
    }


    /**
     * Creates a String containing information about the subset
     * @return String
     */
    private String createSubsetInfo() throws DataServiceException {

        int subsetPosition = selectedSong.getPosition();
        String info = null;
        List<Model> songs = dbService.getSongsInSetlist(selectedSetlist.getId());
        List<Model> songsInSubset = new ArrayList<Model>();

        for (Model song : songs) {
            if (song.getPosition() < subsetPosition) {
                continue;
            }
            if (song.getPosition() > subsetPosition && !song.isSubsetItem()) {
                songsInSubset.add(song);
            }
            if (song.getPosition() > subsetPosition && song.isSubsetItem()) {
                break;
            }
        }

        info = getString(R.string.subset_summary, String.valueOf(songsInSubset.size())) + "\n" +
                getString(R.string.total_set_time, totalTime(songsInSubset)) + "\n\n" +
                getString(R.string.long_click_tip);

        return info;
    }


    /**
     *
     * @param seconds: selectedSong time in seconds
     * @return time: time in minutes and seconds
     */
    public static String formatTime(double seconds) {

        Integer totalSec = Double.valueOf(seconds).intValue();

        int hours = totalSec/3600;
        int mins;
        int sec;

        if (hours > 0) {
            totalSec = totalSec - (hours*3600);
        }

        mins = totalSec/60;
        sec = totalSec % 60;

        return String.format("%02d:%02d:%02d", hours, mins, sec);
    }


    private String totalTime(List<Model> songs) {

        double totalTime = 0;

        for (Model song : songs) {
            totalTime += song.getDuration();
        }

        return formatTime(totalTime);
    }


	/**
	 * 
	 *
	 */
	public void setSelectedSong(Model selectedSong) {
		this.selectedSong = selectedSong;
	}

    public void setDbService(DataService dbService) {
        this.dbService = dbService;
    }

    public void setSelectedSetlist(Model setlist) {
        this.selectedSetlist = setlist;
    }

}
