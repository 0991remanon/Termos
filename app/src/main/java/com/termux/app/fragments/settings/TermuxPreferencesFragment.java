package com.termux.app.fragments.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.termux.R;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;

@Keep
public class TermuxPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(TermuxPreferencesDataStore.getInstance(context));

        setPreferencesFromResource(R.xml.termux_preferences, rootKey);
    }

}

class TermuxPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final TermuxAppSharedPreferences mPreferences;

    private static TermuxPreferencesDataStore mInstance;

    private TermuxPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = TermuxAppSharedPreferences.build(context, true);
    }

    public static synchronized TermuxPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TermuxPreferencesDataStore(context);
        }
        return mInstance;
    }

}
