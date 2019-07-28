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
    private Thread dataChangedThread;

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
                    switch (file) {
                        case "config.yml":
                        case "config.yaml":
                            onDataDirChanged();
                            break;
                        case "STOP":
                        case "START":
                        case "RESTART":
                            callback.onUserControl(file);
                    }
                }
            }
        };

        fileObserver.startWatching();
    }

    private synchronized void onDataDirChanged() {
        if ( dataChangedThread != null )
            dataChangedThread.interrupt();
        dataChangedThread = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return;
            }

            callback.onDataDirChanged();
        });
    }
}
