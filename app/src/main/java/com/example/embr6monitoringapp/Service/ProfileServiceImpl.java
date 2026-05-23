package com.example.embr6monitoringapp.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;


public class ProfileServiceImpl implements ProfileService {

    private static final String PREFS_NAME = "ProfilePrefs";
    private static final String KEY_PREFIX = "profile_photo_";

    private final SharedPreferences prefs;

    public ProfileServiceImpl(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void saveProfilePhoto(String employeeId, Uri uri) {
        prefs.edit()
                .putString(KEY_PREFIX + employeeId, uri.toString())
                .apply();
    }

    @Override
    public String getSavedProfilePhoto(String employeeId) {
        return prefs.getString(KEY_PREFIX + employeeId, null);
    }
}