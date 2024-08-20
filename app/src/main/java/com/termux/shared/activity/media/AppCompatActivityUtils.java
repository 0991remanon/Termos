package com.termux.shared.activity.media;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AppCompatActivityUtils {

    /** Set activity toolbar.
     *
     * @param activity The host {@link AppCompatActivity}.
     * @param id The toolbar resource id.
     */
    public static void setToolbar(@NonNull AppCompatActivity activity, @IdRes int id) {
        Toolbar toolbar = activity.findViewById(id);
        if (toolbar != null)
            activity.setSupportActionBar(toolbar);
    }

    /** Set whether back button should be shown in activity toolbar.
     *
     * @param activity The host {@link AppCompatActivity}.
     * @param showBackButtonInActionBar Set to {@code true} to enable and {@code false} to disable.
     */
    public static void setShowBackButtonInActionBar(@NonNull AppCompatActivity activity,
                                                    boolean showBackButtonInActionBar) {
        final ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            if (showBackButtonInActionBar) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            } else {
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setDisplayShowHomeEnabled(false);
            }
        }
    }

}
