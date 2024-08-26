package com.termux.shared.termux.settings.preferences;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.shared.android.PackageUtils;
import com.termux.shared.settings.preferences.AppSharedPreferences;
import com.termux.shared.settings.preferences.SharedPreferenceUtils;
import com.termux.shared.termux.TermuxConstants;
import com.termux.shared.termux.TermuxUtils;
import com.termux.shared.termux.settings.preferences.TermuxPreferenceConstants.TERMUX_APP;

public class TermuxAppSharedPreferences extends AppSharedPreferences {

    private int MIN_FONTSIZE;
    private int MAX_FONTSIZE;
    private int DEFAULT_FONTSIZE;

    private TermuxAppSharedPreferences(@NonNull Context context) {
        super(context,
            SharedPreferenceUtils.getPrivateSharedPreferences(context,
                TermuxConstants.TERMUX_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION),
            SharedPreferenceUtils.getPrivateAndMultiProcessSharedPreferences(context,
                TermuxConstants.TERMUX_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION));

//        setFontVariables(context);
    }

    /**
     * Get {@link TermuxAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link TermuxConstants#TERMUX_PACKAGE_NAME}.
     * @return Returns the {@link TermuxAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    @Nullable
    public static TermuxAppSharedPreferences build(@NonNull final Context context) {
        Context termuxPackageContext = PackageUtils.getContextForPackage(context, TermuxConstants.TERMUX_PACKAGE_NAME);
        if (termuxPackageContext == null)
            return null;
        else
            return new TermuxAppSharedPreferences(termuxPackageContext);
    }

    /**
     * Get {@link TermuxAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link TermuxConstants#TERMUX_PACKAGE_NAME}.
     * @param exitAppOnError If {@code true} and failed to get package context, then a dialog will
     *                       be shown which when dismissed will exit the app.
     * @return Returns the {@link TermuxAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    public static TermuxAppSharedPreferences build(@NonNull final Context context, final boolean exitAppOnError) {
        Context termuxPackageContext = TermuxUtils.getContextForPackageOrExitApp(context, TermuxConstants.TERMUX_PACKAGE_NAME, exitAppOnError);
        if (termuxPackageContext == null)
            return null;
        else
            return new TermuxAppSharedPreferences(termuxPackageContext);
    }



    public boolean shouldShowTerminalToolbar() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, TERMUX_APP.KEY_SHOW_TERMINAL_TOOLBAR, TERMUX_APP.DEFAULT_VALUE_SHOW_TERMINAL_TOOLBAR);
    }

    public void setShowTerminalToolbar(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, TERMUX_APP.KEY_SHOW_TERMINAL_TOOLBAR, value, false);
    }

    public boolean toogleShowTerminalToolbar() {
        boolean currentValue = shouldShowTerminalToolbar();
        setShowTerminalToolbar(!currentValue);
        return !currentValue;
    }



    public boolean isTerminalMarginAdjustmentEnabled() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, TERMUX_APP.KEY_TERMINAL_MARGIN_ADJUSTMENT, TERMUX_APP.DEFAULT_TERMINAL_MARGIN_ADJUSTMENT);
    }

    public void setTerminalMarginAdjustment(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, TERMUX_APP.KEY_TERMINAL_MARGIN_ADJUSTMENT, value, false);
    }

    public String getTerminalDrawerTransparency() {
        return SharedPreferenceUtils.getString(mSharedPreferences, TERMUX_APP.KEY_TRANSPARENCY_DRAWER, TERMUX_APP.DEFAULT_TRANSPARENCY_DRAWER, false);
    }

    public void setTerminalDrawerTransparency(String value) {
        SharedPreferenceUtils.setString(mSharedPreferences, TERMUX_APP.KEY_TRANSPARENCY_DRAWER, value, false);
    }

    public String getTextSize() {
        return SharedPreferenceUtils.getString(mSharedPreferences, TERMUX_APP.KEY_TEXTSIZE, TERMUX_APP.DEFAULT_TEXTSIZE, false);
    }

    public void setTextSize(String value) {
        SharedPreferenceUtils.setString(mSharedPreferences, TERMUX_APP.KEY_TEXTSIZE, value, false);
    }

    public boolean getPinchInEnabled() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, TERMUX_APP.PINCH_IN_ENABLED, TERMUX_APP.DEFAULT_VALUE_KEY_PINCH_IN_ENABLED);
    }

    public void setPinchInEnabled(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, TERMUX_APP.PINCH_IN_ENABLED, value, false);
    }

    public boolean getPinchOutEnabled() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, TERMUX_APP.PINCH_OUT_ENABLED, TERMUX_APP.DEFAULT_VALUE_KEY_PINCH_OUT_ENABLED);
    }

    public void setPinchOutEnabled(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, TERMUX_APP.PINCH_OUT_ENABLED, value, false);
    }

    public boolean getDoubleTapEnabled() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, TERMUX_APP.DOUBLE_TAP_ENABLED, TERMUX_APP.DEFAULT_VALUE_KEY_DOUBLE_TAP_ENABLED);
    }

    public void setDoubleTapEnabled(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, TERMUX_APP.DOUBLE_TAP_ENABLED, value, false);
    }

    public boolean getQuickExitEnabled() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, TERMUX_APP.QUICK_EXIT_ENABLED, TERMUX_APP.DEFAULT_VALUE_KEY_QUICK_EXIT_ENABLED);
    }

    public void setQuickExitEnabled(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, TERMUX_APP.QUICK_EXIT_ENABLED, value, false);
    }

    public boolean isVibrationEnabled() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, TERMUX_APP.VIBRATION_ENABLED, TERMUX_APP.DEFAULT_VALUE_KEY_VIBRATION_ENABLED);
    }

    public void setVibrationEnabled(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, TERMUX_APP.VIBRATION_ENABLED, value, false);
    }

    public boolean isSoftKeyboardEnabled() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, TERMUX_APP.KEY_SOFT_KEYBOARD_ENABLED, TERMUX_APP.DEFAULT_VALUE_KEY_SOFT_KEYBOARD_ENABLED);
    }

    public void setSoftKeyboardEnabled(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, TERMUX_APP.KEY_SOFT_KEYBOARD_ENABLED, value, false);
    }

    public boolean isSoftKeyboardEnabledOnlyIfNoHardware() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, TERMUX_APP.KEY_SOFT_KEYBOARD_ENABLED_ONLY_IF_NO_HARDWARE, TERMUX_APP.DEFAULT_VALUE_KEY_SOFT_KEYBOARD_ENABLED_ONLY_IF_NO_HARDWARE);
    }

    public boolean isCustomShellEnabled() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, TERMUX_APP.CUSTOM_SHELL_ENABLED, TERMUX_APP.DEFAULT_VALUE_KEY_CUSTOM_SHELL_ENABLED);
    }

    public void setSoftKeyboardEnabledOnlyIfNoHardware(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, TERMUX_APP.KEY_SOFT_KEYBOARD_ENABLED_ONLY_IF_NO_HARDWARE, value, false);
    }

    public void setCustomShellEnabled(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, TERMUX_APP.CUSTOM_SHELL_ENABLED, value, false);
    }

    public String getCustomShellString() {
        return SharedPreferenceUtils.getString(mSharedPreferences, TERMUX_APP.CUSTOM_SHELL_STRING, TERMUX_APP.DEFAULT_VALUE_KEY_CUSTOM_SHELL_STRING, false);
    }

    public void setCustomShellString(String value) {
        SharedPreferenceUtils.setString(mSharedPreferences, TERMUX_APP.CUSTOM_SHELL_STRING, value, false);
    }

    public String getCustomTextColor() {
        return SharedPreferenceUtils.getString(mSharedPreferences, TERMUX_APP.CUSTOM_TEXT_COLOR, TERMUX_APP.DEFAULT_VALUE_KEY_CUSTOM_TEXT_COLOR, false);
    }

    public void setCustomTextColor(String value) {
        SharedPreferenceUtils.setString(mSharedPreferences, TERMUX_APP.CUSTOM_TEXT_COLOR, value, false);
    }

    public String getCustomHomeString() {
        return SharedPreferenceUtils.getString(mSharedPreferences, TERMUX_APP.CUSTOM_HOME_STRING, TERMUX_APP.DEFAULT_VALUE_KEY_CUSTOM_HOME_STRING, false);
    }

    public void setCustomHomeString(String value) {
        SharedPreferenceUtils.setString(mSharedPreferences, TERMUX_APP.CUSTOM_HOME_STRING, value, false);
    }

    public void setRootAsDefault(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, TERMUX_APP.ROOT_AS_DEFAULT, value, false);
    }

    public boolean getRootAsDefault() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, TERMUX_APP.ROOT_AS_DEFAULT, TERMUX_APP.DEFAULT_VALUE_KEY_ROOT_AS_DEFAULT);
    }

    public void setUseCustomArguments(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, TERMUX_APP.USE_CUSTOM_ARGUMENTS, value, false);
    }

    public boolean getUseCustomArguments() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, TERMUX_APP.USE_CUSTOM_ARGUMENTS, TERMUX_APP.DEFAULT_VALUE_KEY_USE_CUSTOM_ARGUMENTS);
    }

    public void setUseCustomTextColor(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, TERMUX_APP.USE_CUSTOM_TEXT_COLOR, value, false);
    }

    public boolean getUseCustomTextColor() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, TERMUX_APP.USE_CUSTOM_TEXT_COLOR, TERMUX_APP.DEFAULT_VALUE_KEY_USE_CUSTOM_TEXT_COLOR);
    }

    public void setUseCustomBackgroundColor(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, TERMUX_APP.USE_CUSTOM_BACKGROUND_COLOR, value, false);
    }

    public boolean getUseCustomBackgroundColor() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, TERMUX_APP.USE_CUSTOM_BACKGROUND_COLOR, TERMUX_APP.DEFAULT_VALUE_KEY_USE_CUSTOM_BACKGROUND_COLOR);
    }

    public void setUseCustomHome(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, TERMUX_APP.USE_CUSTOM_HOME, value, false);
    }

    public boolean getUseCustomHome() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, TERMUX_APP.USE_CUSTOM_HOME, TERMUX_APP.DEFAULT_VALUE_KEY_USE_CUSTOM_HOME);
    }

    public void setUseCustomHomeWhenRoot(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, TERMUX_APP.USE_CUSTOM_HOME_ROOT, value, false);
    }

    public boolean getUseCustomHomeWhenRoot() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, TERMUX_APP.USE_CUSTOM_HOME_ROOT, TERMUX_APP.DEFAULT_VALUE_KEY_USE_CUSTOM_HOME_ROOT);
    }

    public String getCustomBackgroundColor() {
        return SharedPreferenceUtils.getString(mSharedPreferences, TERMUX_APP.CUSTOM_BACKGROUND_COLOR, TERMUX_APP.DEFAULT_VALUE_KEY_CUSTOM_BACKGROUND_COLOR, false);
    }

    public void setCustomBackgroundColor(String value) {
        SharedPreferenceUtils.setString(mSharedPreferences, TERMUX_APP.CUSTOM_BACKGROUND_COLOR, value, false);
    }

    public String getCustomArgumentsString() {
        return SharedPreferenceUtils.getString(mSharedPreferences, TERMUX_APP.CUSTOM_ARGUMENTS_STRING, TERMUX_APP.DEFAULT_VALUE_KEY_CUSTOM_ARGUMENTS_STRING, false);
    }

    public void setCustomArgumentsString(String value) {
        SharedPreferenceUtils.setString(mSharedPreferences, TERMUX_APP.CUSTOM_ARGUMENTS_STRING, value, false);
    }

    public boolean shouldKeepScreenOn() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, TERMUX_APP.KEY_KEEP_SCREEN_ON, TERMUX_APP.DEFAULT_VALUE_KEEP_SCREEN_ON);
    }

    public void setKeepScreenOn(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, TERMUX_APP.KEY_KEEP_SCREEN_ON, value, false);
    }





//    public static int[] getDefaultFontSizes(Context context) {
//        float dipInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());
//
//        int[] sizes = new int[3];
//
//        // This is a bit arbitrary and sub-optimal. We want to give a sensible default for minimum font size
//        // to prevent invisible text due to zoom be mistake:
//        sizes[1] = (int) (4f * dipInPixels); // min
//
//        // http://www.google.com/design/spec/style/typography.html#typography-line-height
//        int defaultFontSize = Math.round(12 * dipInPixels);
//        // Make it divisible by 2 since that is the minimal adjustment step:
//        if (defaultFontSize % 2 == 1) defaultFontSize--;
//
//        sizes[0] = defaultFontSize; // default
//
//        sizes[2] = 256; // max
//
//        return sizes;
//    }

//    public void setFontVariables(Context context) {
//        int[] sizes = getDefaultFontSizes(context);
//
//        DEFAULT_FONTSIZE = sizes[0];
//        MIN_FONTSIZE = sizes[1];
//        MAX_FONTSIZE = sizes[2];
//    }

//    public int getFontSize() {
//        int fontSize = SharedPreferenceUtils.getIntStoredAsString(mSharedPreferences, TERMUX_APP.KEY_FONTSIZE, DEFAULT_FONTSIZE);
//        return DataUtils.clamp(fontSize, MIN_FONTSIZE, MAX_FONTSIZE);
//    }

//    public void setFontSize(int value) {
//        SharedPreferenceUtils.setIntStoredAsString(mSharedPreferences, TERMUX_APP.KEY_FONTSIZE, value, false);
//    }

//    public void changeFontSize(boolean increase) {
//        int fontSize = getFontSize();
//
//        fontSize += (increase ? 1 : -1) * 2;
//        fontSize = Math.max(MIN_FONTSIZE, Math.min(fontSize, MAX_FONTSIZE));
//
//        setFontSize(fontSize);
//    }






    public String getCurrentSession() {
        return SharedPreferenceUtils.getString(mSharedPreferences, TERMUX_APP.KEY_CURRENT_SESSION, null, true);
    }

    public void setCurrentSession(String value) {
        SharedPreferenceUtils.setString(mSharedPreferences, TERMUX_APP.KEY_CURRENT_SESSION, value, false);
    }

    public synchronized int getAndIncrementAppShellNumberSinceBoot() {
        // Keep value at MAX_VALUE on integer overflow and not 0, since not first shell
        return SharedPreferenceUtils.getAndIncrementInt(mSharedPreferences, TERMUX_APP.KEY_APP_SHELL_NUMBER_SINCE_BOOT,
            TERMUX_APP.DEFAULT_VALUE_APP_SHELL_NUMBER_SINCE_BOOT, true, Integer.MAX_VALUE);
    }

    public synchronized void resetAppShellNumberSinceBoot() {
        SharedPreferenceUtils.setInt(mSharedPreferences, TERMUX_APP.KEY_APP_SHELL_NUMBER_SINCE_BOOT,
            TERMUX_APP.DEFAULT_VALUE_APP_SHELL_NUMBER_SINCE_BOOT, true);
    }

    public synchronized int getAndIncrementTerminalSessionNumberSinceBoot() {
        // Keep value at MAX_VALUE on integer overflow and not 0, since not first shell
        return SharedPreferenceUtils.getAndIncrementInt(mSharedPreferences, TERMUX_APP.KEY_TERMINAL_SESSION_NUMBER_SINCE_BOOT,
            TERMUX_APP.DEFAULT_VALUE_TERMINAL_SESSION_NUMBER_SINCE_BOOT, true, Integer.MAX_VALUE);
    }

    public synchronized void resetTerminalSessionNumberSinceBoot() {
        SharedPreferenceUtils.setInt(mSharedPreferences, TERMUX_APP.KEY_TERMINAL_SESSION_NUMBER_SINCE_BOOT,
            TERMUX_APP.DEFAULT_VALUE_TERMINAL_SESSION_NUMBER_SINCE_BOOT, true);
    }

}
