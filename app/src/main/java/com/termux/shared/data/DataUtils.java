package com.termux.shared.data;

import androidx.annotation.Nullable;

public class DataUtils {

    /** Max safe limit of data size to prevent TransactionTooLargeException when transferring data
     * inside or to other apps via transactions. */
    public static final int TRANSACTION_SIZE_LIMIT_IN_BYTES = 100 * 1024; // 100KB

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String getTruncatedCommandOutput(String text, int maxLength, boolean fromEnd, boolean onNewline, boolean addPrefix) {
        if (text == null) return null;

        String prefix = "(truncated) ";

        if (addPrefix)
            maxLength = maxLength - prefix.length();

        if (maxLength < 0 || text.length() < maxLength) return text;

        if (fromEnd) {
            text = text.substring(0, maxLength);
        } else {
            int cutOffIndex = text.length() - maxLength;

            if (onNewline) {
                int nextNewlineIndex = text.indexOf('\n', cutOffIndex);
                if (nextNewlineIndex != -1 && nextNewlineIndex != text.length() - 1) {
                    cutOffIndex = nextNewlineIndex + 1;
                }
            }
            text = text.substring(cutOffIndex);
        }

        if (addPrefix)
            text = prefix + text;

        return text;
    }

    /**
     * Replace a sub string in each item of a {@link String[]}.
     *
     * @param array The {@link String[]} to replace in.
     * @param find The sub string to replace.
     * @param replace The sub string to replace with.
     */
    public static void replaceSubStringsInStringArrayItems(String[] array, String find, String replace) {
        if(array == null || array.length == 0) return;

        for (int i = 0; i < array.length; i++) {
            array[i] = array[i].replace(find, replace);
        }
    }

    /**
     * Get the {@code float} from a {@link String}.
     *
     * @param value The {@link String} value.
     * @param def The default value if failed to read a valid value.
     * @return Returns the {@code float} value after parsing the {@link String} value, otherwise
     * returns default if failed to read a valid value, like in case of an exception.
     */
    public static float getFloatFromString(String value, float def) {
        if (value == null) return def;

        try {
            return Float.parseFloat(value);
        }
        catch (Exception e) {
            return def;
        }
    }

    /**
     * Get the {@code int} from a {@link String}.
     *
     * @param value The {@link String} value.
     * @param def The default value if failed to read a valid value.
     * @return Returns the {@code int} value after parsing the {@link String} value, otherwise
     * returns default if failed to read a valid value, like in case of an exception.
     */
    public static int getIntFromString(String value, int def) {
        if (value == null) return def;

        try {
            return Integer.parseInt(value);
        }
        catch (Exception e) {
            return def;
        }
    }

    /**
     * Get the {@code String} from an {@link Integer}.
     *
     * @param value The {@link Integer} value.
     * @param def The default {@link String} value.
     * @return Returns {@code value} if it is not {@code null}, otherwise returns {@code def}.
     */
    public static String getStringFromInteger(Integer value, String def) {
        return (value == null) ? def : String.valueOf((int) value);
    }

    /**
     * Get the {@code hex string} from a {@link byte[]}.
     *
     * @param bytes The {@link byte[]} value.
     * @return Returns the {@code hex string} value.
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Get the object itself if it is not {@code null}, otherwise default.
     *
     * @param object The {@link Object} to check.
     * @param def The default {@link Object}.
     * @return Returns {@code object} if it is not {@code null}, otherwise returns {@code def}.
     */
    public static <T> T getDefaultIfNull(@Nullable T object, @Nullable T def) {
        return (object == null) ? def : object;
    }

    /** Check if a string is null or empty. */
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

}
