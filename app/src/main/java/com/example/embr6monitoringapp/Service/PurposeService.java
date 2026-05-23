package com.example.embr6monitoringapp.Service;

public interface PurposeService {
    String validate();
    boolean save();
    void resetForm();
    void syncNow();
    long getLastSavedId();
}