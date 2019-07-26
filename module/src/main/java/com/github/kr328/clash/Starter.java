package com.github.kr328.clash;

import android.util.Log;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.File;
import java.io.IOException;

public class Starter {
    private String baseDir;
    private String dataDir;
    private String tempDir;

    private Starter(String baseDir, String dataDir, String tempDir) {
        this.baseDir = baseDir;
        this.dataDir = dataDir;
        this.tempDir = tempDir;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    private void exec() {
        //noinspection ResultOfMethodCallIgnored
        new File(dataDir).mkdirs();

        ClashRunner runner = new ClashRunner(baseDir, dataDir, tempDir, new ClashRunner.Callback() {
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
                switch (type) {
                    case "START":
                        runner.start();
                        break;
                    case "STOP":
                        runner.stop();
                        break;
                    case "RESTART":
                        runner.stop();
                        runner.start();
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
        if ( args.length != 3 ) {
            System.err.println("Usage: app_process /system/bin com.github.kr328.clash.Starter [BASE-DIR] [DATA-DIR] [TEMP-DIR]");
            System.exit(1);
        }

        Log.i(Constants.TAG, "Starter started");

        Utils.waitForUserUnlocked();

        new Starter(args[0], args[1], args[2]).exec();
    }
}
