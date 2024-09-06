package com.example.assignmenttracker.presentation.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.assignmenttracker.Adapters.StudentRecyclerViewAdapter;
import com.example.assignmenttracker.database.RoomDB;
import com.example.assignmenttracker.databinding.ActivityHomeScreenBinding;
import com.example.assignmenttracker.databinding.FragmentAddStudentBinding;
import com.example.assignmenttracker.databinding.ItemviewStudentBinding;
import com.example.assignmenttracker.models.StudentModel;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenActivity extends AppCompatActivity {

    ActivityHomeScreenBinding binding;
    private Context context;
    public static RoomDB database;
    List<StudentModel> studentsList;
    MyBottomSheetDialogFragment bottomSheet;
    StudentRecyclerViewAdapter stundentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityHomeScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    110);
        }

        else {

            init();
            setupRecyclerViewAdapter();
        }
        binding.fabAddStudent.setOnClickListener(v->{showAddStudentBottomDialog();});
        binding.ivSettings.setOnClickListener(v->{
            Intent intent= new Intent(HomeScreenActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private  void setupRecyclerViewAdapter(){
        ArrayList<StudentModel> students= new ArrayList<>();
        students.addAll(studentsList);
        stundentAdapter = new StudentRecyclerViewAdapter(HomeScreenActivity.this,students);
        binding.recyclerviewStudents.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerviewStudents.setAdapter(stundentAdapter);
    }

    private void init(){
        context= HomeScreenActivity.this;

        database=RoomDB.getInstance(context);
        studentsList = database.studentDAO().getAllStudents();
    }
    private void refreshStudentsList(){
        studentsList = database.studentDAO().getAllStudents();
        ArrayList<StudentModel> students= new ArrayList<>();
        students.addAll(studentsList);
        stundentAdapter.setListOfStudents(students);
        stundentAdapter.notifyDataSetChanged();
        //setupRecyclerViewAdapter();
    }

    private void showAddStudentBottomDialog(){

        bottomSheet = new MyBottomSheetDialogFragment(true,-1);
        bottomSheet.setOnDismissListener(new MyBottomSheetDialogFragment.OnDismissListener() {
            @Override
            public void onDismiss() {
                refreshStudentsList();
            }
        });
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 110) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
                init();
                setupRecyclerViewAdapter();
            } else {
                // Permission denied
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}