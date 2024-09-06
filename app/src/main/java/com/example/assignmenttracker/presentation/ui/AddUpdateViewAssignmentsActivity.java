package com.example.assignmenttracker.presentation.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.assignmenttracker.R;
import com.example.assignmenttracker.database.RoomDB;
import com.example.assignmenttracker.databinding.ActivityAddUpdateViewAssignmentsBinding;
import com.example.assignmenttracker.databinding.FragmentAddAssignmentBinding;
import com.example.assignmenttracker.databinding.FragmentAddStudentBinding;
import com.example.assignmenttracker.models.AssignmentModel;

import java.io.File;
import java.util.Calendar;

public class AddUpdateViewAssignmentsActivity extends AppCompatActivity {

    ActivityAddUpdateViewAssignmentsBinding viewAssignmentsBinding;
    //FragmentAddAssignmentBinding fragmentAddAssignmentBinding;
    Context context;
    RoomDB database;

    public static final int ACTION_VIEW=0;
    public static final int ACTION_ADD=1;
    public static final int ACTION_UPDATE=2;
    private int currentAction=0;
    private int assignmentId=-1;
    private int studentId=-1;
    AssignmentModel assignment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentAction=getIntent().getIntExtra("action",0);
        assignmentId=getIntent().getIntExtra("assignmentId",-1);
        studentId=getIntent().getIntExtra("sId",-1);

        if(currentAction==ACTION_VIEW) {
//            viewAssignmentsBinding = ActivityAddUpdateViewAssignmentsBinding.inflate(getLayoutInflater());
//            setContentView(viewAssignmentsBinding.getRoot());
        }
        else if(currentAction==ACTION_ADD || currentAction==ACTION_UPDATE) {
            viewAssignmentsBinding = ActivityAddUpdateViewAssignmentsBinding.inflate(getLayoutInflater());
            setContentView(viewAssignmentsBinding.getRoot());
            init();
            setDatePickUpListerners();
        }


        if(currentAction==ACTION_UPDATE) {
            setupAssignmentForUpdate();
        } else if (currentAction==ACTION_ADD) {
            setUpAddAssignmentView();
        }
    }

    private void setDatePickUpListerners(){
        Calendar calendar = Calendar.getInstance();

        // Get the current date, month, and year
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String currentDate=day + "-" + month+ "-" + year;

        viewAssignmentsBinding.etAutoFillInDate.setText(currentDate);

        viewAssignmentsBinding.ivPickInDate.setOnClickListener(v->{

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year1, month1, dayOfMonth) -> {
                        // Display the selected date in the TextView
                        String selectedDate = dayOfMonth + "-" + (month1 + 1) + "-" + year1;
                        viewAssignmentsBinding.etAutoFillInDate.setText(selectedDate);
                    }, year, month, day);

            datePickerDialog.show();
        });

        viewAssignmentsBinding.ivPickOutDate.setOnClickListener(v->{

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year1, month1, dayOfMonth) -> {
                        // Display the selected date in the TextView
                        String selectedDate = dayOfMonth + "-" + (month1 + 1) + "-" + year1;
                        viewAssignmentsBinding.etOutDate.setText(selectedDate);
                    }, year, month, day);

            datePickerDialog.show();
        });
    }

    private void init(){
        context= AddUpdateViewAssignmentsActivity.this;
        database= RoomDB.getInstance(context);

        if(assignmentId!=-1)
            assignment=database.assignmentDAO().getAssignmentById(assignmentId);
    }

    private void setUpAssignmentForView(){
        //binding.tvProjectName.setText(assignment.getProject());
    }

    private void setupAssignmentForUpdate(){
        viewAssignmentsBinding.btnAddAssignment.setText("Update Assignment");

        viewAssignmentsBinding.etSubject.setText(assignment.getSubject());
        viewAssignmentsBinding.etSemester.setText(assignment.getSemester());
        viewAssignmentsBinding.etProject.setText(assignment.getProject());
        viewAssignmentsBinding.etOutDate.setText(assignment.getOutDate());
        viewAssignmentsBinding.etAutoFillInDate.setText(assignment.getInDate());
        viewAssignmentsBinding.etAdvance.setText(String.valueOf(assignment.getAdvancePayment()));
        viewAssignmentsBinding.etPrice.setText(String.valueOf(assignment.getPrice()));
        viewAssignmentsBinding.etFinalPayment.setText(String.valueOf(assignment.getFinalPayment()));

        viewAssignmentsBinding.btnAddAssignment.setOnClickListener(v->{

            assignment.setSemester(viewAssignmentsBinding.etSemester.getText().toString());
            assignment.setSubject(viewAssignmentsBinding.etSubject.getText().toString());
            assignment.setProject(viewAssignmentsBinding.etProject.getText().toString());
            assignment.setInDate(viewAssignmentsBinding.etAutoFillInDate.getText().toString());
            assignment.setOutDate(viewAssignmentsBinding.etOutDate.getText().toString());
            assignment.setPrice(Integer.parseInt(viewAssignmentsBinding.etPrice.getText().toString()));
            assignment.setAdvancePayment(Integer.parseInt(viewAssignmentsBinding.etAdvance.getText().toString()));
            assignment.setFinalPayment(Integer.parseInt(viewAssignmentsBinding.etFinalPayment.getText().toString()));


            assignment.setInputDoc(viewAssignmentsBinding.tvSelectedInputDoc.getText().toString());
            assignment.setFinalPaymentScreenShot(viewAssignmentsBinding.tvFinalPaymentSs.getText().toString());
            assignment.setAdvancePaymentScreenshot(viewAssignmentsBinding.tvAdvanceSs.getText().toString());

            database.assignmentDAO().updateAssignment(assignment.getAssignmentId(),assignment.getsId(),assignment.getSemester(),assignment.getSubject(),assignment.getProject(),assignment.getInDate(),assignment.getOutDate(),assignment.getSubmissionDoc(),assignment.getInputDoc(),assignment.getPrice(),assignment.getAdvancePayment(),assignment.getAdvancePaymentScreenshot(),assignment.getFinalPayment(),assignment.getFinalPaymentScreenShot());

            finish();
        });
    }


    private void setUpAddAssignmentView(){
        viewAssignmentsBinding.btnSelectInputDoc.setOnClickListener(v->{
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(Intent.createChooser(intent, "Select PDF"), 100);
        });

        viewAssignmentsBinding.btnSelectAdvanceSs.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 200);
        });

        viewAssignmentsBinding.btnSelectFinalPaymentSs.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 300);
        });

        viewAssignmentsBinding.btnAddAssignment.setOnClickListener(v->{
            if(studentId!=-1) {
                AssignmentModel assignmentModel = new AssignmentModel();

                assignmentModel.setsId(studentId);
                assignmentModel.setSemester(viewAssignmentsBinding.etSemester.getText().toString());
                assignmentModel.setSubject(viewAssignmentsBinding.etSubject.getText().toString());
                assignmentModel.setProject(viewAssignmentsBinding.etProject.getText().toString());
                assignmentModel.setInDate(viewAssignmentsBinding.etAutoFillInDate.getText().toString());
                assignmentModel.setOutDate(viewAssignmentsBinding.etOutDate.getText().toString());
                assignmentModel.setPrice(Integer.parseInt(viewAssignmentsBinding.etPrice.getText().toString()));
                assignmentModel.setAdvancePayment(Integer.parseInt(viewAssignmentsBinding.etAdvance.getText().toString()));
                assignmentModel.setFinalPayment(Integer.parseInt(viewAssignmentsBinding.etFinalPayment.getText().toString()));


               assignmentModel.setInputDoc(viewAssignmentsBinding.tvSelectedInputDoc.getText().toString());
               assignmentModel.setFinalPaymentScreenShot(viewAssignmentsBinding.tvFinalPaymentSs.getText().toString());
               assignmentModel.setAdvancePaymentScreenshot(viewAssignmentsBinding.tvAdvanceSs.getText().toString());

               database.assignmentDAO().insertAssignment(assignmentModel);

               finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedUri=null;

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedUri = data.getData();
            String fileName = getFileName(selectedUri);
            viewAssignmentsBinding.tvSelectedInputDoc.setText(fileName);
        }
        else if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            selectedUri = data.getData();
            String fileName = getFileName(selectedUri);
            viewAssignmentsBinding.tvAdvanceSs.setText(fileName);
        }
        else if (requestCode == 300 && resultCode == RESULT_OK && data != null) {
            selectedUri = data.getData();
            String fileName = getFileName(selectedUri);
            viewAssignmentsBinding.tvFinalPaymentSs.setText(fileName);
        }

    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    result = cursor.getString(nameIndex);
                }
            }
        } else if (uri.getScheme().equals("file")) {
            result = new File(uri.getPath()).getAbsolutePath();
        }
        return result;
    }

}