package com.github.kr328.clash;

import android.util.Log;

import java.io.File;
import java.io.IOException;

public class Starter {
    private String coreDir;
    private String dataDir;

    private Starter(String coreDir, String dataDir) {
        this.coreDir = coreDir;
        this.dataDir = dataDir;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    private void exec() {
        //noinspection ResultOfMethodCallIgnored
        new File(dataDir).mkdirs();

        ClashRunner runner = new ClashRunner(coreDir, dataDir, new ClashRunner.Callback() {
            @Override
            public void onStarted() {
                Utils.deleteFiles(dataDir, "RUNNING", "STOPPED");

                try {
                    //noinspection ResultOfMethodCallIgnored
                    new File(dataDir, "RUNNING").createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStopped() {
                Utils.deleteFiles(dataDir, "RUNNING", "STOPPED");

                try {
                    //noinspection ResultOfMethodCallIgnored
                    new File(dataDir, "STOPPED").createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        ClashYamlObserver observer = new ClashYamlObserver(dataDir, new ClashYamlObserver.Callback() {
            @Override
            public void onDataDirChanged() {
                runner.stop();
                runner.start();
            }

            @Override
            public void onUserControl(String type) {
                Utils.deleteFiles(dataDir, "STOP", "START", "RESTART");

                switch (type) {
                    case "START":
                        runner.start();
                        break;
                    case "STOP":
                        runner.stop();
                        break;
                    case "RESTART":
                        runner.restart();
                        break;
                }
            }
        });

        runner.start();

        try {
            synchronized (this) {
                this.wait();
            }
        }
        catch (InterruptedException ignored) {

        }
    }

    public static void main(String[] args) {
        if ( args.length != 2 ) {
            System.err.println("Usage: app_process /system/bin com.github.kr328.clash.Starter [CORE-DIR] [DATA-DIR]");
            System.exit(1);
        }

        Log.i(Constants.TAG, "Starter started");

        Utils.waitForUserUnlocked();

        new Starter(args[0], args[1]).exec();
    }
}
