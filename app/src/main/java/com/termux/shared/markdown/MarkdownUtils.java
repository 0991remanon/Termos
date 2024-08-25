package com.termux.shared.markdown;

import com.google.common.base.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownUtils {

    public static final String backtick = "`";
    public static final Pattern backticksPattern = Pattern.compile("(" + backtick + "+)");

    /**
     * Get the markdown code {@link String} for a {@link String}. This ensures all backticks "`" are
     * properly escaped so that markdown does not break.
     *
     * @param string The {@link String} to convert.
     * @param codeBlock If the {@link String} is to be converted to a code block or inline code.
     * @return Returns the markdown code {@link String}.
     */
    public static String getMarkdownCodeForString(String string, boolean codeBlock) {
        if (string == null) return null;
        if (string.isEmpty()) return "";

        int maxConsecutiveBackTicksCount = getMaxConsecutiveBackTicksCount(string);

        // markdown requires surrounding backticks count to be at least one more than the count
        // of consecutive ticks in the string itself
        int backticksCountToUse;
        if (codeBlock)
            backticksCountToUse = maxConsecutiveBackTicksCount + 3;
        else
            backticksCountToUse = maxConsecutiveBackTicksCount + 1;

        // create a string with n backticks where n==backticksCountToUse
        String backticksToUse = Strings.repeat(backtick, backticksCountToUse);

        if (codeBlock)
            return backticksToUse + "\n" + string + "\n" + backticksToUse;
        else {
            // add a space to any prefixed or suffixed backtick characters
            if (string.startsWith(backtick))
                string = " " + string;
            if (string.endsWith(backtick))
                string = string + " ";

            return backticksToUse + string + backticksToUse;
        }
    }

    /**
     * Get the max consecutive backticks "`" in a {@link String}.
     *
     * @param string The {@link String} to check.
     * @return Returns the max consecutive backticks count.
     */
    public static int getMaxConsecutiveBackTicksCount(String string) {
        if (string == null || string.isEmpty()) return 0;

        int maxCount = 0;
        int matchCount;
        String match;

        Matcher matcher = backticksPattern.matcher(string);
        while(matcher.find()) {
            match = matcher.group(1);
            matchCount = match != null ? match.length() : 0;
            if (matchCount > maxCount)
                maxCount = matchCount;
        }

        return maxCount;
    }

    public static String getSingleLineMarkdownStringEntry(String label, Object object, String def) {
        if (object != null)
            return "**" + label + "**: " + getMarkdownCodeForString(object.toString(), false) +  "  ";
        else
            return "**" + label + "**: " + def +  "  ";
    }

}
