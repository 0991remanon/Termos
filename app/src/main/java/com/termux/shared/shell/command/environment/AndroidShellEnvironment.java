package com.termux.shared.shell.command.environment;

import android.content.Context;

import androidx.annotation.NonNull;

import com.termux.shared.shell.command.ExecutionCommand;
import com.termux.shared.termux.TermuxConstants;

import java.io.File;
import java.util.HashMap;

/**
 * Environment for Android.
 * <p><a href="
 ">* https://cs.android.com/android/platform/superproject/+/android-12.0.0_r32:frameworks/base/core/java/android/os/Environment.</a>java<a href="
 ">* https://cs.android.com/android/platform/superproject/+/android-12.0.0_r32:system/core/rootdir/init.environ.r</a>c.in<a href="
 ">* https://cs.android.com/android/platform/superproject/+/android-5.0.0_r1.0.1:system/core/rootdir/init.environ.r</a>c.in<a href="
 ">* https://cs.android.com/android/_/android/platform/system/core/+/refs/tags/android-12.0.0_r32:rootdir/init.rc;l</a>=910<a href="
 ">* https://cs.android.com/android/platform/superproject/+/android-12.0.0_r32:packages/modules/SdkExtensions/derive_classpath/derive_classpath.cpp;</a>l=96
 */
public class AndroidShellEnvironment extends UnixShellEnvironment {

    protected ShellCommandShellEnvironment shellCommandShellEnvironment;

    public AndroidShellEnvironment() {
        shellCommandShellEnvironment = new ShellCommandShellEnvironment();
    }

    /** Get shell environment for Android. */
    @NonNull
    @Override
    public HashMap<String, String> getEnvironment(@NonNull Context currentPackageContext) {
        HashMap<String, String> environment = new HashMap<>();

        environment.put(ENV_HOME, TermuxConstants.TERMUX_HOME_DIR_PATH);
        environment.put(ENV_LANG, "en_US.UTF-8");
        environment.put(ENV_PATH, System.getenv(ENV_PATH));
        environment.put(ENV_TMPDIR, TermuxConstants.TERMUX_TMP_PREFIX_DIR_PATH);

        environment.put(ENV_COLORTERM, "truecolor");
        environment.put(ENV_TERM, "xterm-256color");

        ShellEnvironmentUtils.putToEnvIfInSystemEnv(environment, "ANDROID_ASSETS");
        ShellEnvironmentUtils.putToEnvIfInSystemEnv(environment, "ANDROID_DATA");
        ShellEnvironmentUtils.putToEnvIfInSystemEnv(environment, "ANDROID_ROOT");
        ShellEnvironmentUtils.putToEnvIfInSystemEnv(environment, "ANDROID_STORAGE");

        // EXTERNAL_STORAGE is needed for /system/bin/am to work on at least
        // Samsung S7 - see https://plus.google.com/110070148244138185604/posts/gp8Lk3aCGp3.
        // https://cs.android.com/android/_/android/platform/system/core/+/fc000489
        ShellEnvironmentUtils.putToEnvIfInSystemEnv(environment, "EXTERNAL_STORAGE");
        ShellEnvironmentUtils.putToEnvIfInSystemEnv(environment, "ASEC_MOUNTPOINT");
        ShellEnvironmentUtils.putToEnvIfInSystemEnv(environment, "LOOP_MOUNTPOINT");

        ShellEnvironmentUtils.putToEnvIfInSystemEnv(environment, "ANDROID_RUNTIME_ROOT");
        ShellEnvironmentUtils.putToEnvIfInSystemEnv(environment, "ANDROID_ART_ROOT");
        ShellEnvironmentUtils.putToEnvIfInSystemEnv(environment, "ANDROID_I18N_ROOT");
        ShellEnvironmentUtils.putToEnvIfInSystemEnv(environment, "ANDROID_TZDATA_ROOT");

        ShellEnvironmentUtils.putToEnvIfInSystemEnv(environment, "BOOTCLASSPATH");
        ShellEnvironmentUtils.putToEnvIfInSystemEnv(environment, "DEX2OATBOOTCLASSPATH");
        ShellEnvironmentUtils.putToEnvIfInSystemEnv(environment, "SYSTEMSERVERCLASSPATH");

        return environment;
    }

    @NonNull
    @Override
    public String getDefaultWorkingDirectoryPath() {
        return "/";
    }

    @NonNull
    @Override
    public HashMap<String, String> setupShellCommandEnvironment(@NonNull Context currentPackageContext,
                                                                @NonNull ExecutionCommand executionCommand) {
        HashMap<String, String> environment = getEnvironment(currentPackageContext);

        String workingDirectory = executionCommand.workingDirectory;
        environment.put(ENV_PWD,
            workingDirectory != null && !workingDirectory.isEmpty() ? new File(workingDirectory).getAbsolutePath() : // PWD must be absolute path
            getDefaultWorkingDirectoryPath());
        ShellEnvironmentUtils.createHomeDir(environment);

        if (executionCommand.setShellCommandShellEnvironment && shellCommandShellEnvironment != null)
            environment.putAll(shellCommandShellEnvironment.getEnvironment(currentPackageContext, executionCommand));

        return environment;
    }

}
