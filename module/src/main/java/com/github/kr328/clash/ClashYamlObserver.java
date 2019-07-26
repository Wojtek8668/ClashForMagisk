package com.github.kr328.clash;

import android.os.FileObserver;
import android.util.Log;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class ClashYamlObserver {
    public interface Callback {
        void onDataDirChanged();
        void onUserControl(String type);
    }

    private Callback callback;
    private File dataDir;
    private FileObserver fileObserver;
    private Timer timer = new Timer();

    ClashYamlObserver(String data, Callback callback) {
        this.dataDir = new File(data);
        this.callback = callback;

        restart();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        if ( fileObserver != null )
            fileObserver.stopWatching();
    }

    private void restart() {
        //noinspection ResultOfMethodCallIgnored
        dataDir.mkdirs();

        fileObserver = new FileObserver(dataDir.getAbsolutePath(), FileObserver.CREATE | FileObserver.MODIFY | FileObserver.DELETE_SELF | FileObserver.MOVED_TO ) {
            @Override
            public void onEvent(int event, String file) {
                Log.d(Constants.TAG, "Data File Changed " + file);

                if (( event & FileObserver.DELETE_SELF ) != 0 ) {
                    restart();
                }
                else {
                    if ( file.endsWith(".yaml") || file.endsWith(".yml") )
                        onDataDirChanged();
                    else {
                        switch (file) {
                            case "STOP":
                            case "START":
                            case "RESTART":
                                Utils.deleteFiles(dataDir.getAbsolutePath(), "STOP", "START", "RESTART");
                                callback.onUserControl(file);
                        }
                    }
                }
            }
        };

        fileObserver.startWatching();
    }

    private synchronized void onDataDirChanged() {
        resetTimer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                callback.onDataDirChanged();
            }
        }, 10 * 1000);
    }

    private void resetTimer() {
        try {
            timer.purge();
            timer.cancel();
        }
        catch (IllegalStateException ignored) { }
    }
}
