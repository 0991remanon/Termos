package com.termux.shared.shell.command.environment;

import static com.termux.shared.shell.command.environment.UnixShellEnvironment.ENV_HOME;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.shared.file.FileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShellEnvironmentUtils {

    /**
     * Convert environment {@link HashMap} to `environ` {@link List <String>}.
     * <p>
     * The items in the environ will have the format `name=value`.
     * <p>
     * Check {@link #isValidEnvironmentVariableName(String)} and {@link #isValidEnvironmentVariableValue(String)}
     * for valid variable names and values.
     *<a href=" <p>
     * https://manpages.debian.org/testing/manpages/envir">...</a>on.7.en.<a href="html
     ">* https://pubs.opengroup.org/onlinepubs/9699919799/basedefs/V1</a>_chap08.html
     */
    @NonNull
    public static List<String> convertEnvironmentToEnviron(@NonNull HashMap<String, String> environmentMap) {
        List<String> environmentList = new ArrayList<>(environmentMap.size());
        String value;
        for (String name : environmentMap.keySet()) {
            value = environmentMap.get(name);
            if (isValidEnvironmentVariableNameValuePair(name, value, true))
                environmentList.add(name + "=" + environmentMap.get(name));
        }
        return environmentList;
    }

    /**
     * Convert environment {@link HashMap} to {@link String} where each item equals "key=value".
     *
     */
    @NonNull
    public static String convertEnvironmentToDotEnvFile(@NonNull HashMap<String, String> environmentMap) {
        return convertEnvironmentToDotEnvFile(convertEnvironmentMapToEnvironmentVariableList(environmentMap));
    }

    /**
     * Convert environment {@link HashMap} to `.env` file {@link String}.
     * <p>
     * The items in the `.env` file have the format `export name="value"`.
     * <p>
     * If the {@link ShellEnvironmentVariable#escaped} is set to {@code true}, then
     * {@link ShellEnvironmentVariable#value} will be considered to be a literal value that has
     * already been escaped by the caller, otherwise all the `"`\$` in the value will be escaped
     * with `a backslash `\`, like `\"`. Note that if `$` is escaped and if its part of variable,
     * then variable expansion will not happen if `.env` file is sourced.
     * <p>
     * The `\` at the end of a value line means line continuation. Value can contain newline characters.
     * <p>
     * Check {@link #isValidEnvironmentVariableName(String)} and {@link #isValidEnvironmentVariableValue(String)}
     * for valid variable names and values<a href=".
     ">* <p>
     * https://github.com/ko1nksm/shdo</a>tenv#env<a href="-file-syntax
     ">* https://github.com/ko1nksm/shdotenv/blob/main/d</a>ocs/specification.md
     */
    @NonNull
    public static String convertEnvironmentToDotEnvFile(@NonNull List<ShellEnvironmentVariable> environmentList) {
        StringBuilder environment = new StringBuilder();
        Collections.sort(environmentList);
        for (ShellEnvironmentVariable variable : environmentList) {
            if (isValidEnvironmentVariableNameValuePair(variable.name, variable.value, true) && variable.value != null) {
                environment.append("export ").append(variable.name).append("=\"")
                    .append(variable.escaped ? variable.value : variable.value.replaceAll("([\"`\\\\$])", "\\\\$1"))
                    .append("\"\n");
            }
        }
        return environment.toString();
    }

    /**
     * Convert environment {@link HashMap} to {@link List< ShellEnvironmentVariable >}. Each item
     * will have its {@link ShellEnvironmentVariable#escaped} set to {@code false}.
     */
    @NonNull
    public static List<ShellEnvironmentVariable> convertEnvironmentMapToEnvironmentVariableList(@NonNull HashMap<String, String> environmentMap) {
        List<ShellEnvironmentVariable> environmentList = new ArrayList<>();
        for (String name :environmentMap.keySet()) {
            environmentList.add(new ShellEnvironmentVariable(name, environmentMap.get(name), false));
        }
        return environmentList;
    }

    /**
     * Check if environment variable name and value pair is valid. Errors will be logged if
     * {@code logErrors} is {@code true}.
     * <p>
     * Check {@link #isValidEnvironmentVariableName(String)} and {@link #isValidEnvironmentVariableValue(String)}
     * for valid variable names and values.
     */
    public static boolean isValidEnvironmentVariableNameValuePair(@Nullable String name, @Nullable String value, boolean logErrors) {
        if (!isValidEnvironmentVariableName(name)) {
            return false;
        }

        if (!isValidEnvironmentVariableValue(value)) {
            return !logErrors;
        }

        return true;
    }

    /**
     * Check if environment variable name is valid. It must not be {@code null} and must not contain
     * the null byte ('\0') and must only contain alphanumeric and underscore characters and must not
     * start with a digit.
     */
    public static boolean isValidEnvironmentVariableName(@Nullable String name) {
        return name != null && !name.contains("\0") && name.matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }

    /**
     * Check if environment variable value is valid. It must not be {@code null} and must not contain
     * the null byte ('\0').
     */
    public static boolean isValidEnvironmentVariableValue(@Nullable String value) {
        return value != null && !value.contains("\0");
    }



    /** Put value in environment if variable exists in {@link System) environment. */
    public static void putToEnvIfInSystemEnv(@NonNull HashMap<String, String> environment,
                                             @NonNull String name) {
        String value = System.getenv(name);
        if (value != null) {
            environment.put(name, value);
        }
    }

    /** Put {@link String} value in environment if value set. */
    public static void putToEnvIfSet(@NonNull HashMap<String, String> environment, @NonNull String name,
                                     @Nullable String value) {
        if (value != null) {
            environment.put(name, value);
        }
    }

    /** Put {@link Boolean} value "true" or "false" in environment if value set. */
    public static void putToEnvIfSet(@NonNull HashMap<String, String> environment, @NonNull String name,
                                     @Nullable Boolean value) {
        if (value != null) {
            environment.put(name, String.valueOf(value));
        }
    }



    /** Create HOME directory in environment {@link Map} if set. */
    public static void createHomeDir(@NonNull HashMap<String, String> environment) {
        String homeDirectory = environment.get(ENV_HOME);
        if (homeDirectory != null && !homeDirectory.isEmpty()) {
            FileUtils.createDirectoryFile("shell home", homeDirectory);

        }
    }

}
