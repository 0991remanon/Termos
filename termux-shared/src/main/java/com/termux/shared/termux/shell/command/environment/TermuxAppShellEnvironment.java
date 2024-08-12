package com.termux.shared.termux.shell.command.environment;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.shared.android.PackageUtils;
import com.termux.shared.android.SELinuxUtils;
import com.termux.shared.data.DataUtils;
import com.termux.shared.shell.command.environment.ShellEnvironmentUtils;
import com.termux.shared.termux.TermuxConstants;
import com.termux.shared.termux.TermuxUtils;
import com.termux.shared.termux.shell.am.TermuxAmSocketServer;

import java.util.HashMap;

/**
 * Environment for {@link TermuxConstants#TERMUX_PACKAGE_NAME} app.
 */
public class TermuxAppShellEnvironment {

    /** Termux app environment variables. */
    public static HashMap<String, String> termuxAppEnvironment;

    /** Environment variable root scope. */
    public static final String TERMUX_ENV__S_ROOT = "TERMUX_"; // Default: "TERMUX_"

    /** Environment variable scope for Termux. */
    public static final String TERMUX_ENV__S_TERMUX = TERMUX_ENV__S_ROOT + "_"; // Default: "TERMUX__"

    /** Environment variable for the Termux app version. */
    public static final String ENV_TERMUX_VERSION = TermuxConstants.TERMUX_ENV_PREFIX_ROOT + "_VERSION";

    /** Environment variable prefix for the Termux app. */
    public static final String TERMUX_APP_ENV_PREFIX = TermuxConstants.TERMUX_ENV_PREFIX_ROOT + "_APP__";

    /** Environment variable for the Termux app version name. */
    public static final String ENV_TERMUX_APP__APP_VERSION_NAME = TERMUX_APP_ENV_PREFIX + "APP_VERSION_NAME";
    /** Environment variable for the Termux app version code. */
    public static final String ENV_TERMUX_APP__APP_VERSION_CODE = TERMUX_APP_ENV_PREFIX + "APP_VERSION_CODE";
    /** Environment variable for the Termux app package name. */
    public static final String ENV_TERMUX_APP__PACKAGE_NAME = TERMUX_APP_ENV_PREFIX + "PACKAGE_NAME";
    /** Environment variable for the Termux app process id. */
    public static final String ENV_TERMUX_APP__PID = TERMUX_APP_ENV_PREFIX + "PID";
    /** Environment variable for the Termux app uid. */
    public static final String ENV_TERMUX__UID = TERMUX_ENV__S_TERMUX + "UID";
    /** Environment variable for the Termux app targetSdkVersion. */
    public static final String ENV_TERMUX_APP__TARGET_SDK = TERMUX_APP_ENV_PREFIX + "TARGET_SDK";
    /** Environment variable for the Termux app is debuggable apk build. */
    public static final String ENV_TERMUX_APP__IS_DEBUGGABLE_BUILD = TERMUX_APP_ENV_PREFIX + "IS_DEBUGGABLE_BUILD";
    /** Environment variable for the Termux app {@link TermuxConstants} APK_RELEASE_*. */
    public static final String ENV_TERMUX_APP__APK_RELEASE = TERMUX_APP_ENV_PREFIX + "APK_RELEASE";
    /** Environment variable for the Termux app install path. */
    public static final String ENV_TERMUX_APP__APK_FILE = TERMUX_APP_ENV_PREFIX + "APK_FILE";
    /** Environment variable for the Termux app is installed on external/portable storage. */
    public static final String ENV_TERMUX_APP__IS_INSTALLED_ON_EXTERNAL_STORAGE = TERMUX_APP_ENV_PREFIX + "IS_INSTALLED_ON_EXTERNAL_STORAGE";

    /** Environment variable for the Termux app process selinux context. */
    public static final String ENV_TERMUX__SE_PROCESS_CONTEXT = TERMUX_ENV__S_TERMUX + "SE_PROCESS_CONTEXT";
    /** Environment variable for the Termux app data files selinux context. */
    public static final String ENV_TERMUX__SE_FILE_CONTEXT = TERMUX_ENV__S_TERMUX + "SE_FILE_CONTEXT";
    /** Environment variable for the Termux app seInfo tag found in selinux policy used to set app process and app data files selinux context. */
    public static final String ENV_TERMUX__SE_INFO = TERMUX_ENV__S_TERMUX + "SE_INFO";
    /** Environment variable for the Termux app user id. */
    public static final String ENV_TERMUX__USER_ID = TERMUX_ENV__S_TERMUX + "USER_ID";
    /** Environment variable for the Termux app profile owner. */
    public static final String ENV_TERMUX__PROFILE_OWNER = TERMUX_ENV__S_TERMUX + "PROFILE_OWNER";

    /** Environment variable for the Termux app files directory. */
    public static final String ENV_TERMUX_APP__DATA_DIR = TERMUX_APP_ENV_PREFIX + "DATA_DIR";


    /** Environment variable for the Termux app {@link TermuxAmSocketServer#getTermuxAppAMSocketServerEnabled(Context)}. */
    public static final String ENV_TERMUX_APP__AM_SOCKET_SERVER_ENABLED = TERMUX_APP_ENV_PREFIX + "AM_SOCKET_SERVER_ENABLED";



    /** Get shell environment for Termux app. */
    @Nullable
    public static HashMap<String, String> getEnvironment(@NonNull Context currentPackageContext) {
        setTermuxAppEnvironment(currentPackageContext);
        return termuxAppEnvironment;
    }

    /** Set Termux app environment variables in {@link #termuxAppEnvironment}. */
    public synchronized static void setTermuxAppEnvironment(@NonNull Context currentPackageContext) {
        boolean isTermuxApp = TermuxConstants.TERMUX_PACKAGE_NAME.equals(currentPackageContext.getPackageName());

        // If current package context is of termux app and its environment is already set, then no need to set again since it won't change
        // Other apps should always set environment again since termux app may be installed/updated/deleted in background
        if (termuxAppEnvironment != null && isTermuxApp)
            return;

        termuxAppEnvironment = null;

        String packageName = TermuxConstants.TERMUX_PACKAGE_NAME;
        PackageInfo packageInfo = PackageUtils.getPackageInfoForPackage(currentPackageContext, packageName);
        if (packageInfo == null) return;
        ApplicationInfo applicationInfo = PackageUtils.getApplicationInfoForPackage(currentPackageContext, packageName);
        if (applicationInfo == null || !applicationInfo.enabled) return;

        HashMap<String, String> environment = new HashMap<>();

        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_VERSION, PackageUtils.getVersionNameForPackage(packageInfo));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__APP_VERSION_NAME, PackageUtils.getVersionNameForPackage(packageInfo));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__APP_VERSION_CODE, String.valueOf(PackageUtils.getVersionCodeForPackage(packageInfo)));

        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__PACKAGE_NAME, packageName);
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__PID, TermuxUtils.getTermuxAppPID(currentPackageContext));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX__UID, String.valueOf(PackageUtils.getUidForPackage(applicationInfo)));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__TARGET_SDK, String.valueOf(PackageUtils.getTargetSDKForPackage(applicationInfo)));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__IS_DEBUGGABLE_BUILD, PackageUtils.isAppForPackageADebuggableBuild(applicationInfo));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__APK_FILE, PackageUtils.getBaseAPKPathForPackage(applicationInfo));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__IS_INSTALLED_ON_EXTERNAL_STORAGE, PackageUtils.isAppInstalledOnExternalStorage(applicationInfo));

        putTermuxAPKSignature(currentPackageContext, environment);

        Context termuxPackageContext = TermuxUtils.getTermuxPackageContext(currentPackageContext);
        if (termuxPackageContext != null) {


            /*
            // Will not be set for plugins
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__AM_SOCKET_SERVER_ENABLED,
                TermuxAmSocketServer.getTermuxAppAMSocketServerEnabled(currentPackageContext));
             */

            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__DATA_DIR, applicationInfo.dataDir);

            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX__SE_PROCESS_CONTEXT, SELinuxUtils.getContext());
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX__SE_FILE_CONTEXT, SELinuxUtils.getFileContext(applicationInfo.dataDir));

            String seInfoUser = PackageUtils.getApplicationInfoSeInfoUserForPackage(applicationInfo);
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX__SE_INFO, PackageUtils.getApplicationInfoSeInfoForPackage(applicationInfo) +
                (DataUtils.isNullOrEmpty(seInfoUser) ? "" : seInfoUser));

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX__USER_ID, String.valueOf(PackageUtils.getUserIdForPackage(currentPackageContext)));
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX__PROFILE_OWNER, PackageUtils.getProfileOwnerPackageNameForUser(currentPackageContext));
        }

        termuxAppEnvironment = environment;
    }

    /** Put {@link #ENV_TERMUX_APP__APK_RELEASE} in {@code environment}. */
    public static void putTermuxAPKSignature(@NonNull Context currentPackageContext,
                                             @NonNull HashMap<String, String> environment) {
        String signingCertificateSHA256Digest = PackageUtils.getSigningCertificateSHA256DigestForPackage(currentPackageContext,
            TermuxConstants.TERMUX_PACKAGE_NAME);
        if (signingCertificateSHA256Digest != null) {
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__APK_RELEASE,
                TermuxUtils.getAPKRelease(signingCertificateSHA256Digest).replaceAll("[^a-zA-Z]", "_").toUpperCase());
        }
    }

    /** Update {@link #ENV_TERMUX_APP__AM_SOCKET_SERVER_ENABLED} value in {@code environment}. */
    public synchronized static void updateTermuxAppAMSocketServerEnabled(@NonNull Context currentPackageContext) {
        if (termuxAppEnvironment == null) return;
        termuxAppEnvironment.remove(ENV_TERMUX_APP__AM_SOCKET_SERVER_ENABLED);
        ShellEnvironmentUtils.putToEnvIfSet(termuxAppEnvironment, ENV_TERMUX_APP__AM_SOCKET_SERVER_ENABLED,
            TermuxAmSocketServer.getTermuxAppAMSocketServerEnabled(currentPackageContext));
    }

}
