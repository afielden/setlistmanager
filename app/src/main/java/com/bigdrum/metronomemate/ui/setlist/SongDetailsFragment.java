package com.bigdrum.metronomemate.ui.setlist;

import com.bigdrum.metronomemate.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class SongDetailsFragment extends DialogFragment {
	
	Model song;
	
	/**
	 * 
	 */
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String message;
        if (song.isSetlist()) {
        	message = "Click on this Setlist to manage its songs";
        }
        else if (song.isSubsetItem()) {
        	message = getString(R.string.subset_marker_text);
        }
        else {
        	message = "Song: " + song.getName() + "\n" +
 				   "Artist: " + song.getArtist() + "\n" +
		           "Tempo: " + song.getTempo() + "\n" +
 				   "Key: " + getResources().getStringArray(R.array.keys_array)[song.getKey()];
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
	 * @param selectedSong
	 */
	public void setSong(Model song) {
		this.song = song;
	}

}
