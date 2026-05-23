package com.example.embr6monitoringapp.DAO;

import com.example.embr6monitoringapp.Models.ComplianceStatusModel;

import java.util.List;

public interface ComplianceStatusDao {

    boolean insert(ComplianceStatusModel model);

    List<ComplianceStatusModel> getByEmployee(String employeeId);
}