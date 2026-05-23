package com.example.embr6monitoringapp.Service;

import android.content.Context;

import com.example.embr6monitoringapp.DAO.MonitoringProgressDao;
import com.example.embr6monitoringapp.DAO.MonitoringProgressDaoImpl;
import com.example.embr6monitoringapp.Models.MonitoringRecord;

import java.util.List;

public class MonitoringProgressServiceImpl implements MonitoringProgressService {

    private final MonitoringProgressDao dao;

    public MonitoringProgressServiceImpl(Context context) {
        dao = new MonitoringProgressDaoImpl(context);
    }

    @Override
    public long saveRecord(MonitoringRecord record) {
        return dao.insertRecord(record);
    }

    @Override
    public boolean savePurposeSection(int recordId, String purposeStatus,
                                      String findings, String evidenceImageUri) {
        boolean updated = dao.updatePurposeSection(
                recordId, purposeStatus, findings, evidenceImageUri);
        if (updated) dao.markComplete(recordId);
        return updated;
    }

    @Override
    public boolean updateFullRecord(MonitoringRecord record) {
        return dao.updateFullRecord(record);
    }

    @Override
    public List<MonitoringRecord> getRecordsForEmployee(String employeeId) {
        return dao.getRecordsByEmployee(employeeId);
    }

    @Override
    public MonitoringRecord getRecordById(int recordId) {
        return dao.getRecordById(recordId);
    }


    @Override
    public List<MonitoringRecord> getCompletedRecordsByEmployee(String employeeId) {
        return dao.getCompletedRecordsByEmployee(employeeId);
    }
}