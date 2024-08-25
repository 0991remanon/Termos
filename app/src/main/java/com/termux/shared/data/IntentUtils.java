package com.termux.shared.data;

import android.content.Intent;

import androidx.annotation.NonNull;

public class IntentUtils {

    /**
     * Get a {@link String} extra from an {@link Intent} if its not {@code null} or empty.
     *
     * @param intent The {@link Intent} to get the extra from.
     * @param key The {@link String} key name.
     * @param def The default value if extra is not set.
     * @return Returns the {@link String} extra if set, otherwise {@code null}.
     */
    public static String getStringExtraIfSet(@NonNull Intent intent, String key, String def) {
        String value = intent.getStringExtra(key);
        if (value == null || value.isEmpty()) {
            if (def != null && !def.isEmpty())
                return def;
            else
                return null;
        }
        return value;
    }

    /**
     * Get an {@link Integer} from an {@link Intent} stored as a {@link String} extra if its not
     * {@code null} or empty.
     *
     * @param intent The {@link Intent} to get the extra from.
     * @param key The {@link String} key name.
     * @param def The default value if extra is not set.
     * @return Returns the {@link Integer} extra if set, otherwise {@code null}.
     */
    public static Integer getIntegerExtraIfSet(@NonNull Intent intent, String key, Integer def) {
        try {
            String value = intent.getStringExtra(key);
            if (value == null || value.isEmpty()) {
                return def;
            }

            return Integer.parseInt(value);
        }
        catch (Exception e) {
            return def;
        }
    }

    /**
     * Get a {@link String[]} extra from an {@link Intent} if its not {@code null} or empty.
     *
     * @param intent The {@link Intent} to get the extra from.
     * @param key The {@link String} key name.
     * @param def The default value if extra is not set.
     * @return Returns the {@link String[]} extra if set, otherwise {@code null}.
     */
    public static String[] getStringArrayExtraIfSet(Intent intent, String key, String[] def) {
        String[] value = intent.getStringArrayExtra(key);
        if (value == null || value.length == 0) {
            if (def != null && def.length != 0)
                return def;
            else
                return null;
        }
        return value;
    }

}
