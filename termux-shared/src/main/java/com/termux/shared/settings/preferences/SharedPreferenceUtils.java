package com.termux.shared.settings.preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;


import java.util.Set;

public class SharedPreferenceUtils {

    /**
     * Get {@link SharedPreferences} instance of the preferences file 'name' with the operating mode
     * {@link Context#MODE_PRIVATE}. This file will be created in the app package's default
     * shared preferences directory.
     *
     * @param context The {@link Context} to get the {@link SharedPreferences} instance.
     * @param name The preferences file basename without extension.
     * @return The single {@link SharedPreferences} instance that can be used to retrieve and
     * modify the preference values.
     */
    public static SharedPreferences getPrivateSharedPreferences(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * Get {@link SharedPreferences} instance of the preferences file 'name' with the operating mode
     * {@link Context#MODE_PRIVATE} and {@link Context#MODE_MULTI_PROCESS}. This file will be
     * created in the app package's default shared preferences directory.
     *
     * @param context The {@link Context} to get the {@link SharedPreferences} instance.
     * @param name The preferences file basename without extension.
     * @return The single {@link SharedPreferences} instance that can be used to retrieve and
     * modify the preference values.
     */
    public static SharedPreferences getPrivateAndMultiProcessSharedPreferences(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
    }



    /**
     * Get a {@code boolean} from {@link SharedPreferences}.
     *
     * @param sharedPreferences The {@link SharedPreferences} to get the value from.
     * @param key The key for the value.
     * @param def The default value if failed to read a valid value.
     * @return Returns the {@code boolean} value stored in {@link SharedPreferences}, otherwise returns
     * default if failed to read a valid value, like in case of an exception.
     */
    public static boolean getBoolean(SharedPreferences sharedPreferences, String key, boolean def) {
        if (sharedPreferences == null) {
            return def;
        }

        try {
            return sharedPreferences.getBoolean(key, def);
        }
        catch (ClassCastException e) {
            return def;
        }
    }

    /**
     * Set a {@code boolean} in {@link SharedPreferences}.
     *
     * @param sharedPreferences The {@link SharedPreferences} to set the value in.
     * @param key The key for the value.
     * @param value The value to store.
     * @param commitToFile If set to {@code true}, then value will be set to shared preferences
     *                     in-memory cache and the file synchronously. Ideally, only to be used for
     *                     multi-process use-cases.
     */
    @SuppressLint("ApplySharedPref")
    public static void setBoolean(SharedPreferences sharedPreferences, String key, boolean value, boolean commitToFile) {
        if (sharedPreferences == null) {
            return;
        }

        if (commitToFile)
            sharedPreferences.edit().putBoolean(key, value).commit();
        else
            sharedPreferences.edit().putBoolean(key, value).apply();

    }

    /**
     * Get an {@code int} from {@link SharedPreferences}.
     *
     * @param sharedPreferences The {@link SharedPreferences} to get the value from.
     * @param key The key for the value.
     * @param def The default value if failed to read a valid value.
     * @return Returns the {@code int} value stored in {@link SharedPreferences}, otherwise returns
     * default if failed to read a valid value, like in case of an exception.
     */
    public static int getInt(SharedPreferences sharedPreferences, String key, int def) {
        if (sharedPreferences == null) {
            return def;
        }

        try {
            return sharedPreferences.getInt(key, def);
        }
        catch (ClassCastException e) {
            return def;
        }
    }

    /**
     * Set an {@code int} in {@link SharedPreferences}.
     *
     * @param sharedPreferences The {@link SharedPreferences} to set the value in.
     * @param key The key for the value.
     * @param value The value to store.
     * @param commitToFile If set to {@code true}, then value will be set to shared preferences
     *                     in-memory cache and the file synchronously. Ideally, only to be used for
     *                     multi-process use-cases.
     */
    @SuppressLint("ApplySharedPref")
    public static void setInt(SharedPreferences sharedPreferences, String key, int value, boolean commitToFile) {
        if (sharedPreferences == null) {
            return;
        }

        if (commitToFile)
            sharedPreferences.edit().putInt(key, value).commit();
        else
            sharedPreferences.edit().putInt(key, value).apply();
    }

    /**
     * Set an {@code int} in {@link SharedPreferences}.
     *
     * @param sharedPreferences The {@link SharedPreferences} to set the value in.
     * @param key The key for the value.
     * @param def The default value if failed to read a valid value.
     * @param commitToFile If set to {@code true}, then value will be set to shared preferences
     *                     in-memory cache and the file synchronously. Ideally, only to be used for
     *                     multi-process use-cases.
     * @param resetValue The value if not {@code null} that should be set if current or incremented
     *                   value is less than 0.
     * @return Returns the {@code int} value stored in {@link SharedPreferences} before increment,
     * otherwise returns default if failed to read a valid value, like in case of an exception.
     */
    @SuppressLint("ApplySharedPref")
    public static int getAndIncrementInt(SharedPreferences sharedPreferences, String key, int def,
                                         boolean commitToFile, Integer resetValue) {
        if (sharedPreferences == null) {
            return def;
        }

        int curValue = getInt(sharedPreferences, key, def);
        if (resetValue != null && (curValue < 0)) curValue = resetValue;

        int newValue = curValue + 1;
        if (resetValue != null && newValue < 0) newValue = resetValue;

        setInt(sharedPreferences, key, newValue, commitToFile);
        return curValue;
    }

    /**
     * Get a {@code String} from {@link SharedPreferences}.
     *
     * @param sharedPreferences The {@link SharedPreferences} to get the value from.
     * @param key The key for the value.
     * @param def The default value if failed to read a valid value.
     * @param defIfEmpty If set to {@code true}, then {@code def} will be returned if value is empty.
     * @return Returns the {@code String} value stored in {@link SharedPreferences}, otherwise returns
     * default if failed to read a valid value, like in case of an exception.
     */
    public static String getString(SharedPreferences sharedPreferences, String key, String def, boolean defIfEmpty) {
        if (sharedPreferences == null) {
            return def;
        }

        try {
            String value = sharedPreferences.getString(key, def);
            if (defIfEmpty && (value == null || value.isEmpty()))
                return def;
            else
                return value;
        }
        catch (ClassCastException e) {
            return def;
        }
    }

    /**
     * Set a {@code String} in {@link SharedPreferences}.
     *
     * @param sharedPreferences The {@link SharedPreferences} to set the value in.
     * @param key The key for the value.
     * @param value The value to store.
     * @param commitToFile If set to {@code true}, then value will be set to shared preferences
     *                     in-memory cache and the file synchronously. Ideally, only to be used for
     *                     multi-process use-cases.
     */
    @SuppressLint("ApplySharedPref")
    public static void setString(SharedPreferences sharedPreferences, String key, String value, boolean commitToFile) {
        if (sharedPreferences == null) {
            return;
        }

        if (commitToFile)
            sharedPreferences.edit().putString(key, value).commit();
        else
            sharedPreferences.edit().putString(key, value).apply();
    }

    /**
     * Get an {@code int} from {@link SharedPreferences} that is stored as a {@link String}.
     *
     * @param sharedPreferences The {@link SharedPreferences} to get the value from.
     * @param key The key for the value.
     * @param def The default value if failed to read a valid value.
     * @return Returns the {@code int} value after parsing the {@link String} value stored in
     * {@link SharedPreferences}, otherwise returns default if failed to read a valid value,
     * like in case of an exception.
     */
    public static int getIntStoredAsString(SharedPreferences sharedPreferences, String key, int def) {
        if (sharedPreferences == null) {
            return def;
        }

        String stringValue;
        int intValue;

        try {
            stringValue = sharedPreferences.getString(key, Integer.toString(def));
            if (stringValue != null)
                intValue =  Integer.parseInt(stringValue);
            else
                intValue = def;
        } catch (NumberFormatException | ClassCastException e) {
            intValue = def;
        }

        return intValue;
    }

    /**
     * Set an {@code int} into {@link SharedPreferences} that is stored as a {@link String}.
     *
     * @param sharedPreferences The {@link SharedPreferences} to set the value in.
     * @param key The key for the value.
     * @param value The value to store.
     * @param commitToFile If set to {@code true}, then value will be set to shared preferences
     *                     in-memory cache and the file synchronously. Ideally, only to be used for
     *                     multi-process use-cases.
     */
    @SuppressLint("ApplySharedPref")
    public static void setIntStoredAsString(SharedPreferences sharedPreferences, String key, int value, boolean commitToFile) {
        if (sharedPreferences == null) {
            return;
        }

        if (commitToFile)
            sharedPreferences.edit().putString(key, Integer.toString(value)).commit();
        else
            sharedPreferences.edit().putString(key, Integer.toString(value)).apply();
    }
}
