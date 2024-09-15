package com.example.assignmenttracker.presenentation.new_ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmenttracker.Adapters.StudentRecyclerViewAdapter;
import com.example.assignmenttracker.R;
import com.example.assignmenttracker.database.RoomDB;
import com.example.assignmenttracker.databinding.ActivityStudentsListBinding;
import com.example.assignmenttracker.models.AssignmentModel;
import com.example.assignmenttracker.models.StudentModel;
import com.example.assignmenttracker.presentation.ui.AddUpdateViewAssignmentsActivity;
import com.example.assignmenttracker.presentation.ui.MyBottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class StudentsListActivity extends AppCompatActivity {

    ActivityStudentsListBinding binding;
    private Context context;
    public static RoomDB database;
    List<StudentModel> studentsList;
    MyBottomSheetDialogFragment bottomSheet;
    StudentRecyclerViewAdapter stundentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityStudentsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupActionBar();

        setSearchView();
    }

    private void setSearchView(){
        binding.searchView.setIconifiedByDefault(false); // Ensures the search view is expanded
        //binding.searchView.setFocusable(true);           // Focus on the search view
        binding.searchView.setIconified(false);          // Ensures the view stays open

        // Optionally, set a query text listener to handle searches
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle the search query submission
                // Perform the search operation here


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle text change in search query

                if(!newText.isEmpty()) {
                    String searchText = newText.toLowerCase();

                    ArrayList<StudentModel> filteredList = new ArrayList<>();

                    //filter by project name, student name, subject, university, semester, pn no
                    for (StudentModel student : studentsList) {

                        //StudentModel student = database.studentDAO().getStudentById(assigment.getsId());
                        if (student.getsMobileNumber().toLowerCase().contains(searchText) || student.getsUniversityName().toLowerCase().contains(searchText)
                            || student.getsName().toLowerCase().contains(searchText) || student.getsReferBy().toLowerCase().contains(searchText)) {
                            filteredList.add(student);
                        }
                    }
                    refreshStudentsList(filteredList);

                } else{
                    refreshStudentsList((ArrayList<StudentModel>) studentsList);
                }
                return false;
            }
        });
    }



    private void setupActionBar(){
        // Set up the custom action bar
        //Toolbar toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(binding.customToolbar);

        // Customize the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);  // Hide the default title
            //actionBar.setTitle("Students");
            actionBar.setDisplayShowCustomEnabled(true);  // Enable custom view
            actionBar.setCustomView(R.layout.action_bar_layout);  // Set custom layout
        }

    }



    private  void setupRecyclerViewAdapter(){
        ArrayList<StudentModel> students= new ArrayList<>();
        students.addAll(studentsList);
        stundentAdapter = new StudentRecyclerViewAdapter(StudentsListActivity.this,students);
        binding.recyclerviewStudents.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerviewStudents.setAdapter(stundentAdapter);

        setupSwipeListener();
    }

    private void setupSwipeListener() {
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    // Handle left swipe
                    // For example, remove the item

                    //delete confirmation dilaog before

                    AlertDialog.Builder builder = new AlertDialog.Builder(StudentsListActivity.this);

                    // Set the message show for the Alert time
                    builder.setMessage("It will delete All assignments added to "+ studentsList.get(position).getsName()+" \nAre you sure ?");

                    // Set Alert Title
                    builder.setTitle("Delete "+ studentsList.get(position).getsName());

                    // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                    builder.setCancelable(false);

                    // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                    builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                                // When the user click yes button then app will close


                                StudentModel deleteData = studentsList.get(position);


                                //assignmentsList = database.assignmentDAO().getAllAssignments();
                                //Toast.makeText(DetailedAssignmentsActivity.this,"Assignment Deleted",Toast.LENGTH_SHORT).show();

                                List<AssignmentModel> deleteList = database.assignmentDAO().getAssignmentsForStudent(studentsList.get(position).getsId());

                                for (AssignmentModel assignmentModel : deleteList) {
                                    database.assignmentDAO().deleteAssignment(assignmentModel.getAssignmentId());
                                }

                                database.studentDAO().deleteStudent(studentsList.get(position).getsId());
                                studentsList.remove(position);

                        refreshStudentsList((ArrayList<StudentModel>) studentsList);
                        dialog.dismiss();

                            });

                    builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                        // If user click no then dialog box is canceled.
                        refreshStudentsList((ArrayList<StudentModel>) studentsList);
                        dialog.dismiss();
                    });

                    // Create the Alert dialog
                    AlertDialog alertDialog = builder.create();
                    // Show the Alert Dialog box
                    alertDialog.show();

                } else if (direction == ItemTouchHelper.RIGHT) {

                    //student update code
                    showAddStudentBottomDialog(studentsList.get(position).getsId());

                    /*Intent viewAssignment = new Intent(StudentsListActivity.this, AddUpdateViewAssignmentsActivity.class);
                    viewAssignment.putExtra("action", AddUpdateViewAssignmentsActivity.ACTION_UPDATE);
                    viewAssignment.putExtra("sId", studentsList.get(position).getsId());
                    viewAssignment.putExtra("assignmentId", studentsList.get(position).getAssignmentId());
                    startActivity(viewAssignment);*/
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(StudentsListActivity.this, R.color.red))
                        .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                        .addSwipeLeftLabel("Delete")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(StudentsListActivity.this, R.color.white))
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(StudentsListActivity.this, R.color.white))
                        .addSwipeRightActionIcon(R.drawable.baseline_edit_24)
                        .addSwipeRightLabel("Edit")
                        .setSwipeRightLabelColor(ContextCompat.getColor(StudentsListActivity.this, R.color.black))
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerviewStudents);

    }

    private void init(){
        context= StudentsListActivity.this;

        database= RoomDB.getInstance(getApplicationContext(),false);
        studentsList = database.studentDAO().getAllStudents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
        setupRecyclerViewAdapter();

        binding.searchView.clearFocus();

        binding.fabAddStudent.setOnClickListener(v->{
            showAddStudentBottomDialog(-1);
        });
    }

    private void refreshStudentsList(ArrayList<StudentModel> students){
//        studentsList = database.studentDAO().getAllStudents();
//        ArrayList<StudentModel> students= new ArrayList<>();
//        students.addAll(studentsList);

        stundentAdapter.setListOfStudents(students);
        stundentAdapter.notifyDataSetChanged();
        //setupRecyclerViewAdapter();
    }

    private void showAddStudentBottomDialog( int sId){

        bottomSheet = new MyBottomSheetDialogFragment(true,sId);
        bottomSheet.setOnDismissListener(new MyBottomSheetDialogFragment.OnDismissListener() {
            @Override
            public void onDismiss() {
                studentsList = database.studentDAO().getAllStudents();
                refreshStudentsList((ArrayList<StudentModel>) studentsList);
            }
        });
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());

    }


}