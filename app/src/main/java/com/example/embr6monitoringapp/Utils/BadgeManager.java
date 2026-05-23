package com.example.embr6monitoringapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class BadgeManager {
    private static final String PREF_NAME = "archive_badge_prefs";
    private static final String KEY_LAST_ARCHIVE_COUNT = "last_archive_count";

    private final SharedPreferences prefs;

    public BadgeManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveLastArchiveCount(int count) {
        prefs.edit().putInt(KEY_LAST_ARCHIVE_COUNT, count).apply();
    }

    public int getLastArchiveCount() {
        return prefs.getInt(KEY_LAST_ARCHIVE_COUNT, -1);
    }

    public boolean hasNewArchives(int currentCount) {
        int lastCount = getLastArchiveCount();
        return lastCount != -1 && currentCount > lastCount;
    }

    public void clearBadge() {
        saveLastArchiveCount(-1);
    }
}