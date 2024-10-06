package com.example.myattendance;

public class StudentItem {
    private long sid;
    private String roll;
    private String name;
    private String status;



    public StudentItem(String roll, String name)
    {
        this.roll=roll;
        this.name=name;
        status="";
    }

    public StudentItem(long sid,String roll, String name)
    {
        this.sid=sid;
        this.roll=roll;
        this.name=name;
        status="";
    }


    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }
}
