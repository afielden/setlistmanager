package com.bigdrum.setlistmanager.prefs;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import com.bigdrum.setlistmanager.R;
import com.bigdrum.setlistmanager.database.Constants;
import com.bigdrum.setlistmanager.database.DataService;

import java.text.ParseException;
import java.util.List;

/**
 * Created by andrew on 02/06/15.
 */
public class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private ListPreference dateFormat;
    private EditTextPreference emailUser;
    private EditTextPreference emailPassword;
    private EditTextPreference bandName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);

        emailUser = (EditTextPreference) findPreference(Constants.prefs_email_user);
        emailUser.setTitle(emailUser.getText());
        emailUser.setOnPreferenceChangeListener(this);

        emailPassword = (EditTextPreference) findPreference(Constants.prefs_email_password);
        emailPassword.setOnPreferenceChangeListener(this);

        bandName = (EditTextPreference) findPreference(Constants.prefs_band_name);
        bandName.setTitle(bandName.getText());
        bandName.setOnPreferenceChangeListener(this);

        dateFormat = (ListPreference)findPreference(Constants.prefs_date_format);
        dateFormat.setTitle(dateFormat.getValue());
        dateFormat.setOnPreferenceChangeListener(this);

    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference.getKey().equals(Constants.prefs_email_user)) {
            if (newValue == null || newValue.equals("")) {
                Toast.makeText(this.getActivity(), R.string.null_string, Toast.LENGTH_LONG).show();
                return false;
            }
            emailUser.setTitle(newValue.toString());
        }

        else if (preference.getKey().equals(Constants.prefs_email_password)) {
//			emailPassword.setTitle(newValue.toString());
        }

        else if (preference.getKey().equals(Constants.prefs_band_name)) {
            if (newValue == null || newValue.equals("")) {
                Toast.makeText(this.getActivity(), R.string.null_string, Toast.LENGTH_LONG).show();
                return false;
            }
            bandName.setTitle(newValue.toString());
        }

        else if (preference.getKey().equals(Constants.prefs_date_format)) {

//            if (!dateFormat.getValue().equals((String)newValue)) {
//
//                try {
//                    DataService.getDataService(getActivity()).changeGigDateFormat((String)newValue);
//                    Toast.makeText(this.getActivity(), R.string.date_update_success, Toast.LENGTH_LONG).show();
//                } catch (ParseException e) {
//                    Toast.makeText(this.getActivity(), R.string.date_update_failed, Toast.LENGTH_LONG).show();
//                    Log.d("SetlistManager", e.toString());
//                }
//            }

            dateFormat.setTitle(newValue.toString());

        }

        return true;
    }
}
