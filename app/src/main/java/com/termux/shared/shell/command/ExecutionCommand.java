package com.termux.shared.shell.command;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.shared.errors.Error;
import com.termux.shared.shell.command.result.ResultConfig;
import com.termux.shared.shell.command.result.ResultData;
import com.termux.shared.shell.command.runner.app.AppShell;
import com.termux.terminal.TerminalSession;

import java.util.Collections;
import java.util.List;

public class ExecutionCommand {

    /*
    The {@link ExecutionState#SUCCESS} and {@link ExecutionState#FAILED} is defined based on
    successful execution of command without any internal errors or exceptions being raised.
    The shell command {@link #exitCode} being non-zero **does not** mean that execution command failed.
    Only the {@link #errCode} being non-zero means that execution command failed from the Termux app
    perspective.
    */

    /** The {@link Enum} that defines {@link ExecutionCommand} state. */
    public enum ExecutionState {

        PRE_EXECUTION("Pre-Execution", 0),
        EXECUTING("Executing", 1),
        EXECUTED("Executed", 2),
        SUCCESS("Success", 3),
        FAILED("Failed", 4);

        private final String name;
        private final int value;

        ExecutionState(final String name, final int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }


    }

    public enum Runner {

        /** Run command in {@link TerminalSession}. */
        TERMINAL_SESSION("terminal-session"),

        /** Run command in {@link AppShell}. */
        APP_SHELL("app-shell");

        ///** Run command in {@link AdbShell}. */
        //ADB_SHELL("adb-shell"),

        ///** Run command in {@link RootShell}. */
        //ROOT_SHELL("root-shell");

        private final String name;

        Runner(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public boolean equalsRunner(String runner) {
            return runner != null && runner.equals(this.name);
        }

        /** Get {@link Runner} for {@code name} if found, otherwise {@code null}. */
        @Nullable
        public static Runner runnerOf(String name) {
            for (Runner v : Runner.values()) {
                if (v.name.equals(name)) {
                    return v;
                }
            }
            return null;
        }

        /** Get {@link Runner} for {@code name} if found, otherwise {@code def}. */
        @NonNull
        public static Runner runnerOf(@Nullable String name, @NonNull Runner def) {
            Runner runner = runnerOf(name);
            return runner != null ? runner : def;
        }

    }

    public enum ShellCreateMode {

        /** Always create {@link TerminalSession}. */
        ALWAYS("always"),

        /** Create shell only if no shell with {@link #shellName} found. */
        NO_SHELL_WITH_NAME("no-shell-with-name");

        private final String mode;

        ShellCreateMode(final String mode) {
            this.mode = mode;
        }

        public String getMode() {
            return mode;
        }

        public boolean equalsMode(String sessionCreateMode) {
            return sessionCreateMode != null && sessionCreateMode.equals(this.mode);
        }

        /** Get {@link ShellCreateMode} for {@code mode} if found, otherwise {@code null}. */
        @Nullable
        public static ShellCreateMode modeOf(String mode) {
            for (ShellCreateMode v : ShellCreateMode.values()) {
                if (v.mode.equals(mode)) {
                    return v;
                }
            }
            return null;
        }

    }

    /** The optional unique id for the {@link ExecutionCommand}. This should equal -1 if execution
     * command is not going to be managed by a shell manager. */
    public Integer id;

    /** The process id of command. */
    public int mPid = -1;

    /** The current state of the {@link ExecutionCommand}. */
    private ExecutionState currentState = ExecutionState.PRE_EXECUTION;
    /** The previous state of the {@link ExecutionCommand}. */
    private ExecutionState previousState = ExecutionState.PRE_EXECUTION;


    /** The executable for the {@link ExecutionCommand}. */
    public String executable;
    /** The executable Uri for the {@link ExecutionCommand}. */
    public Uri executableUri;
    /** The executable arguments array for the {@link ExecutionCommand}. */
    public String[] arguments;
    /** The stdin string for the {@link ExecutionCommand}. */
    public String stdin;
    /** The current working directory for the {@link ExecutionCommand}. */
    public String workingDirectory;


    /** The terminal transcript rows for the {@link ExecutionCommand}. */
    public Integer terminalTranscriptRows;


    /** The {@link Runner} for the {@link ExecutionCommand}. */
    public String runner;


    public Integer backgroundCustomLogLevel;


    /** The session action of {@link Runner#TERMINAL_SESSION} commands. */
    public String sessionAction;


    /** The shell name of commands. */
    public String shellName;

    /** The {@link ShellCreateMode} of commands. */
    public String shellCreateMode;

    /** Whether to set {@link ExecutionCommand} shell environment. */
    public boolean setShellCommandShellEnvironment;




    /** The command label for the {@link ExecutionCommand}. */
    public String commandLabel;
    /** The markdown text for the command description for the {@link ExecutionCommand}. */
    public String commandDescription;
    /** The markdown text for the help of command for the {@link ExecutionCommand}. This can be used
     * to provide useful info to the user if an internal error is raised. */
    public String commandHelp;


    /** Defines the markdown text for the help of the Termux plugin API that was used to start the
     * {@link ExecutionCommand}. This can be used to provide useful info to the user if an internal
     * error is raised. */
    public String pluginAPIHelp;


    /** Defines the {@link Intent} received which started the command. */
    public Intent commandIntent;

    /** Defines if {@link ExecutionCommand} was started because of an external plugin request
     * like with an intent or from within Termux app itself. */
    public boolean isPluginExecutionCommand;

    /** Defines the {@link ResultConfig} for the {@link ExecutionCommand} containing information
     * on how to handle the result. */
    public final ResultConfig resultConfig = new ResultConfig();

    /** Defines the {@link ResultData} for the {@link ExecutionCommand} containing information
     * of the result. */
    public final ResultData resultData = new ResultData();


    /** Defines if processing results already called for this {@link ExecutionCommand}. */
    public boolean processingResultsAlreadyCalled;


    public ExecutionCommand() {
    }

    public ExecutionCommand(Integer id) {
        this.id = id;
    }

    public ExecutionCommand(Integer id, String executable, String[] arguments, String stdin, String workingDirectory, String runner) {
        this.id = id;
        this.executable = executable;
        this.arguments = arguments;
        this.stdin = stdin;
        this.workingDirectory = workingDirectory;
        this.runner = runner;
    }


    public boolean isPluginExecutionCommandWithPendingResult() {
        return isPluginExecutionCommand && resultConfig.isCommandWithPendingResult();
    }


    public synchronized boolean setState(ExecutionState newState) {
        // The state transition cannot go back or change if already at {@link ExecutionState#SUCCESS}
        if (newState.getValue() < currentState.getValue() || currentState == ExecutionState.SUCCESS) {
            return false;
        }

        // The {@link ExecutionState#FAILED} can be set again, like to add more errors, but we don't update
        // {@link #previousState} with the {@link #currentState} value if its at {@link ExecutionState#FAILED} to
        // preserve the last valid state
        if (currentState != ExecutionState.FAILED)
            previousState = currentState;

        currentState = newState;
        return  true;
    }

    public synchronized boolean hasExecuted() {
        return currentState.getValue() >= ExecutionState.EXECUTED.getValue();
    }

    public synchronized boolean isExecuting() {
        return currentState == ExecutionState.EXECUTING;
    }

    public synchronized boolean isSuccessful() {
        return currentState == ExecutionState.SUCCESS;
    }


    public synchronized boolean setStateFailed(@NonNull Error error) {
        return setStateFailed(error.getType(), error.getCode(), error.getMessage(), null);
    }


    public synchronized boolean setStateFailed(int code, String message) {
        return setStateFailed(null, code, message, null);
    }

    public synchronized boolean setStateFailed(int code, String message, Throwable throwable) {
        return setStateFailed(null, code, message, Collections.singletonList(throwable));
    }


    public synchronized boolean setStateFailed(String type, int code, String message, List<Throwable> throwablesList) {
this.resultData.setStateFailed(type, code, message, throwablesList);

        return setState(ExecutionState.FAILED);
    }

    public synchronized boolean shouldNotProcessResults() {
        if (processingResultsAlreadyCalled) {
            return true;
        } else {
            processingResultsAlreadyCalled = true;
            return false;
        }
    }

    public synchronized boolean isStateFailed() {
        if (currentState != ExecutionState.FAILED)
            return false;

        return resultData.isStateFailed();
    }


    public String getIdLogString() {
        if (id != null)
            return "(" + id + ") ";
        else
            return "";
    }

    public String getCommandLabelLogString() {
        if (commandLabel != null && !commandLabel.isEmpty())
            return commandLabel;
        else
            return "Execution Command";
    }

    public String getCommandIdAndLabelLogString() {
        return getIdLogString() + getCommandLabelLogString();
    }
}
