package com.detoxyourtv.home;

import android.content.Context;
import android.content.SharedPreferences;

final class CrashGuard {

    private static final String PREFS = "crash_guard";
    private static final String CRASH_TIMES = "crash_times";
    private static final String SEPARATOR = ",";
    private static final int CRASH_LIMIT = 3;
    private static final long WINDOW_MS = 5 * 60 * 1000L;

    private static boolean installed;

    private CrashGuard() {
    }

    static synchronized void install(Context context) {
        if (installed) return;
        installed = true;
        Context app = context.getApplicationContext();
        Thread.UncaughtExceptionHandler previous = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            recordCrash(app);
            if (previous != null) previous.uncaughtException(thread, error);
        });
    }

    private static void recordCrash(Context app) {
        long now = System.currentTimeMillis();
        SharedPreferences prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        StringBuilder recent = new StringBuilder(String.valueOf(now));
        int count = 1;
        for (String stamp : prefs.getString(CRASH_TIMES, "").split(SEPARATOR)) {
            if (isRecent(stamp, now)) {
                recent.append(SEPARATOR).append(stamp);
                count++;
            }
        }
        if (count >= CRASH_LIMIT) {
            enterSafeMode(app);
            prefs.edit().remove(CRASH_TIMES).commit();
            return;
        }
        prefs.edit().putString(CRASH_TIMES, recent.toString()).commit();
    }

    private static boolean isRecent(String stamp, long now) {
        try {
            return now - Long.parseLong(stamp.trim()) < WINDOW_MS;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void enterSafeMode(Context app) {
        app.getSharedPreferences(Config.SETTINGS_NAME, Context.MODE_PRIVATE).edit()
                .putBoolean("animations", false)
                .putBoolean("advanced_effects", false)
                .commit();
    }
}
