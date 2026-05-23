package com.example.embr6monitoringapp.Models;

public class ReportInfoModel {

    private int id;
    private String employeeId;
    private String embId;
    private String reportControl;
    private String typeMonitoring;
    private String dateOfInspection;



    public ReportInfoModel() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getEmbId() { return embId; }
    public void setEmbId(String embId) { this.embId = embId; }

    public String getReportControl() { return reportControl; }
    public void setReportControl(String reportControl) { this.reportControl = reportControl; }

    public String getTypeMonitoring() { return typeMonitoring; }
    public void setTypeMonitoring(String typeMonitoring) { this.typeMonitoring = typeMonitoring; }

    public String getDateOfInspection() { return dateOfInspection; }
    public void setDateOfInspection(String dateOfInspection) { this.dateOfInspection = dateOfInspection; }
}