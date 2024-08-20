package com.termux.shared.shell.command.result;

import android.app.PendingIntent;

import com.termux.shared.markdown.MarkdownUtils;

import java.util.Formatter;

public class ResultConfig {

    /** Defines {@link PendingIntent} that should be sent with the result of the command. We cannot
     * implement {@link java.io.Serializable} because {@link PendingIntent} cannot be serialized. */
    public PendingIntent resultPendingIntent;
    /** The key with which to send result {@link android.os.Bundle} in {@link #resultPendingIntent}. */
    public String resultBundleKey;
    /** The key with which to send {@link ResultData#stdout} in {@link #resultPendingIntent}. */
    public String resultStdoutKey;
    /** The key with which to send {@link ResultData#stderr} in {@link #resultPendingIntent}. */
    public String resultStderrKey;
    /** The key with which to send {@link ResultData#exitCode} in {@link #resultPendingIntent}. */
    public String resultExitCodeKey;
    /** The key with which to send {@link ResultData#errorsList} errCode in {@link #resultPendingIntent}. */
    public String resultErrCodeKey;
    /** The key with which to send {@link ResultData#errorsList} errmsg in {@link #resultPendingIntent}. */
    public String resultErrmsgKey;
    /** The key with which to send original length of {@link ResultData#stdout} in {@link #resultPendingIntent}. */
    public String resultStdoutOriginalLengthKey;
    /** The key with which to send original length of {@link ResultData#stderr} in {@link #resultPendingIntent}. */
    public String resultStderrOriginalLengthKey;


    /** Defines the directory path in which to write the result of the command. */
    public String resultDirectoryPath;
    /** Defines the directory path under which {@link #resultDirectoryPath} can exist. */
    public String resultDirectoryAllowedParentPath;
    /** Defines whether the result should be written to a single file or multiple files
     * (err, error, stdout, stderr, exit_code) in {@link #resultDirectoryPath}. */
    public boolean resultSingleFile;
    /** Defines the basename of the result file that should be created in {@link #resultDirectoryPath}
     * if {@link #resultSingleFile} is {@code true}. */
    public String resultFileBasename;
    /** Defines the output {@link Formatter} format of the {@link #resultFileBasename} result file. */
    public String resultFileOutputFormat;
    /** Defines the error {@link Formatter} format of the {@link #resultFileBasename} result file. */
    public String resultFileErrorFormat;
    /** Defines the suffix of the result files that should be created in {@link #resultDirectoryPath}
     * if {@link #resultSingleFile} is {@code true}. */
    public String resultFilesSuffix;


    public ResultConfig() {
    }


    public boolean isCommandWithPendingResult() {
        return resultPendingIntent != null || resultDirectoryPath != null;
    }

    /**
     * Get a markdown {@link String} for {@link ResultConfig}.
     *
     * @param resultConfig The {@link ResultConfig} to convert.
     * @return Returns the markdown {@link String}.
     */
    public static String getResultConfigMarkdownString(final ResultConfig resultConfig) {
        if (resultConfig == null) return "null";

        StringBuilder markdownString = new StringBuilder();

        if (resultConfig.resultPendingIntent != null)
            markdownString.append(MarkdownUtils.getSingleLineMarkdownStringEntry("Result PendingIntent Creator", resultConfig.resultPendingIntent.getCreatorPackage(), "-"));
        else
            markdownString.append("**Result PendingIntent Creator:** -  ");

        if (resultConfig.resultDirectoryPath != null) {
            markdownString.append("\n").append(MarkdownUtils.getSingleLineMarkdownStringEntry("Result Directory Path", resultConfig.resultDirectoryPath, "-"));
            markdownString.append("\n").append(MarkdownUtils.getSingleLineMarkdownStringEntry("Result Single File", resultConfig.resultSingleFile, "-"));
            markdownString.append("\n").append(MarkdownUtils.getSingleLineMarkdownStringEntry("Result File Basename", resultConfig.resultFileBasename, "-"));
            markdownString.append("\n").append(MarkdownUtils.getSingleLineMarkdownStringEntry("Result File Output Format", resultConfig.resultFileOutputFormat, "-"));
            markdownString.append("\n").append(MarkdownUtils.getSingleLineMarkdownStringEntry("Result File Error Format", resultConfig.resultFileErrorFormat, "-"));
            markdownString.append("\n").append(MarkdownUtils.getSingleLineMarkdownStringEntry("Result Files Suffix", resultConfig.resultFilesSuffix, "-"));
        }

        return markdownString.toString();
    }

}
