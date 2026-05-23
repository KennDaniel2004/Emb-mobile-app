package com.example.embr6monitoringapp.Service;

import com.example.embr6monitoringapp.Models.MonitoringRecord;

import java.util.List;

public interface MonitoringProgressService {

    long saveRecord(MonitoringRecord record);

    boolean savePurposeSection(int recordId, String purposeStatus,
                               String findings, String evidenceImageUri);

    boolean updateFullRecord(MonitoringRecord record);

    List<MonitoringRecord> getRecordsForEmployee(String employeeId);

    MonitoringRecord getRecordById(int recordId);

    List<MonitoringRecord> getCompletedRecordsByEmployee(String employeeId);
}