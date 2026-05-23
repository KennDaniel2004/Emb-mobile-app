package com.example.embr6monitoringapp.Utils;


public class SessionManager {

    private static SessionManager instance;

    private String employeeId;
    private String fullName;
    private String position;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public String getEmployeeId()                { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getFullName()              { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPosition()                { return position; }
    public void setPosition(String position)   { this.position = position; }

    public void clear() {
        employeeId = null;
        fullName   = null;
        position   = null;
    }
}