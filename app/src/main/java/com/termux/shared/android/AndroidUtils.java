package com.termux.shared.android;

import android.annotation.SuppressLint;

import com.termux.shared.markdown.MarkdownUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AndroidUtils {

    public static void appendPropertyToMarkdown(StringBuilder markdownString, String label, Object value) {
        markdownString.append("\n").append(getPropertyMarkdown(label, value));
    }

    public static String getPropertyMarkdown(String label, Object value) {
        return MarkdownUtils.getSingleLineMarkdownStringEntry(label, value, "-");
    }

    public static String getCurrentMilliSecondLocalTimeStamp() {
        @SuppressLint("SimpleDateFormat")
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS");
        df.setTimeZone(TimeZone.getDefault());
        return df.format(new Date());
    }

}
