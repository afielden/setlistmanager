package com.bigdrum.metronomemate.ui.setlist;

import com.bigdrum.metronomemate.R;
import com.bigdrum.metronomemate.database.DataService;
import com.bigdrum.metronomemate.database.DataServiceException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

public class SongDetailsFragment extends DialogFragment {
	
	Model song;
    DataService dbService;
	
	/**
	 * 
	 */
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String message = "";

        if (song.isSetlist()) {
            try {
                List<Model> songs = dbService.getSongsInSetlist(song.getId());

                message = getString(R.string.setlist_summary, String.valueOf(songs.size())) + "\n" +
                        getString(R.string.total_set_time, totalTime(songs)) + "\n\n" +
                        getString(R.string.long_click_tip);

            } catch (DataServiceException e) {
                Toast.makeText(getActivity(), e.getReason(), Toast.LENGTH_LONG).show();
            }
        }
        else if (song.isSubsetItem()) {
        	message = getString(R.string.subset_marker_text);
        }
        else {
        	message = "Song: " + song.getName() + "\n" +
 				   "Artist: " + song.getArtist() + "\n" +
		           "Tempo: " + song.getTempo() + "\n" +
                    "Duration: " + formatTime(song.getDuration()) + "\n" +
 				   "Key: " + getResources().getStringArray(R.array.keys_array)[song.getKey()] + "\n\n" +
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
     *
     * @param seconds: song time in seconds
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
	public void setSong(Model song) {
		this.song = song;
	}

    public void setDbService(DataService dbService) {
        this.dbService = dbService;
    }

}
