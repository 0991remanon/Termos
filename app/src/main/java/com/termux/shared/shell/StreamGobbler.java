/*
 * Copyright (C) 2012-2019 Jorrit "Chainfire" Jongma
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.termux.shared.shell;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


/**
 * Thread utility class continuously reading from an InputStream
 * <p><a href="
 ">* https://github.com/Chainfire/libsuperuser/blob/1.1.0.201907261845/libsuperuser/src/eu/chainfire/libsuperuser/Shell.java#</a>L141<a href="
 ">* https://github.com/Chainfire/libsuperuser/blob/1.1.0.201907261845/libsuperuser/src/eu/chainfire/libsuperuser/StreamGobbler.</a>java
 */
@SuppressWarnings({"WeakerAccess"})
public class StreamGobbler extends Thread {
    private static int threadCounter = 0;
    private static int incThreadCounter() {
        synchronized (StreamGobbler.class) {
            int ret = threadCounter;
            threadCounter++;
            return ret;
        }
    }

    /**
     * Line callback interface
     */
    public interface OnLineListener {
        /**
         * <p>Line callback</p>
         *
         * <p>This callback should process the line as quickly as possible.
         * Delays in this callback may pause the native process or even
         * result in a deadlock</p>
         *
         * @param line String that was gobbled
         */
        void onLine(String line);
    }

    /**
     * Stream closed callback interface
     */
    public interface OnStreamClosedListener {
        /**
         * <p>Stream closed callback</p>
         */
        void onStreamClosed();
    }

    @NonNull
    private final String shell;
    @NonNull
    private final InputStream inputStream;
    @NonNull
    private final BufferedReader reader;
    @Nullable
    private final List<String> listWriter;
    @Nullable
    private final StringBuilder stringWriter;
    @Nullable
    private final OnLineListener lineListener;
    @Nullable
    private final OnStreamClosedListener streamClosedListener;
    @Nullable

    private volatile boolean active = true;
    private volatile boolean calledOnClose = false;

    /**
     * <p>StreamGobbler constructor</p>
     *
     * <p>We use this class because shell STDOUT and STDERR should be read as quickly as
     * possible to prevent a deadlock from occurring, or Process.waitFor() never
     * returning (as the buffer is full, pausing the native process)</p>
     * Do not use this for concurrent reading for STDOUT and STDERR for the same StringBuilder since
     * its not synchronized.
     *
     * @param shell Name of the shell
     * @param inputStream InputStream to read from
     * @param outputString {@literal List<String>} to write to, or null
     * @param logLevel The custom log level to use for logging the command output. If set to
     */
    @AnyThread
    public StreamGobbler(@NonNull String shell, @NonNull InputStream inputStream,
                         @Nullable StringBuilder outputString,
                         @Nullable Integer logLevel) {
        super("Gobbler#" + incThreadCounter());
        this.shell = shell;
        this.inputStream = inputStream;
        reader = new BufferedReader(new InputStreamReader(inputStream));
        streamClosedListener = null;

        listWriter = null;
        stringWriter = outputString;
        lineListener = null;

    }

    @Override
    public void run() {
        // keep reading the InputStream until it ends (or an error occurs)
        // optionally pausing when a command is executed that consumes the InputStream itself
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (listWriter != null) listWriter.add(line);
                if (lineListener != null) lineListener.onLine(line);
                while (!active) {
                    synchronized (this) {
                        try {
                            this.wait(128);
                        } catch (InterruptedException e) {
                            // no action
                        }
                    }
                }
            }
        } catch (IOException e) {
            // reader probably closed, expected exit condition
            if (streamClosedListener != null) {
                calledOnClose = true;
                streamClosedListener.onStreamClosed();
            }
        }

        // make sure our stream is closed and resources will be freed
        try {
            reader.close();
        } catch (IOException e) {
            // read already closed
        }

        if (!calledOnClose) {
            if (streamClosedListener != null) {
                calledOnClose = true;
                streamClosedListener.onStreamClosed();
            }
        }
    }

}
