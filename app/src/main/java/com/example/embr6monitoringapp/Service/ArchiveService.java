package com.example.embr6monitoringapp.Service;

import com.example.embr6monitoringapp.Models.MonitoringRecord;

import java.util.List;

public interface ArchiveService {

    List<MonitoringRecord> getArchivedRecords(String employeeId);

    boolean unarchiveRecord(int recordId);

    boolean deleteRecordPermanently(int recordId);

    int batchUnarchiveRecords(List<Integer> recordIds);

    int batchDeleteRecordsPermanently(List<Integer> recordIds);

    int getArchivedCount(String employeeId);

    int getRecentlyArchivedCount(String employeeId);

    List<MonitoringRecord> searchArchivedRecords(String employeeId, String query);

    boolean isArchiveEmpty(String employeeId);

    String getEmptyStateMessage(String searchQuery);

    int getEmptyStateIcon();
}