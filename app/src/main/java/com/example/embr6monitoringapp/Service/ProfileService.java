package com.example.embr6monitoringapp.Service;

import android.net.Uri;

public interface ProfileService {

    void saveProfilePhoto(String employeeId, Uri uri);

    String getSavedProfilePhoto(String employeeId);
}