package com.example.assignmenttracker;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.assignmenttracker.database.RoomDB;
import com.example.assignmenttracker.databinding.ActivityMainBinding;
import com.example.assignmenttracker.models.StudentModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private Context context;
    RoomDB database;
    List<StudentModel> studentsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(getColor(R.color.red));
    }

    private void init(){
        context= MainActivity.this;

        database=RoomDB.getInstance(context);

        studentsList = database.studentDAO().getAllStudents();
    }

    private void insert(StudentModel student){
        database.studentDAO().insertStudent(student);
        updateStudentsList();
    }

    private void deleteStudent(StudentModel student){
        database.studentDAO().deleteStudent(student.getsId());
        updateStudentsList();
    }
    private void updateStudentsList(){
        studentsList.clear();
        studentsList.addAll(database.studentDAO().getAllStudents());
    }
    private void updateStudent(StudentModel student){
        database.studentDAO().updateStudent(student.getsId(),student.getsName(),student.getsUniversityName(),student.getsMobileNumber(),student.getsReferBy());
        updateStudentsList();
    }


}