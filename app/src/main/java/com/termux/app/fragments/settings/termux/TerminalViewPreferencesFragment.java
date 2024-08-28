package com.termux.app.fragments.settings.termux;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.termux.R;
import com.termux.app.ColorPickerDialog;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;
import com.termux.shared.termux.settings.preferences.TermuxPreferenceConstants;


@Keep
public class TerminalViewPreferencesFragment extends PreferenceFragmentCompat implements ColorPickerDialog.OnColorChangedListener {

    private final int TEXT_COLOR = 0;
    private final int BACKGROUND_COLOR = 1;
    private TerminalViewPreferencesDataStore terminalViewPreferencesDataStore;
    private ColorPickerDialog cpd;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        terminalViewPreferencesDataStore = TerminalViewPreferencesDataStore.getInstance(context);
        preferenceManager.setPreferenceDataStore(terminalViewPreferencesDataStore);

        setPreferencesFromResource(R.xml.termux_terminal_view_preferences, rootKey);

        Preference textColorPreference = findPreference("custom_text_color");
        if (textColorPreference != null) {
            textColorPreference.setOnPreferenceClickListener(pref -> {
                try {
                    cpd = new ColorPickerDialog(context, this, Integer.parseInt(terminalViewPreferencesDataStore.getString(TermuxPreferenceConstants.TERMUX_APP.CUSTOM_TEXT_COLOR, TermuxPreferenceConstants.TERMUX_APP.DEFAULT_VALUE_KEY_CUSTOM_TEXT_COLOR)), TEXT_COLOR);
                    cpd.show();
                } catch (Exception e){}
                return true;
            });
        }

        Preference backgroundColorPreference = findPreference("custom_background_color");
        if (backgroundColorPreference != null) {
            backgroundColorPreference.setOnPreferenceClickListener(pref -> {
                try {
                    cpd = new ColorPickerDialog(context, this, Integer.parseInt(terminalViewPreferencesDataStore.getString(TermuxPreferenceConstants.TERMUX_APP.CUSTOM_BACKGROUND_COLOR, TermuxPreferenceConstants.TERMUX_APP.DEFAULT_VALUE_KEY_CUSTOM_BACKGROUND_COLOR)), BACKGROUND_COLOR);
                    cpd.show();
                } catch (Exception e){}
                return true;
            });
        }

    }

    @Override
    public void colorChanged(int color, int requestCode) {
        if (terminalViewPreferencesDataStore == null) return;

        switch (requestCode) {
            case TEXT_COLOR:
                terminalViewPreferencesDataStore.putString(TermuxPreferenceConstants.TERMUX_APP.CUSTOM_TEXT_COLOR, String.valueOf(color));
                break;
            case BACKGROUND_COLOR:
                terminalViewPreferencesDataStore.putString(TermuxPreferenceConstants.TERMUX_APP.CUSTOM_BACKGROUND_COLOR, String.valueOf(color));
                break;
        }
    }
}

class TerminalViewPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final TermuxAppSharedPreferences mPreferences;

    private static TerminalViewPreferencesDataStore mInstance;

    private TerminalViewPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = TermuxAppSharedPreferences.build(context, true);
    }

    public static synchronized TerminalViewPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TerminalViewPreferencesDataStore(context);
        }
        return mInstance;
    }



    @Override
    public void putBoolean(String key, boolean value) {
        if (mPreferences == null) return;
        if (key == null) return;

        switch (key) {
            case "terminal_margin_adjustment":
                    mPreferences.setTerminalMarginAdjustment(value);
                break;
            case "use_custom_text_color":
                mPreferences.setUseCustomTextColor(value);
                break;
            case "use_custom_background_color":
                mPreferences.setUseCustomBackgroundColor(value);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (mPreferences == null) return false;

        switch (key) {
            case "terminal_margin_adjustment":
                return mPreferences.isTerminalMarginAdjustmentEnabled();
            case "use_custom_text_color":
                return mPreferences.getUseCustomTextColor();
            case "use_custom_background_color":
                return mPreferences.getUseCustomBackgroundColor();
            default:
                return false;
        }
    }

    @Override
    public void putString(String key, @Nullable String value) {
        if (mPreferences == null) return;
        if (key == null) return;

        switch (key) {
            case "terminal_drawer_transparency":
                mPreferences.setTerminalDrawerTransparency(value);
                break;
            case "text_size":
                mPreferences.setTextSize(value);
                break;
            case "custom_text_color":
                mPreferences.setCustomTextColor(value);
                break;
            case "custom_background_color":
                mPreferences.setCustomBackgroundColor(value);
                break;
            case "cursor_period":
                mPreferences.setCursorPeriod(value);
                break;
            case "cursor_style":
                mPreferences.setCursorStyle(value);
                break;
            default:
                break;
        }
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        if (mPreferences == null) return null;

        switch (key) {
            case "terminal_drawer_transparency":
                return mPreferences.getTerminalDrawerTransparency();
            case "text_size":
                return mPreferences.getTextSize();
            case "custom_text_color":
                return mPreferences.getCustomTextColor();
            case "custom_background_color":
                return mPreferences.getCustomBackgroundColor();
            case "cursor_period":
                return mPreferences.getCursorPeriod();
            case "cursor_style":
                return mPreferences.getCursorStyle();
            default:
                return null;
        }
    }

}
