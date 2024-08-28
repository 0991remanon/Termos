package com.termux.app.fragments.settings.termux;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.preference.Preference;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.termux.R;
import com.termux.shared.interact.ShareUtils;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Keep
public class TerminalOtherPreferencesFragment extends PreferenceFragmentCompat {

    private TerminalOtherPreferencesDataStore terminalOtherPreferencesDataStore;

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        terminalOtherPreferencesDataStore = TerminalOtherPreferencesDataStore.getInstance(context);
        preferenceManager.setPreferenceDataStore(terminalOtherPreferencesDataStore);

        setPreferencesFromResource(R.xml.termux_terminal_other_preferences, rootKey);

        Preference clearAllPreference = findPreference("clear_all_settings");
        if (clearAllPreference != null) {
            clearAllPreference.setOnPreferenceClickListener(pref -> {
                final AlertDialog.Builder b = new AlertDialog.Builder(context);
                b.setIcon(android.R.drawable.ic_dialog_alert);
                b.setTitle(R.string.reset_all_settings_title);
                b.setNeutralButton(android.R.string.yes, (dialog, id) -> {
                    try {
                        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear();
                        editor.apply();
                    } catch (Exception e) {
                    }
                });
                b.setPositiveButton(android.R.string.no, null);
                b.show();
                return true;
            });
        }

        Preference aboutPreference = findPreference("about_app");
        if (aboutPreference != null) {
            aboutPreference.setOnPreferenceClickListener(pref -> {
                List<String> versionName = new ArrayList<>();
                versionName.add("");
                try {
                    versionName.set(0, "v" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
                } catch (Exception e) {}

                final AlertDialog.Builder b = new AlertDialog.Builder(context);
                b.setIcon(R.mipmap.ic_launcher);
                b.setTitle(getString(R.string.application_name) + " " + versionName.get(0));
                b.setMessage(R.string.termux_about_dialog_message);
                b.setNeutralButton("Github", (dialog, id) -> {
                    try {
                        ShareUtils.openUrl(context, "https://github.com/0991remanon/Termos");
                    } catch (Exception e) {
                    }
                });

                b.setNegativeButton(versionName.get(0), null);
                b.setPositiveButton(android.R.string.ok, null);
                final AlertDialog lastDialog = b.show();

                final List<String> lastVersion = new ArrayList<>();
                final StringBuilder response = new StringBuilder();

                new Thread(() -> {
                    try {
                        URL url = new URL("https://api.github.com/repos/0991remanon/Termos/releases/latest");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                        //connection.setRequestMethod("GET");

                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();
                        connection.disconnect();
                    } catch (Exception e) {
                        return;
                    }

                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        String tagName = jsonObject.getString("tag_name");
                        String downloadUrl = jsonObject.getJSONArray("assets")
                                .getJSONObject(0)
                                .getString("browser_download_url");
                        lastVersion.add(tagName);
                        lastVersion.add(downloadUrl);
                    } catch (Exception e) {
                        return;
                    }

                    getActivity().runOnUiThread(() -> {
                        try {
                            if (lastVersion.size() == 2 && Integer.parseInt(lastVersion.get(0).replaceAll("[v,\\.]", "")) > Integer.parseInt(versionName.get(0).replaceAll("[v,\\.]", ""))) {
                                android.widget.Button negativeButton = lastDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                                negativeButton.setText(lastVersion.get(0));
                                negativeButton.setOnClickListener((negabut) -> {
                                    try {
                                        ShareUtils.openUrl(context, lastVersion.get(1));
                                    } catch (Exception e) {
                                    }
                                });

                                lastDialog.onContentChanged();
                            }
                        } catch (Exception e) {}
                    });
                }).start();

                return true;
            });
        }
    }

}

class TerminalOtherPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final TermuxAppSharedPreferences mPreferences;

    private static TerminalOtherPreferencesDataStore mInstance;

    private TerminalOtherPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = TermuxAppSharedPreferences.build(context, true);
    }

    public static synchronized TerminalOtherPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TerminalOtherPreferencesDataStore(context);
        }
        return mInstance;
    }



    @Override
    public void putBoolean(String key, boolean value) {
        if (mPreferences == null) return;
        if (key == null) return;

        switch (key) {
            case "remove_task":
                    mPreferences.setRemoveTask(value);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (mPreferences == null) return false;

        switch (key) {
            case "remove_task":
                return mPreferences.getRemoveTask();
            default:
                return false;
        }
    }

}
