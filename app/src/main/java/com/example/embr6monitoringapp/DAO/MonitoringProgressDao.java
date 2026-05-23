package com.example.embr6monitoringapp.DAO;

import com.example.embr6monitoringapp.Models.MonitoringRecord;

import java.util.List;

public interface MonitoringProgressDao {

    long insertRecord(MonitoringRecord record);

    boolean updatePurposeSection(int recordId, String purposeStatus,
                                 String findings, String evidenceImageUri);

    boolean updateFullRecord(MonitoringRecord record);

    boolean markComplete(int recordId);

    List<MonitoringRecord> getRecordsByEmployee(String employeeId);

    MonitoringRecord getRecordById(int recordId);

    List<MonitoringRecord> getCompletedRecordsByEmployee(String employeeId);

    // ---- Archive-specific methods ----

    /** Returns all records where is_archived = 1 for the given employee. */
    List<MonitoringRecord> getArchivedRecordsByEmployee(String employeeId);

    /**
     * Set is_archived on a single record.
     *
     * @param recordId the record primary key
     * @param status   1 = archived, 0 = unarchived
     */
    boolean updateArchiveStatus(int recordId, int status);

    /** Permanently delete a single record by its primary key. */
    boolean deleteRecord(int recordId);

    /**
     * Batch-update archive status in a single transaction.
     *
     * @return number of rows successfully updated
     */
    int batchUpdateArchiveStatus(List<Integer> recordIds, int status);

    /**
     * Batch-delete records in a single transaction.
     *
     * @return number of rows successfully deleted
     */
    int batchDeleteRecords(List<Integer> recordIds);
}