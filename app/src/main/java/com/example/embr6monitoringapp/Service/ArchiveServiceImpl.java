package com.example.embr6monitoringapp.Service;

import android.content.Context;
import android.util.Log;

import com.example.embr6monitoringapp.DAO.MonitoringProgressDao;
import com.example.embr6monitoringapp.DAO.MonitoringProgressDaoImpl;
import com.example.embr6monitoringapp.Models.MonitoringRecord;
import com.example.embr6monitoringapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArchiveServiceImpl implements ArchiveService {

    private static final String TAG = "ArchiveServiceImpl";

    private final MonitoringProgressDao dao;
    private final ExecutorService executorService;
    private final Context context;

    public ArchiveServiceImpl(Context context) {
        this.context = context;
        this.dao = new MonitoringProgressDaoImpl(context);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public List<MonitoringRecord> getArchivedRecords(String employeeId) {
        if (employeeId == null || employeeId.isEmpty()) {
            Log.e(TAG, "getArchivedRecords: employeeId is null or empty");
            return new ArrayList<>();
        }

        try {
            List<MonitoringRecord> records = dao.getArchivedRecordsByEmployee(employeeId);
            Log.d(TAG, "Retrieved " + (records != null ? records.size() : 0) + " archived records");
            return records != null ? records : new ArrayList<>();
        } catch (Exception e) {
            Log.e(TAG, "Error getting archived records: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean unarchiveRecord(int recordId) {
        if (recordId <= 0) {
            Log.e(TAG, "unarchiveRecord: invalid recordId");
            return false;
        }

        try {
            boolean success = dao.updateArchiveStatus(recordId, 0); // 0 = not archived
            Log.d(TAG, "Unarchive record " + recordId + ": " + success);
            return success;
        } catch (Exception e) {
            Log.e(TAG, "Error unarchiving record: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteRecordPermanently(int recordId) {
        if (recordId <= 0) {
            Log.e(TAG, "deleteRecordPermanently: invalid recordId");
            return false;
        }

        try {
            boolean success = dao.deleteRecord(recordId);
            Log.d(TAG, "Delete record " + recordId + ": " + success);
            return success;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting record: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public int batchUnarchiveRecords(List<Integer> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            Log.e(TAG, "batchUnarchiveRecords: empty recordIds list");
            return 0;
        }

        try {
            int successCount = dao.batchUpdateArchiveStatus(recordIds, 0);
            Log.d(TAG, "Batch unarchived " + successCount + " of " + recordIds.size());
            return successCount;
        } catch (Exception e) {
            Log.e(TAG, "Error batch unarchiving: " + e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public int batchDeleteRecordsPermanently(List<Integer> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            Log.e(TAG, "batchDeleteRecordsPermanently: empty recordIds list");
            return 0;
        }

        try {
            int successCount = dao.batchDeleteRecords(recordIds);
            Log.d(TAG, "Batch deleted " + successCount + " of " + recordIds.size());
            return successCount;
        } catch (Exception e) {
            Log.e(TAG, "Error batch deleting: " + e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public int getArchivedCount(String employeeId) {
        List<MonitoringRecord> records = getArchivedRecords(employeeId);
        return records != null ? records.size() : 0;
    }

    @Override
    public int getRecentlyArchivedCount(String employeeId) {
        List<MonitoringRecord> records = getArchivedRecords(employeeId);
        if (records == null || records.isEmpty()) return 0;

        int recentlyCount = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        long thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);

        for (MonitoringRecord record : records) {
            try {
                Date recordDate = sdf.parse(record.getDateOfInspection());
                if (recordDate != null && recordDate.getTime() >= thirtyDaysAgo) {
                    recentlyCount++;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing date: " + e.getMessage());
            }
        }
        return recentlyCount;
    }

    @Override
    public List<MonitoringRecord> searchArchivedRecords(String employeeId, String query) {
        List<MonitoringRecord> allRecords = getArchivedRecords(employeeId);
        if (allRecords == null || allRecords.isEmpty()) {
            return new ArrayList<>();
        }

        if (query == null || query.trim().isEmpty()) {
            return allRecords;
        }

        String lowerQuery = query.toLowerCase().trim();
        List<MonitoringRecord> filtered = new ArrayList<>();

        for (MonitoringRecord record : allRecords) {
            if (matchesSearch(record, lowerQuery)) {
                filtered.add(record);
            }
        }

        Log.d(TAG, "Search found " + filtered.size() + " records for query: " + query);
        return filtered;
    }

    @Override
    public boolean isArchiveEmpty(String employeeId) {
        List<MonitoringRecord> records = getArchivedRecords(employeeId);
        return records == null || records.isEmpty();
    }

    @Override
    public String getEmptyStateMessage(String searchQuery) {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            return "No matching records found for \"" + searchQuery + "\"";
        } else {
            return "Archive is Empty\nRecords you archive will appear here";
        }
    }

    @Override
    public int getEmptyStateIcon() {
        return R.drawable.archive4;
    }

    private boolean matchesSearch(MonitoringRecord record, String query) {
        if (record.getNameOfEstablishment() != null &&
                record.getNameOfEstablishment().toLowerCase().contains(query)) {
            return true;
        }
        if (record.getEmbId() != null &&
                record.getEmbId().toLowerCase().contains(query)) {
            return true;
        }
        if (record.getDateOfInspection() != null &&
                record.getDateOfInspection().toLowerCase().contains(query)) {
            return true;
        }
        return false;
    }


    public void getArchivedRecordsAsync(String employeeId, ArchiveCallback<List<MonitoringRecord>> callback) {
        executorService.execute(() -> {
            List<MonitoringRecord> result = getArchivedRecords(employeeId);
            if (callback != null) {
                callback.onResult(result);
            }
        });
    }

    public interface ArchiveCallback<T> {
        void onResult(T result);
    }
}