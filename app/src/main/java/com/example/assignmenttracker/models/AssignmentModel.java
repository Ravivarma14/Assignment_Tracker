package com.example.assignmenttracker.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "assignments")
public class AssignmentModel {

    @ColumnInfo(name = "assignment_id")
    @PrimaryKey(autoGenerate = true)
    public int assignmentId=0;
    @ColumnInfo(name = "s_id")
    public int sId=0;
    @ColumnInfo(name = "semester")
    public String semester;
    @ColumnInfo(name = "subject")
    public String subject;
    @ColumnInfo(name = "project")
    public String project;
    @ColumnInfo(name = "in_date")
    public String inDate;
    @ColumnInfo(name = "submission_doc")
    public String submissionDoc;
    @ColumnInfo(name = "input_doc")
    public String inputDoc;
    @ColumnInfo(name = "out_date")
    public String outDate;
    @ColumnInfo(name = "price")
    public Integer price;
    @ColumnInfo(name = "advance_payment")
    public Integer advancePayment;
    @ColumnInfo(name = "advance_payment_screenshot")
    public String advancePaymentScreenshot;
    @ColumnInfo(name = "final_payment")
    public Integer finalPayment;
    @ColumnInfo(name = "final_payment_screenshot")
    public String finalPaymentScreenShot;

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public int getsId() {
        return sId;
    }

    public void setsId(int sId) {
        this.sId = sId;
    }
    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getInDate() {
        return inDate;
    }

    public void setInDate(String inDate) {
        this.inDate = inDate;
    }

    public String getSubmissionDoc() {
        return submissionDoc;
    }

    public void setSubmissionDoc(String submissionDoc) {
        this.submissionDoc = submissionDoc;
    }

    public String getInputDoc() {
        return inputDoc;
    }

    public void setInputDoc(String inputDoc) {
        this.inputDoc = inputDoc;
    }

    public String getOutDate() {
        return outDate;
    }

    public void setOutDate(String outDate) {
        this.outDate = outDate;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getAdvancePayment() {
        return advancePayment;
    }

    public void setAdvancePayment(Integer advancePayment) {
        this.advancePayment = advancePayment;
    }

    public String getAdvancePaymentScreenshot() {
        return advancePaymentScreenshot;
    }

    public void setAdvancePaymentScreenshot(String advancePaymentScreenshot) {
        this.advancePaymentScreenshot = advancePaymentScreenshot;
    }

    public Integer getFinalPayment() {
        return finalPayment;
    }

    public void setFinalPayment(Integer finalPayment) {
        this.finalPayment = finalPayment;
    }

    public String getFinalPaymentScreenShot() {
        return finalPaymentScreenShot;
    }

    public void setFinalPaymentScreenShot(String finalPaymentScreenShot) {
        this.finalPaymentScreenShot = finalPaymentScreenShot;
    }
}
