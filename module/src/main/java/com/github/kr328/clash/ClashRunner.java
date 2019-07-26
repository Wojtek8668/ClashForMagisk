package com.github.kr328.clash;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

class ClashRunner {
    interface Callback {
        void onStarted();

        void onStopped();
    }

    private String baseDir;
    private String dataDir;
    private String tempDir;
    private Process process;
    private Callback callback;

    ClashRunner(String baseDir, String dataDir, String tempDir, Callback callback) {
        this.baseDir = baseDir;
        this.dataDir = dataDir;
        this.tempDir = tempDir;
        this.callback = callback;
    }

    synchronized void start() {
        if (process != null)
            return;

        try {
            copyConfig();

            String command = baseDir + "/setuidgid " + Constants.CLASH_UID + " " + Constants.CLASH_GID + " " + baseDir + "/clash -d " + tempDir + " 2>&1";

            Log.d(Constants.TAG, "Starting clash " + command);

            process = Runtime.getRuntime().exec("/system/bin/sh");

            process.getOutputStream().write(("echo $$ > " + tempDir + "/clash_pid\n").getBytes());
            process.getOutputStream().write(("exec " + command + "\n").getBytes());
            process.getOutputStream().flush();

            new Thread(() -> {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;

                try {
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

        Log.i(Constants.TAG, "Clash started");
        callback.onStarted();
    }

    synchronized void stop() {
        if (process == null)
            return;

        try {
            android.os.Process.killProcess(Integer.parseInt(Utils.readString(new File(tempDir, "clash_pid")).trim()));
        } catch (IOException e) {
            Log.e(Constants.TAG, "Try stop clash failure", e);
        }
    }

    private void copyConfig() throws IOException {
        File file = Utils.findLatestFile(new File(dataDir), ".yaml");
        if (file == null)
            return;

        Utils.copyFile(file, new File(tempDir, "config.yaml"));
    }
}
