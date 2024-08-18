package com.termux.shared.errors;

import android.content.Context;

import androidx.annotation.NonNull;

import com.termux.shared.markdown.MarkdownUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Error implements Serializable {

    /** The optional error label. */
    private String label;
    /** The error type. */
    private String type;
    /** The error code. */
    private int code;
    /** The error message. */
    private String message;
    /** The error exceptions. */
    private List<Throwable> throwablesList = new ArrayList<>();

    public Error() {
        InitError(null, null, null, null);
    }

    public Error(String type, Integer code, String message, List<Throwable> throwablesList) {
        InitError(type, code, message, throwablesList);
    }

    public Error(String type, Integer code, String message, Throwable throwable) {
        InitError(type, code, message, Collections.singletonList(throwable));
    }

    public Error(String type, Integer code, String message) {
        InitError(type, code, message, null);
    }

    public Error(Integer code, String message, List<Throwable> throwablesList) {
        InitError(null, code, message, throwablesList);
    }

    public Error(Integer code, String message, Throwable throwable) {
        InitError(null, code, message, Collections.singletonList(throwable));
    }

    public Error(Integer code, String message) {
        InitError(null, code, message, null);
    }

    public Error(String message, Throwable throwable) {
        InitError(null, null, message, Collections.singletonList(throwable));
    }

    public Error(String message, List<Throwable> throwablesList) {
        InitError(null, null, message, throwablesList);
    }

    public Error(String message) {
        InitError(null, null, message, null);
    }

    private void InitError(String type, Integer code, String message, List<Throwable> throwablesList) {
        if (type != null && !type.isEmpty())
            this.type = type;
        else
            this.type = Errno.TYPE;

        if (code != null && code > Errno.ERRNO_SUCCESS.getCode())
            this.code = code;
        else
            this.code = Errno.ERRNO_SUCCESS.getCode();

        this.message = message;

        if (throwablesList != null)
            this.throwablesList = throwablesList;
    }

    public Error setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getLabel() {
        return label;
    }


    public String getType() {
        return type;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void appendMessage(String message) {
        if (message != null && isStateFailed())
            this.message = this.message + message;
    }

    public List<Throwable> getThrowablesList() {
        return Collections.unmodifiableList(throwablesList);
    }

    public synchronized boolean setStateFailed(String type, int code, String message, List<Throwable> throwablesList) {
        this.message = message;
        this.throwablesList = throwablesList;

        if (type != null && !type.isEmpty())
            this.type = type;

        if (code > Errno.ERRNO_SUCCESS.getCode()) {
            this.code = code;
            return true;
        } else {
            this.code = Errno.ERRNO_FAILED.getCode();
            return false;
        }
    }

    public boolean isStateFailed() {
        return code > Errno.ERRNO_SUCCESS.getCode();
    }

    public String getErrorMarkdownString() {
        StringBuilder markdownString = new StringBuilder();

        markdownString.append(MarkdownUtils.getSingleLineMarkdownStringEntry("Error Code", getCode(), "-"));
        markdownString.append("\n").append(MarkdownUtils.getMultiLineMarkdownStringEntry(
            (Errno.TYPE.equals(getType()) ? "Error Message" : "Error Message (" + getType() + ")"), message, "-"));
        if (throwablesList != null && throwablesList.size() > 0)
            markdownString.append("\n\n");

        return markdownString.toString();
    }
}
