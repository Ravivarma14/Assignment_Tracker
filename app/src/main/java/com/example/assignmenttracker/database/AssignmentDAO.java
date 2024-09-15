package com.example.assignmenttracker.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.assignmenttracker.models.AssignmentModel;
import com.example.assignmenttracker.models.StudentModel;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface AssignmentDAO {

    @Insert(onConflict = REPLACE)
    void insertAssignment(AssignmentModel assignment);

    @Query("UPDATE assignments SET s_id= :sId, semester=:semester, subject=:subject, project=:project, in_date=:inDate, out_date=:outDate, submission_doc=:submissionDoc, input_doc=:inputDoc, price=:price, advance_payment=:advancePayment, advance_payment_screenshot=:advancePaymentScreenshot, final_payment=:finalPayment, final_payment_screenshot=:finaPaymentScreenshot WHERE assignment_id=:assignmentId")
    void updateAssignment(int assignmentId, int sId, String semester, String subject, String project, String inDate,String outDate, String submissionDoc, String inputDoc, Integer price, Integer advancePayment, String advancePaymentScreenshot, Integer finalPayment, String finaPaymentScreenshot);

    @Query("SELECT * FROM assignments")
    List<AssignmentModel> getAllAssignments();

    @Query("SELECT * FROM assignments WHERE s_id=:sId")
    List<AssignmentModel> getAssignmentsForStudent( int sId);

    @Query("SELECT * FROM assignments WHERE assignment_id=:assignmentId")
    AssignmentModel getAssignmentById(int assignmentId);

    @Query("DELETE FROM assignments WHERE assignment_id=:assignmentId")
    void deleteAssignment(int assignmentId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllAssignments(List<AssignmentModel> entities);

}
