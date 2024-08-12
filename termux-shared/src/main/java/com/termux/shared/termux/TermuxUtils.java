package com.termux.shared.termux;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.shared.R;
import com.termux.shared.android.AndroidUtils;
import com.termux.shared.data.DataUtils;
import com.termux.shared.file.FileUtils;
import com.termux.shared.reflection.ReflectionUtils;
import com.termux.shared.shell.command.runner.app.AppShell;
import com.termux.shared.termux.file.TermuxFileUtils;
import com.termux.shared.markdown.MarkdownUtils;
import com.termux.shared.shell.command.ExecutionCommand;
import com.termux.shared.errors.Error;
import com.termux.shared.android.PackageUtils;
import com.termux.shared.termux.TermuxConstants.TERMUX_APP;
import com.termux.shared.termux.shell.command.environment.TermuxShellEnvironment;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;

public class TermuxUtils {
    /**
     * Get the {@link Context} for {@link TermuxConstants#TERMUX_PACKAGE_NAME} package with the
     * {@link Context#CONTEXT_RESTRICTED} flag.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the package.
     * @return Returns the {@link Context}. This will {@code null} if an exception is raised.
     */
    public static Context getTermuxPackageContext(@NonNull Context context) {
        return PackageUtils.getContextForPackage(context, TermuxConstants.TERMUX_PACKAGE_NAME);
    }

    /**
     * Get the {@link Context} for {@link TermuxConstants#TERMUX_PACKAGE_NAME} package with the
     * {@link Context#CONTEXT_INCLUDE_CODE} flag.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the package.
     * @return Returns the {@link Context}. This will {@code null} if an exception is raised.
     */
    public static Context getTermuxPackageContextWithCode(@NonNull Context context) {
        return PackageUtils.getContextForPackage(context, TermuxConstants.TERMUX_PACKAGE_NAME, Context.CONTEXT_INCLUDE_CODE);
    }

    /** Wrapper for {@link PackageUtils#getContextForPackageOrExitApp(Context, String, boolean, String)}. */
    public static Context getContextForPackageOrExitApp(@NonNull Context context, String packageName,
                                                        final boolean exitAppOnError) {
        return PackageUtils.getContextForPackageOrExitApp(context, packageName, exitAppOnError, TermuxConstants.TERMUX_GITHUB_REPO_URL);
    }

    /**
     * Get a field value from the {@link TERMUX_APP#BUILD_CONFIG_CLASS_NAME} class of the Termux app
     * APK installed on the device.
     * This can only be used by apps that share `sharedUserId` with the Termux app.
     *
     * This is a wrapper for {@link #getTermuxAppAPKClassField(Context, String, String)}.
     *
     * @param currentPackageContext The context of current package.
     * @param fieldName The name of the field to get.
     * @return Returns the field value, otherwise {@code null} if an exception was raised or failed
     * to get termux app package context.
     */
    public static Object getTermuxAppAPKBuildConfigClassField(@NonNull Context currentPackageContext,
                                                              @NonNull String fieldName) {
        return getTermuxAppAPKClassField(currentPackageContext, TERMUX_APP.BUILD_CONFIG_CLASS_NAME, fieldName);
    }

    /**
     * Get a field value from a class of the Termux app APK installed on the device.
     * This can only be used by apps that share `sharedUserId` with the Termux app.
     *
     * This is done by getting first getting termux app package context and then getting in class
     * loader (instead of current app's) that contains termux app class info, and then using that to
     * load the required class and then getting required field from it.
     *
     * Note that the value returned is from the APK file and not the current value loaded in Termux
     * app process, so only default values will be returned.
     *
     * Trying to access {@code null} fields will result in {@link NoSuchFieldException}.
     *
     * @param currentPackageContext The context of current package.
     * @param clazzName The name of the class from which to get the field.
     * @param fieldName The name of the field to get.
     * @return Returns the field value, otherwise {@code null} if an exception was raised or failed
     * to get termux app package context.
     */
    public static Object getTermuxAppAPKClassField(@NonNull Context currentPackageContext,
                                                   @NonNull String clazzName, @NonNull String fieldName) {
        try {
            Context termuxPackageContext = TermuxUtils.getTermuxPackageContextWithCode(currentPackageContext);
            if (termuxPackageContext == null)
                return null;

            Class<?> clazz = termuxPackageContext.getClassLoader().loadClass(clazzName);
            return ReflectionUtils.invokeField(clazz, fieldName, null).value;
        } catch (Exception e) {
            return null;
        }
    }


    /** Returns {@code true} if {@link Uri} has `package:` scheme for {@link TermuxConstants#TERMUX_PACKAGE_NAME} sub plugin package. */
    public static boolean isUriDataForTermuxPluginPackage(@NonNull Uri data) {
        return data.toString().startsWith("package:" + TermuxConstants.TERMUX_PACKAGE_NAME + ".");
    }

    /**
     * Send the {@link TermuxConstants#BROADCAST_TERMUX_OPENED} broadcast to notify apps that Termux
     * app has been opened.
     *
     * @param context The Context to send the broadcast.
     */
    public static void sendTermuxOpenedBroadcast(@NonNull Context context) {
        Intent broadcast = new Intent(TermuxConstants.BROADCAST_TERMUX_OPENED);
        List<ResolveInfo> matches = context.getPackageManager().queryBroadcastReceivers(broadcast, 0);

        // send broadcast to registered Termux receivers
        // this technique is needed to work around broadcast changes that Oreo introduced
        for (ResolveInfo info : matches) {
            Intent explicitBroadcast = new Intent(broadcast);
            ComponentName cname = new ComponentName(info.activityInfo.applicationInfo.packageName,
                info.activityInfo.name);
            explicitBroadcast.setComponent(cname);
            context.sendBroadcast(explicitBroadcast);
        }
    }

    public static String getAPKRelease(String signingCertificateSHA256Digest) {
        if (signingCertificateSHA256Digest == null) return "null";

        switch (signingCertificateSHA256Digest.toUpperCase()) {
            case TermuxConstants.APK_RELEASE_FDROID_SIGNING_CERTIFICATE_SHA256_DIGEST:
                return TermuxConstants.APK_RELEASE_FDROID;
            case TermuxConstants.APK_RELEASE_GITHUB_SIGNING_CERTIFICATE_SHA256_DIGEST:
                return TermuxConstants.APK_RELEASE_GITHUB;
            case TermuxConstants.APK_RELEASE_GOOGLE_PLAYSTORE_SIGNING_CERTIFICATE_SHA256_DIGEST:
                return TermuxConstants.APK_RELEASE_GOOGLE_PLAYSTORE;
            case TermuxConstants.APK_RELEASE_TERMUX_DEVS_SIGNING_CERTIFICATE_SHA256_DIGEST:
                return TermuxConstants.APK_RELEASE_TERMUX_DEVS;
            default:
                return "Unknown";
        }
    }

    /**
     * Get a process id of the main app process of the {@link TermuxConstants#TERMUX_PACKAGE_NAME}
     * package.
     *
     * @param context The context for operations.
     * @return Returns the process if found and running, otherwise {@code null}.
     */
    public static String getTermuxAppPID(final Context context) {
        return PackageUtils.getPackagePID(context, TermuxConstants.TERMUX_PACKAGE_NAME);
    }

}
