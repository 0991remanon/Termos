package com.termux.app.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.termux.R;
import com.termux.app.fragments.settings.TermuxPreferencesFragment;
import com.termux.shared.activity.media.AppCompatActivityUtils;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new TermuxPreferencesFragment())
                .commit();
        }

        AppCompatActivityUtils.setToolbar(this, com.termux.R.id.toolbar);
        AppCompatActivityUtils.setShowBackButtonInActionBar(this, true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finishAndRemoveTask();
    }
}
