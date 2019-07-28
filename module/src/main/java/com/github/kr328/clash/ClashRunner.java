package com.github.kr328.clash;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ClashRunner {
    interface Callback {
        void onStarted();
        void onStopped();
    }

    private final static Pattern PATTERN_CLASH_PID_OUTPUT = Pattern.compile("CLASH_PID=\\[(\\d+)]");

    private String coreDir;
    private String dataDir;
    private Callback callback;

    private Process process;
    private int pid;

    ClashRunner(String coreDir, String dataDir, Callback callback) {
        this.coreDir = coreDir;
        this.dataDir = dataDir;
        this.callback = callback;
    }

    synchronized void start() {
        if (process != null)
            return;

        try {
            String command = coreDir + "/setuidgid " + Constants.CLASH_UID + " " + Constants.CLASH_GID + " " + coreDir + "/clash -d " + dataDir + " 2>&1";

            Log.d(Constants.TAG, "Starting clash " + command);

            process = Runtime.getRuntime().exec("/system/bin/sh");

            process.getOutputStream().write(("echo \"CLASH_PID=[$$]\"\n").getBytes());
            process.getOutputStream().write(("exec " + command + "\n").getBytes());
            process.getOutputStream().flush();

            new Thread(() -> {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;

                try {
                    while ((line = reader.readLine()) != null) {
                        Matcher matcher = PATTERN_CLASH_PID_OUTPUT.matcher(line);
                        if ( matcher.find() ) {
                            pid = Integer.parseInt(matcher.group(1));
                            break;
                        }
                    }

                    while ((line = reader.readLine()) != null)
                        Log.i(Constants.TAG, line);

                    reader.close();

                    synchronized (ClashRunner.this) {
                        process = null;

                        callback.onStopped();
                    }
                } catch (IOException e) {
                    Log.i(Constants.TAG, "Clash stdout closed");
                }


            }).start();
        } catch (IOException e) {
            Log.e(Constants.TAG, "Start clash process failure", e);
        }

        Log.i(Constants.TAG, "Clash started pid=" + pid);
        callback.onStarted();
    }

    synchronized void stop() {
        if (process == null)
            return;

        android.os.Process.killProcess(pid);
    }

    void restart() {
        stop();
        Utils.waitForProcessExited(pid);
        start();
    }
}
