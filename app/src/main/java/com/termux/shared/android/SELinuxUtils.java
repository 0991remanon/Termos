package com.termux.shared.android;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.shared.reflection.ReflectionUtils;

import java.lang.reflect.Method;

public class SELinuxUtils {

    public static final String ANDROID_OS_SELINUX_CLASS = "android.os.SELinux";

    /**
     * Gets the security context of the current process.
     *
     * @return Returns a {@link String} representing the security context of the current process.
     * This will be {@code null} if an exception is raised.
     */
    @Nullable
    public static String getContext() {
        ReflectionUtils.bypassHiddenAPIReflectionRestrictions();
        String methodName = "getContext";
        try {
            @SuppressLint("PrivateApi") Class<?> clazz = Class.forName(ANDROID_OS_SELINUX_CLASS);
            Method method = ReflectionUtils.getDeclaredMethod(clazz, methodName);
            if (method == null) {
                return null;
            }

            return (String) ReflectionUtils.invokeMethod(method, null).value;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the security context of a file object.
     *
     * @param path The pathname of the file object.
     * @return Returns a {@link String} representing the security context of the file.
     * This will be {@code null} if an exception is raised.
     */
    @Nullable
    public static String getFileContext(@NonNull String path) {
        ReflectionUtils.bypassHiddenAPIReflectionRestrictions();
        String methodName = "getFileContext";
        try {
            @SuppressLint("PrivateApi") Class<?> clazz = Class.forName(ANDROID_OS_SELINUX_CLASS);
            Method method = ReflectionUtils.getDeclaredMethod(clazz, methodName, String.class);
            if (method == null) {
                return null;
            }

            return (String) ReflectionUtils.invokeMethod(method, null, path).value;
        } catch (Exception e) {
            return null;
        }
    }

}
