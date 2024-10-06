package com.example.myattendance;

public class FacultyItem {
    private String name;
    private String password;
    private String email;
    private String department;

    public FacultyItem(String name, String password, String email, String department) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getDepartment() {
        return department;
    }
}
