package com.termux.shared.shell.command.environment;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.shared.shell.ShellUtils;
import com.termux.shared.shell.command.ExecutionCommand;
import com.termux.shared.termux.TermuxConstants;

import java.util.HashMap;

/**
 * Environment for Unix-like systems.
 *
 * https://manpages.debian.org/testing/manpages/environ.7.en.html
 * https://pubs.opengroup.org/onlinepubs/9699919799/basedefs/V1_chap08.html
 */
public abstract class UnixShellEnvironment implements IShellEnvironment {

    /** Environment variable for the terminal's colour capabilities. */
    public static final String ENV_COLORTERM = "COLORTERM";

    /** Environment variable for the path of the user's home directory. */
    public static final String ENV_HOME = "HOME";

    /** Environment variable for the locale category for native language, local customs, and coded
     * character set in the absence of the LC_ALL and other LC_* environment variables. */
    public static final String ENV_LANG = "LANG";

    /** Environment variable for the represent the sequence of directory path prefixes separated with
     * colons ":" that certain functions and utilities apply in searching for an executable file
     * known only by a filename. */
    public static final String ENV_PATH = "PATH";

    /** Environment variable for the absolute path of the current working directory. It shall not
     * contain any components that are dot or dot-dot. The value is set by the cd utility, and by
     * the sh utility during initialization. */
    public static final String ENV_PWD = "PWD";

    /** Environment variable for the terminal type for which output is to be prepared. This information
     * is used by utilities and application programs wishing to exploit special capabilities specific
     * to a terminal. The format and allowable values of this environment variable are unspecified. */
    public static final String ENV_TERM = "TERM";

    /** Environment variable for the path of a directory made available for programs that need a place
     * to create temporary files. */
    public static final String ENV_TMPDIR = "TMPDIR";

    /** Possible paths to common/supported login shell binaries . */
    public static final String[] LOGIN_SHELL_BIN_PATHS = new String[]{TermuxConstants.TERMUX_BIN_PREFIX_DIR_PATH, "/product/bin", "/apex/com.android.runtime/bin", "/apex/com.android.art/bin", "/system_ext/bin", "/system/bin", "/system/xbin", "/odm/bin", "/vendor/bin", "/vendor/xbin"};

    /** Names for common/supported login shell binaries. */
    public static final String[] LOGIN_SHELL_BINARIES = new String[]{"login", "bash", "zsh", "fish", "sh"};



    @NonNull
    public abstract HashMap<String, String> getEnvironment(@NonNull Context currentPackageContext);

    @NonNull
    @Override
    public abstract String getDefaultWorkingDirectoryPath();

    @NonNull
    @Override
    public String[] setupShellCommandArguments(@NonNull String executable, @Nullable String[] arguments) {
        return ShellUtils.setupShellCommandArguments(executable, arguments);
    }

    @NonNull
    @Override
    public abstract HashMap<String, String> setupShellCommandEnvironment(@NonNull Context currentPackageContext,
                                                                         @NonNull ExecutionCommand executionCommand);

}
