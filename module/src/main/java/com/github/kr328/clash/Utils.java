package com.github.kr328.clash;

import android.util.Log;

import java.io.*;
import java.util.ArrayList;

class Utils {
    static void waitForUserUnlocked() {
        File file = new File("/sdcard/Android");

        while ( !file.isDirectory() ) {
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

            Log.i(Constants.TAG, "Wait 1s for user unlock");
        }
    }

    static File findLatestFile(File file, String... suffix) {
        File[] files = file.listFiles();

        if ( files == null )
            return null;

        File result = null;
        long last = -1;

        for ( File f : files ) {
            for ( String s : suffix ) {
                if ( f.getName().endsWith(s) ) {
                    long current = f.lastModified();
                    if ( current > last ) {
                        result = f;
                        last = current;
                    }
                }
            }
        }

        return result;
    }

    static void copyFile(File from, File to) throws IOException {
        FileInputStream inputStream = new FileInputStream(from);
        FileOutputStream outputStream = new FileOutputStream(to);
        byte[] buffer = new byte[1024];
        int readLength;

        while (( readLength = inputStream.read(buffer)) > 0 )
            outputStream.write(buffer, 0, readLength);

        inputStream.close();
        outputStream.close();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    static void deleteFiles(String baseDir, String... files) {
        for ( String f : files )
            new File(baseDir, f).delete();
    }

    static String readString(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        byte[] buffer = new byte[stream.available()];

        stream.read(buffer);

        stream.close();

        return new String(buffer);
    }

    static void waitForProcessExited(int pid) {
        while ( new File("/proc/" + pid).exists() ) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
