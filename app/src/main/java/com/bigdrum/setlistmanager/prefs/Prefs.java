package com.bigdrum.setlistmanager.prefs;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.bigdrum.setlistmanager.R;
import com.bigdrum.setlistmanager.database.Constants;

public class Prefs extends PreferenceActivity  implements OnPreferenceChangeListener{

    EditTextPreference emailUser;
    EditTextPreference emailPassword;
    EditTextPreference bandName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        emailUser = (EditTextPreference) findPreference(Constants.prefs_email_user);
        bindPreferenceSummaryToValue(emailUser);
        emailUser.setOnPreferenceChangeListener(this);

        emailPassword = (EditTextPreference) findPreference(Constants.prefs_email_password);
        bindPreferenceSummaryToValue(emailUser);
        emailPassword.setOnPreferenceChangeListener(this);

        bandName = (EditTextPreference) findPreference(Constants.prefs_band_name);
        bindPreferenceSummaryToValue(bandName);
        bandName.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(Constants.prefs_email_user)) {
            if (newValue == null || newValue.equals("")) {
                Toast.makeText(this, R.string.null_string, Toast.LENGTH_LONG).show();
                return false;
            }
            emailUser.setTitle(newValue.toString());
        }
        else if (preference.getKey().equals(Constants.prefs_email_password)) {
//			emailPassword.setTitle(newValue.toString());
        }
        else if (preference.getKey().equals(Constants.prefs_band_name)) {
            if (newValue == null || newValue.equals("")) {
                Toast.makeText(this, R.string.null_string, Toast.LENGTH_LONG).show();
                return false;
            }
            bandName.setTitle(newValue.toString());
        }

        return true;
    }


    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

//	@Override
//	public boolean onPreferenceChange(Preference preference, Object newValue) {
//		if (preference.getKey().equals("pref_email_user")) {
//			emailUser.setTitle(newValue.toString());
//			SharedPreferences preferences = getPreferences(MODE_PRIVATE);
//			preferences.edit().putString("pref_email_user", (String)newValue).apply();
//			
////			preference.setPersistent(true);
//		}
//		if (preference.getKey().equals("pref_email_password")) {
//			emailPassword.setTitle(newValue.toString());
//		}
//		
//		return true;
//	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.prefs, menu);
//		return true;
//	}

}