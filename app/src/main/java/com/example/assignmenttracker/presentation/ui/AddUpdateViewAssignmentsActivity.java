package com.example.assignmenttracker.presentation.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

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
import com.example.assignmenttracker.models.StudentModel;
import com.example.assignmenttracker.utils.PathUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AddUpdateViewAssignmentsActivity extends AppCompatActivity {

    ActivityAddUpdateViewAssignmentsBinding viewAssignmentsBinding;
    Context context;
    RoomDB database;

    public static final int ACTION_VIEW=0;
    public static final int ACTION_ADD=1;
    public static final int ACTION_UPDATE=2;
    private int currentAction=0;
    private int assignmentId=-1;
    private int studentId=-1;
    AssignmentModel assignment;
    ArrayList<Integer> studentIdsList= new ArrayList<>();
    String selectedStudentName="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentAction=getIntent().getIntExtra("action",0);
        assignmentId=getIntent().getIntExtra("assignmentId",-1);

        if(currentAction==ACTION_ADD || currentAction==ACTION_UPDATE) {
            viewAssignmentsBinding = ActivityAddUpdateViewAssignmentsBinding.inflate(getLayoutInflater());
            setContentView(viewAssignmentsBinding.getRoot());
            init();
            setDatePickUpListerners();
        }


        List<StudentModel> students= database.studentDAO().getAllStudents();
        ArrayList<StudentModel> studentList=new ArrayList<>();
        studentList.addAll(students);
        setupStudentAutoCompleteTextview(studentList);

        setUpListeners();

        if(currentAction==ACTION_UPDATE) {
            setupAssignmentForUpdate();
        } else if (currentAction==ACTION_ADD) {
            setUpAddAssignmentView();
        }
    }

    ArrayList<String> studentAdapterList;
    private void setupStudentAutoCompleteTextview(ArrayList<StudentModel> studentModelArrayList){

        studentAdapterList = new ArrayList<>();
        for(StudentModel student: studentModelArrayList){
            studentIdsList.add(student.getsId());
            studentAdapterList.add(student.getsName());
        }

        // Create an ArrayAdapter using the string array and a default dropdown layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,studentAdapterList);

        // Set the adapter to the AutoCompleteTextView
        viewAssignmentsBinding.studentAutoCompleteTV.setAdapter(adapter);

        // Set threshold (number of characters to start showing suggestions)
        viewAssignmentsBinding.studentAutoCompleteTV.setThreshold(1); // Start showing suggestions after 1 character

        // Set item click listener for when an item is selected
        viewAssignmentsBinding.studentAutoCompleteTV.setOnItemClickListener((parent, view, position, id) -> {
            studentId= studentIdsList.get(position);
            selectedStudentName = (String) parent.getItemAtPosition(position);
        });
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
        database= RoomDB.getInstance(getApplicationContext(),false);

        if(assignmentId!=-1)
            assignment=database.assignmentDAO().getAssignmentById(assignmentId);
    }


    private void setupAssignmentForUpdate(){
        viewAssignmentsBinding.headerText.setText("Update Assignment");
        viewAssignmentsBinding.btnAddAssignment.setText("Update Assignment");

        int position= studentAdapterList.indexOf(assignment.getStudentName());
        Log.d("TAG", "setupAssignmentForUpdate: position: "+ position);
        if(position>=0 && position<studentAdapterList.size()) {
            Log.d("TAG", "setupAssignmentForUpdate: position: "+ position);
            viewAssignmentsBinding.studentAutoCompleteTV.setText((CharSequence) viewAssignmentsBinding.studentAutoCompleteTV.getAdapter().getItem(position),false);
            studentId = studentIdsList.get(position);
        }

        viewAssignmentsBinding.etSubject.setText(assignment.getSubject());
        viewAssignmentsBinding.etSemester.setText(assignment.getSemester());
        viewAssignmentsBinding.etProject.setText(assignment.getProject());
        viewAssignmentsBinding.etOutDate.setText(assignment.getOutDate());
        viewAssignmentsBinding.etAutoFillInDate.setText(assignment.getInDate());
        viewAssignmentsBinding.etAdvance.setText(String.valueOf(assignment.getAdvancePayment()));
        viewAssignmentsBinding.etPrice.setText(String.valueOf(assignment.getPrice()));
        viewAssignmentsBinding.etFinalPayment.setText(String.valueOf(assignment.getFinalPayment()));

        viewAssignmentsBinding.tvSelectedInputDoc.setText(assignment.getInputDoc());
        viewAssignmentsBinding.tvAdvanceSs.setText(assignment.getAdvancePaymentScreenshot());
        viewAssignmentsBinding.tvFinalPaymentSs.setText(assignment.getFinalPaymentScreenShot());

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

    private void setUpListeners(){
        viewAssignmentsBinding.etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty() && !viewAssignmentsBinding.etAdvance.getText().toString().isEmpty()){
                    viewAssignmentsBinding.etFinalPayment.setText(String.valueOf(Integer.parseInt(s.toString()) - Integer.parseInt(viewAssignmentsBinding.etAdvance.getText().toString())));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        viewAssignmentsBinding.etAdvance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty() && !viewAssignmentsBinding.etPrice.getText().toString().isEmpty()){
                    viewAssignmentsBinding.etFinalPayment.setText(String.valueOf(Integer.parseInt(viewAssignmentsBinding.etPrice.getText().toString()) - Integer.parseInt(s.toString())));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        viewAssignmentsBinding.btnSelectInputDoc.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), 100);
        });

        viewAssignmentsBinding.btnSelectAdvanceSs.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.putExtra(Intent.EXTRA_MIME_TYPES, Arrays.asList("image/png", "image/jpeg").toArray());
            intent.setType("*/*");
            startActivityForResult(intent, 200);
        });

        viewAssignmentsBinding.btnSelectFinalPaymentSs.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.putExtra(Intent.EXTRA_MIME_TYPES, Arrays.asList("image/png", "image/jpeg").toArray());
            intent.setType("*/*");
            startActivityForResult(intent, 300);
        });


    }

    private void setUpAddAssignmentView(){

        viewAssignmentsBinding.btnAddAssignment.setOnClickListener(v->{

                String semesterField= viewAssignmentsBinding.etSemester.getText().toString();
                String subjectField= viewAssignmentsBinding.etSubject.getText().toString();
                String projectField= viewAssignmentsBinding.etProject.getText().toString();
                String setInDateField= viewAssignmentsBinding.etAutoFillInDate.getText().toString();
                String setOutDateField= viewAssignmentsBinding.etOutDate.getText().toString();
                String priceField= viewAssignmentsBinding.etPrice.getText().toString();
                String setAdvancePaymentField= viewAssignmentsBinding.etAdvance.getText().toString();
                String setFinalPaymentField= viewAssignmentsBinding.etFinalPayment.getText().toString();
                String setInputDocField= viewAssignmentsBinding.tvSelectedInputDoc.getText().toString();
                String setAdvancePaymentSSField= viewAssignmentsBinding.tvAdvanceSs.getText().toString();
                String setFinalPaymentSSField= viewAssignmentsBinding.tvFinalPaymentSs.getText().toString();

                if(studentId==-1 || selectedStudentName.isEmpty() || semesterField.isEmpty() || subjectField.isEmpty()
                    || projectField.isEmpty() || setInDateField.isEmpty() || setOutDateField.isEmpty() || priceField.isEmpty()
                    || setAdvancePaymentField.isEmpty() || setFinalPaymentField.isEmpty() || setInputDocField.isEmpty()
                    || setAdvancePaymentSSField.isEmpty() || setFinalPaymentSSField.isEmpty() || Integer.parseInt(setFinalPaymentField) <= 0){
                    Toast.makeText(AddUpdateViewAssignmentsActivity.this, "Fill all details to add Assignment", Toast.LENGTH_SHORT).show();
                }
                else {
                    AssignmentModel assignmentModel = new AssignmentModel();

                    assignmentModel.setsId(studentId);
                    assignmentModel.setStudentName(selectedStudentName);
                    assignmentModel.setSemester(semesterField);
                    assignmentModel.setSubject(subjectField);
                    assignmentModel.setProject(projectField);
                    assignmentModel.setInDate(setInDateField);
                    assignmentModel.setOutDate(setOutDateField);
                    assignmentModel.setPrice(Integer.parseInt(priceField));
                    assignmentModel.setAdvancePayment(Integer.parseInt(setAdvancePaymentField));
                    assignmentModel.setFinalPayment(Integer.parseInt(setFinalPaymentField));


                    assignmentModel.setInputDoc(setInputDocField);
                    assignmentModel.setFinalPaymentScreenShot(setFinalPaymentSSField);
                    assignmentModel.setAdvancePaymentScreenshot(setAdvancePaymentSSField);

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
            String fileName = null; //getFileName(selectedUri);
            try {
                fileName = PathUtils.getPath(AddUpdateViewAssignmentsActivity.this,selectedUri);
                Toast.makeText(AddUpdateViewAssignmentsActivity.this,"file path: "+ fileName,Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(AddUpdateViewAssignmentsActivity.this,"error getting image path",Toast.LENGTH_SHORT).show();
            }
            viewAssignmentsBinding.tvSelectedInputDoc.setText(fileName);
        }
        else if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            selectedUri = data.getData();
            String fileName = null; //getFileName(selectedUri);
            try {
                fileName = PathUtils.getPath(AddUpdateViewAssignmentsActivity.this,selectedUri);
                Toast.makeText(AddUpdateViewAssignmentsActivity.this,"file path: "+ fileName,Toast.LENGTH_SHORT).show();
            } catch (URISyntaxException e) {
                Toast.makeText(AddUpdateViewAssignmentsActivity.this,"error getting image path",Toast.LENGTH_SHORT).show();
            }
            viewAssignmentsBinding.tvAdvanceSs.setText(fileName);
        }
        else if (requestCode == 300 && resultCode == RESULT_OK && data != null) {
            selectedUri = data.getData();
            String fileName = null; //getFileName(selectedUri);
            try {
                fileName = PathUtils.getPath(AddUpdateViewAssignmentsActivity.this,selectedUri);
                Toast.makeText(AddUpdateViewAssignmentsActivity.this,"file2 path: "+ fileName,Toast.LENGTH_SHORT).show();
            } catch (URISyntaxException e) {
                Toast.makeText(AddUpdateViewAssignmentsActivity.this,"error2 getting image path",Toast.LENGTH_SHORT).show();
            }
            viewAssignmentsBinding.tvFinalPaymentSs.setText(fileName);
        }

    }
}