package com.example.embr6monitoringapp.Models;

public class UserModel {

    private int    id;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String position;
    private String username;
    private String password;


    public int getId()              { return id; }
    public void setId(int id)       { this.id = id; }

    public String getEmployeeId()                   { return employeeId; }
    public void setEmployeeId(String employeeId)    { this.employeeId = employeeId; }

    public String getFirstName()                    { return firstName; }
    public void setFirstName(String firstName)      { this.firstName = firstName; }

    public String getLastName()                     { return lastName; }
    public void setLastName(String lastName)        { this.lastName = lastName; }

    public String getMiddleName()                   { return middleName; }
    public void setMiddleName(String middleName)    { this.middleName = middleName; }

    public String getPosition()                     { return position; }
    public void setPosition(String position)        { this.position = position; }

    public String getUsername()                     { return username; }
    public void setUsername(String username)        { this.username = username; }

    public String getPassword()                     { return password; }
    public void setPassword(String password)        { this.password = password; }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (lastName  != null && !lastName.isEmpty())  sb.append(lastName).append(", ");
        if (firstName != null && !firstName.isEmpty()) sb.append(firstName);
        if (middleName != null && !middleName.isEmpty()) {
            sb.append(" ").append(middleName.charAt(0)).append(".");
        }
        return sb.toString().trim();
    }
}