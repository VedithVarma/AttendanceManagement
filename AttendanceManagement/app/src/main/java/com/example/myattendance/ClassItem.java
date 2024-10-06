package com.example.myattendance;

public class ClassItem {
    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    private long cid;
   private  String className;
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    private String subjectName;

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }


    public ClassItem(String className, String subjectName){
        this.className = className;
        this.subjectName = subjectName;

    }

    public ClassItem(long cid,String className, String subjectName){
        this.cid=cid;
        this.className = className;
        this.subjectName = subjectName;

    }

}
