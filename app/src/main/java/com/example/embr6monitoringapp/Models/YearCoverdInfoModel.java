package com.example.embr6monitoringapp.Models;

public class YearCoverdInfoModel {

    private int    id;
    private String employeeId;
    private String yearCovered;
    private String volCuM;
    private String total;

    public int getId()                    { return id; }
    public void setId(int id)             { this.id = id; }

    public String getEmployeeId()                      { return employeeId; }
    public void setEmployeeId(String employeeId)       { this.employeeId = employeeId; }

    public String getYearCovered()                     { return yearCovered; }
    public void setYearCovered(String yearCovered)     { this.yearCovered = yearCovered; }

    public String getVolCuM()                          { return volCuM; }
    public void setVolCuM(String volCuM)               { this.volCuM = volCuM; }

    public String getTotal()                           { return total; }
    public void setTotal(String total)                 { this.total = total; }
}