package com.example.embr6monitoringapp.DAO;

import com.example.embr6monitoringapp.Models.PurposeModel;
import java.util.List;

public interface PurposeDAO {
    long insertPurpose(PurposeModel model);
    List<PurposeModel> getUnsyncedPurpose();
    int updateSyncStatus(int id);
    PurposeModel getPurposeByEmployeeId(String employeeId);
}