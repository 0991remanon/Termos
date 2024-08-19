package com.termux.app.fragments.settings.termux;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.termux.R;
import com.termux.app.Utils;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;

import java.io.File;

@Keep
public class TerminalEnvPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(TerminalEnvPreferencesDataStore.getInstance(context));

        setPreferencesFromResource(R.xml.termux_terminal_env_preferences, rootKey);
    }

}

class TerminalEnvPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final TermuxAppSharedPreferences mPreferences;

    private static TerminalEnvPreferencesDataStore mInstance;

    private TerminalEnvPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = TermuxAppSharedPreferences.build(context, true);
    }

    public static synchronized TerminalEnvPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TerminalEnvPreferencesDataStore(context);
        }
        return mInstance;
    }



    @Override
    public void putBoolean(String key, boolean value) {
        if (mPreferences == null) return;
        if (key == null) return;

        switch (key) {
            case "use_custom_shell":
                mPreferences.setCustomShellEnabled(value);
                break;
            case "root_as_default":
                mPreferences.setRootAsDefault(value);
                break;
            case "use_custom_arguments":
                mPreferences.setUseCustomArguments(value);
                break;
            case "use_custom_home":
                mPreferences.setUseCustomHome(value);
                break;
            case "custom_home_root":
                mPreferences.setUseCustomHomeWhenRoot(value);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (mPreferences == null) return false;

        switch (key) {
            case "use_custom_shell":
                return mPreferences.isCustomShellEnabled();
            case "root_as_default":
                return mPreferences.getRootAsDefault();
            case "use_custom_arguments":
                return mPreferences.getUseCustomArguments();
            case "use_custom_home":
                return mPreferences.getUseCustomHome();
            case "custom_home_root":
                return mPreferences.getUseCustomHomeWhenRoot();
            default:
                return false;
        }
    }

    @Override
    public void putString(String key, @Nullable String value) {
        if (mPreferences == null) return;
        if (key == null) return;

        switch (key) {
            case "custom_shell_string":
                if (value == null || value.trim().isEmpty()) {
                    mPreferences.setCustomShellString("");
                    break;
                }
                if (!new File(value).canExecute()) {
                    Utils.darkToast(mContext, R.string.error_wrong_custom_shell_path, 1);
                    break;
                }
                mPreferences.setCustomShellString(value);
                break;
            case "custom_home_string":
                if (value == null || value.trim().isEmpty()) {
                    mPreferences.setCustomHomeString("");
                    break;
                }
                File homeDir = new File(value);
                if (!homeDir.exists() || !homeDir.isDirectory()) {
                    Utils.darkToast(mContext, R.string.error_wrong_custom_home_path, 1);
                    break;
                }
                mPreferences.setCustomHomeString(value);
                break;
            case "custom_arguments":
                if (value == null || value.trim().isEmpty()) {
                    mPreferences.setCustomArgumentsString("");
                    break;
                }
                mPreferences.setCustomArgumentsString(value);
                break;
            default:
                break;
        }
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        if (mPreferences == null) return "";

        switch (key) {
            case "custom_shell_string":
                return mPreferences.getCustomShellString();
            case "custom_home_string":
                return mPreferences.getCustomHomeString();
            case "custom_arguments":
                return mPreferences.getCustomArgumentsString();
            default:
                break;
        }

        return "";
    }
}
