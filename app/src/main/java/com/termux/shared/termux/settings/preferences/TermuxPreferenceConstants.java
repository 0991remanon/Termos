package com.termux.shared.termux.settings.preferences;

/*
 * Version: v0.16.0
 *
 * Changelog
 *
 * - 0.1.0 (2021-03-12)
 *      - Initial Release.
 *
 * - 0.2.0 (2021-03-13)
 *      - Added `KEY_LOG_LEVEL` and `KEY_TERMINAL_VIEW_LOGGING_ENABLED`.
 *
 * - 0.3.0 (2021-03-16)
 *      - Changed to per app scoping of variables so that the same file can store all constants of
 *          Termux app and its plugins. This will allow {@link com.termux.app.TermuxSettings} to
 *          manage preferences of plugins as well if they don't have launcher activity themselves
 *          and also allow plugin apps to make changes to preferences from background.
 *      - Added following to `TERMUX_TASKER_APP`:
 *           `KEY_LOG_LEVEL`.
 *
 * - 0.4.0 (2021-03-13)
 *      - Added following to `TERMUX_APP`:
 *          `KEY_PLUGIN_ERROR_NOTIFICATIONS_ENABLED` and `DEFAULT_VALUE_PLUGIN_ERROR_NOTIFICATIONS_ENABLED`.
 *
 * - 0.5.0 (2021-03-24)
 *      - Added following to `TERMUX_APP`:
 *          `KEY_LAST_NOTIFICATION_ID` and `DEFAULT_VALUE_KEY_LAST_NOTIFICATION_ID`.
 *
 * - 0.6.0 (2021-03-24)
 *      - Change `DEFAULT_VALUE_KEEP_SCREEN_ON` value to `false` in `TERMUX_APP`.
 *
 * - 0.7.0 (2021-03-27)
 *      - Added following to `TERMUX_APP`:
 *          `KEY_SOFT_KEYBOARD_ENABLED` and `DEFAULT_VALUE_KEY_SOFT_KEYBOARD_ENABLED`.
 *
 * - 0.8.0 (2021-04-06)
 *      - Added following to `TERMUX_APP`:
 *          `KEY_CRASH_REPORT_NOTIFICATIONS_ENABLED` and `DEFAULT_VALUE_CRASH_REPORT_NOTIFICATIONS_ENABLED`.
 *
 * - 0.9.0 (2021-04-07)
 *      - Updated javadocs.
 *
 * - 0.10.0 (2021-05-12)
 *      - Added following to `TERMUX_APP`:
 *          `KEY_SOFT_KEYBOARD_ENABLED_ONLY_IF_NO_HARDWARE` and `DEFAULT_VALUE_KEY_SOFT_KEYBOARD_ENABLED_ONLY_IF_NO_HARDWARE`.
 *
 * - 0.11.0 (2021-07-08)
 *      - Added following to `TERMUX_APP`:
 *          `KEY_DISABLE_TERMINAL_MARGIN_ADJUSTMENT`.
 *
 * - 0.12.0 (2021-08-27)
 *      - Added `TERMUX_API_APP.KEY_LOG_LEVEL`, `TERMUX_BOOT_APP.KEY_LOG_LEVEL`,
 *          `TERMUX_FLOAT_APP.KEY_LOG_LEVEL`, `TERMUX_STYLING_APP.KEY_LOG_LEVEL`,
 *          `TERMUX_Widget_APP.KEY_LOG_LEVEL`.
 *
 * - 0.13.0 (2021-09-02)
 *      - Added following to `TERMUX_FLOAT_APP`:
 *          `KEY_WINDOW_X`, `KEY_WINDOW_Y`, `KEY_WINDOW_WIDTH`, `KEY_WINDOW_HEIGHT`, `KEY_FONTSIZE`,
 *          `KEY_TERMINAL_VIEW_KEY_LOGGING_ENABLED`.
 *
 * - 0.14.0 (2021-09-04)
 *      - Added `TERMUX_WIDGET_APP.KEY_TOKEN`.
 *
 * - 0.15.0 (2021-09-05)
 *      - Added following to `TERMUX_TASKER_APP`:
 *          `KEY_LAST_PENDING_INTENT_REQUEST_CODE` and `DEFAULT_VALUE_KEY_LAST_PENDING_INTENT_REQUEST_CODE`.
 *
 * - 0.16.0 (2022-06-11)
 *      - Added following to `TERMUX_APP`:
 *          `KEY_APP_SHELL_NUMBER_SINCE_BOOT` and `KEY_TERMINAL_SESSION_NUMBER_SINCE_BOOT`.
 */

import com.termux.shared.shell.command.ExecutionCommand;

/**
 * A class that defines shared constants of the SharedPreferences used by Termux app and its plugins.
 * This class will be hosted by termux-shared lib and should be imported by other termux plugin
 * apps as is instead of copying constants to random classes. The 3rd party apps can also import
 * it for interacting with termux apps. If changes are made to this file, increment the version number
 * and add an entry in the Changelog section above.
 */
public final class TermuxPreferenceConstants {

    /**
     * Termux app constants.
     */
    public static final class TERMUX_APP {

        /**
         * Defines the key for whether terminal view margin adjustment that is done to prevent soft
         * keyboard from covering bottom part of terminal view on some devices is enabled or not.
         * Margin adjustment may cause screen flickering on some devices and so should be disabled.
         */
        public static final String KEY_TERMINAL_MARGIN_ADJUSTMENT = "terminal_margin_adjustment";
        public static final boolean DEFAULT_TERMINAL_MARGIN_ADJUSTMENT = true;

        /**
         * Defines the key for whether to show transparency terminal drawer.
         */
        public static final String KEY_TRANSPARENCY_DRAWER = "terminal_drawer_transparency";
        public static final String DEFAULT_TRANSPARENCY_DRAWER = "-16777216";

        /**
         * Defines the key for whether to show terminal toolbar containing extra keys and text input field.
         */
        public static final String KEY_SHOW_TERMINAL_TOOLBAR = "show_extra_keys";
        public static final boolean DEFAULT_VALUE_SHOW_TERMINAL_TOOLBAR = true;


        /**
         * Defines the key for whether the soft keyboard will be enabled, for cases where users want
         * to use a hardware keyboard instead.
         */
        public static final String KEY_SOFT_KEYBOARD_ENABLED = "soft_keyboard_enabled";
        public static final boolean DEFAULT_VALUE_KEY_SOFT_KEYBOARD_ENABLED = true;


        /**
         * Defines the key for whether the soft keyboard will be enabled only if no hardware keyboard
         * attached, for cases where users want to use a hardware keyboard instead.
         */
        public static final String KEY_SOFT_KEYBOARD_ENABLED_ONLY_IF_NO_HARDWARE = "soft_keyboard_enabled_only_if_no_hardware";
        public static final boolean DEFAULT_VALUE_KEY_SOFT_KEYBOARD_ENABLED_ONLY_IF_NO_HARDWARE = false;

        /**
         * Defines the key for whether the pinch in will be enabled.
         */
        public static final String PINCH_IN_ENABLED = "pinch_in_enabled";
        public static final boolean DEFAULT_VALUE_KEY_PINCH_IN_ENABLED = true;

        /**
         * Defines the key for whether the pinch out will be enabled.
         */
        public static final String PINCH_OUT_ENABLED = "pinch_out_enabled";
        public static final boolean DEFAULT_VALUE_KEY_PINCH_OUT_ENABLED = false;

        /**
         * Defines the key for whether the double tap will be enabled.
         */
        public static final String DOUBLE_TAP_ENABLED = "double_tap_enabled";
        public static final boolean DEFAULT_VALUE_KEY_DOUBLE_TAP_ENABLED = false;

        /**
         * Defines the key for whether the quick exit will be enabled.
         */
        public static final String QUICK_EXIT_ENABLED = "quick_exit_enabled";
        public static final boolean DEFAULT_VALUE_KEY_QUICK_EXIT_ENABLED = false;

        /**
         * Defines the key for whether the vibration will be enabled.
         */
        public static final String VIBRATION_ENABLED = "vibration_enabled";
        public static final boolean DEFAULT_VALUE_KEY_VIBRATION_ENABLED = true;

        /**
         * Defines the key for whether the custom shell will be enabled.
         */
        public static final String CUSTOM_SHELL_ENABLED = "use_custom_shell";
        public static final boolean DEFAULT_VALUE_KEY_CUSTOM_SHELL_ENABLED = false;

        /**
         * Defines the key that specifies the path to the user's shell.
         */
        public static final String CUSTOM_SHELL_STRING = "custom_shell_string";
        public static final String DEFAULT_VALUE_KEY_CUSTOM_SHELL_STRING = "";

        /**
         * Defines the key that specifies the path to the user's $HOME.
         */
        public static final String CUSTOM_HOME_STRING = "custom_home_string";
        public static final String DEFAULT_VALUE_KEY_CUSTOM_HOME_STRING = "";

        /**
         * Defines the key that specifies the path to the user's $HOME.
         */
        public static final String CUSTOM_TEXT_COLOR = "custom_text_color";
        public static final String DEFAULT_VALUE_KEY_CUSTOM_TEXT_COLOR = "-1";

        /**
         * Defines the key that specifies starting root sessions.
         */
        public static final String ROOT_AS_DEFAULT = "root_as_default";
        public static final boolean DEFAULT_VALUE_KEY_ROOT_AS_DEFAULT = false;

        /**
         * Defines the key that specifies adding user shell arguments.
         */
        public static final String USE_CUSTOM_ARGUMENTS = "use_custom_arguments";
        public static final boolean DEFAULT_VALUE_KEY_USE_CUSTOM_ARGUMENTS = false;

        /**
         * Defines the key that specifies using or not user's custom text color.
         */
        public static final String USE_CUSTOM_TEXT_COLOR = "use_custom_text_color";
        public static final boolean DEFAULT_VALUE_KEY_USE_CUSTOM_TEXT_COLOR = false;

        /**
         * Defines the key that specifies using or not user's custom background color.
         */
        public static final String USE_CUSTOM_BACKGROUND_COLOR = "use_custom_background_color";
        public static final boolean DEFAULT_VALUE_KEY_USE_CUSTOM_BACKGROUND_COLOR = false;

        /**
         * Defines the key that specifies user's custom background color.
         */
        public static final String CUSTOM_BACKGROUND_COLOR = "custom_background_color";
        public static final String DEFAULT_VALUE_KEY_CUSTOM_BACKGROUND_COLOR = "-16777216";

        /**
         * Defines the key that specifies using or not user's custom $HOME.
         */
        public static final String USE_CUSTOM_HOME = "use_custom_home";
        public static final boolean DEFAULT_VALUE_KEY_USE_CUSTOM_HOME = false;

        /**
         * Defines the key that specifies using or not user's custom $HOME when root.
         */
        public static final String USE_CUSTOM_HOME_ROOT = "custom_home_root";
        public static final boolean DEFAULT_VALUE_KEY_USE_CUSTOM_HOME_ROOT = false;

        /**
         * Defines the key that specifies user shell arguments.
         */
        public static final String CUSTOM_ARGUMENTS_STRING = "custom_arguments";
        public static final String DEFAULT_VALUE_KEY_CUSTOM_ARGUMENTS_STRING = "";

        /**
         * Defines the key for whether to always keep screen on.
         */
        public static final String KEY_KEEP_SCREEN_ON = "screen_always_on";
        public static final boolean DEFAULT_VALUE_KEEP_SCREEN_ON = false;

        /**
         * Defines the key for font size of termux terminal view.
         */
        public static final String KEY_TEXTSIZE = "text_size";
        public static final String DEFAULT_TEXTSIZE = "14";

        /**
         * Defines the key for current termux terminal session.
         */
        public static final String KEY_CURRENT_SESSION = "current_session";

        /**
         * The {@link ExecutionCommand.Runner#APP_SHELL} number after termux app process since boot.
         */
        public static final String KEY_APP_SHELL_NUMBER_SINCE_BOOT = "app_shell_number_since_boot";
        public static final int DEFAULT_VALUE_APP_SHELL_NUMBER_SINCE_BOOT = 0;

        /**
         * The {@link ExecutionCommand.Runner#TERMINAL_SESSION} number after termux app process since boot.
         */
        public static final String KEY_TERMINAL_SESSION_NUMBER_SINCE_BOOT = "terminal_session_number_since_boot";
        public static final int DEFAULT_VALUE_TERMINAL_SESSION_NUMBER_SINCE_BOOT = 0;

    }
}
