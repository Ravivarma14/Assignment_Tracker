package com.example.assignmenttracker.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

import com.example.assignmenttracker.models.StudentModel;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface StudentDAO {
    //DAO- Data Access Object
    @Insert(onConflict = REPLACE)
    void insertStudent(StudentModel student);

    @Query("UPDATE students SET student_name= :name, university_name=:universityName, mobile_no=:mobileNo, refer_by=:referBy WHERE s_id=:sId")
    void updateStudent(int sId, String name, String universityName, String mobileNo, String referBy);

    @Query("SELECT * FROM students")
    List<StudentModel> getAllStudents();

    @Query("SELECT * FROM students WHERE s_id=:sId")
    StudentModel getStudentById(int sId);

    @Query("DELETE FROM students WHERE s_id=:sId")
    void deleteStudent(int sId);
}
