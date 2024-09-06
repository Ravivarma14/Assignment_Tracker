package com.example.assignmenttracker.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "students")
public class StudentModel implements Serializable {
    @ColumnInfo(name = "s_id")
    @PrimaryKey(autoGenerate = true)
    public int sId=0;
    @ColumnInfo(name = "student_name")
    public String sName;
    @ColumnInfo(name = "university_name")
    public String sUniversityName;
    @ColumnInfo(name = "mobile_no")
    public String sMobileNumber;
    @ColumnInfo(name = "refer_by")
    public String sReferBy;

    public int getsId() {
        return sId;
    }

    public void setsId(int sId) {
        this.sId = sId;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsUniversityName() {
        return sUniversityName;
    }

    public void setsUniversityName(String sUniversityName) {
        this.sUniversityName = sUniversityName;
    }

    public String getsMobileNumber() {
        return sMobileNumber;
    }

    public void setsMobileNumber(String sMobileNumber) {
        this.sMobileNumber = sMobileNumber;
    }

    public String getsReferBy() {
        return sReferBy;
    }

    public void setsReferBy(String sReferBy) {
        this.sReferBy = sReferBy;
    }

}
