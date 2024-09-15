package com.example.assignmenttracker.presentation.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmenttracker.Adapters.AssignmentsRecyclerViewAdapter;
import com.example.assignmenttracker.Adapters.StudentRecyclerViewAdapter;
import com.example.assignmenttracker.R;
import com.example.assignmenttracker.database.RoomDB;
import com.example.assignmenttracker.databinding.ActivityDetailedAssignmentsBinding;
import com.example.assignmenttracker.models.AssignmentModel;
import com.example.assignmenttracker.models.StudentModel;
import com.example.assignmenttracker.presenentation.new_ui.HomeScreenActivity;
import com.google.android.material.snackbar.Snackbar;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class DetailedAssignmentsActivity extends AppCompatActivity {

    ActivityDetailedAssignmentsBinding binding;
    private Context context;
    RoomDB database;
    int sId=-1;
    StudentModel currentStudent;
    List<AssignmentModel> assignmentsList;
    AssignmentsRecyclerViewAdapter assignmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDetailedAssignmentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sId= getIntent().getIntExtra("sId",0);

    }

    @Override
    protected void onResume() {
        super.onResume();

        init();
        currentStudent= database.studentDAO().getStudentById(sId);
        setCurrentStudent();
        setupRecyclerViewAdapter();

        //binding.fabAddAssignment.setOnClickListener(v->{navigateToAddAssignment(sId);});

    }

    private void setCurrentStudent(){
        binding.tvStudentName.setText(currentStudent.getsName());
        binding.tvReferBy.setText(currentStudent.getsReferBy());
        binding.tvUniversity.setText(currentStudent.getsUniversityName());
        binding.tvPhoneNo.setText(currentStudent.getsMobileNumber());
    }

    private  void setupRecyclerViewAdapter(){

        ArrayList<AssignmentModel> assignments= new ArrayList<>();
        assignments.addAll(assignmentsList);

        assignmentAdapter = new AssignmentsRecyclerViewAdapter(DetailedAssignmentsActivity.this,assignments);
        binding.recyclerviewAssignments.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerviewAssignments.setAdapter(assignmentAdapter);

        setupSwipeListener();
    }

    private void setupSwipeListener(){
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


                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailedAssignmentsActivity.this);

                    // Set the message show for the Alert time
                    builder.setMessage("Are you sure ?");

                    // Set Alert Title
                    builder.setTitle("Delete "+ assignmentsList.get(position).getProject());

                    // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                    builder.setCancelable(false);

                    // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                    builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                        // When the user click yes button then app will close

                        AssignmentModel deleteData = assignmentsList.get(position);

                        database.assignmentDAO().deleteAssignment(assignmentsList.get(position).getAssignmentId());
                        assignmentsList.remove(position);
                        refreshAssignmentList();
                        //Toast.makeText(DetailedAssignmentsActivity.this,"Assignment Deleted",Toast.LENGTH_SHORT).show();

                        Snackbar.make(binding.recyclerviewAssignments, "xxxx", Snackbar.LENGTH_LONG).setAction("undo", v -> {
                            assignmentsList.add(deleteData);
                            database.assignmentDAO().insertAssignment(deleteData);
                            refreshAssignmentList();
                        }).show();

                        dialog.dismiss();
                    });

                    builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                        // If user click no then dialog box is canceled.
                        refreshAssignmentList();
                        dialog.dismiss();
                    });

                    // Create the Alert dialog
                    AlertDialog alertDialog = builder.create();
                    // Show the Alert Dialog box
                    alertDialog.show();


                } else if (direction == ItemTouchHelper.RIGHT) {
                    Intent viewAssignment=new Intent(DetailedAssignmentsActivity.this, AddUpdateViewAssignmentsActivity.class);
                    viewAssignment.putExtra("action",AddUpdateViewAssignmentsActivity.ACTION_UPDATE);
                    viewAssignment.putExtra("sId",sId);
                    viewAssignment.putExtra("assignmentId",assignmentsList.get(position).getAssignmentId());
                    startActivity(viewAssignment);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(DetailedAssignmentsActivity.this, R.color.red))
                        .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                        .addSwipeLeftLabel("Delete")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(DetailedAssignmentsActivity.this,R.color.white))
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(DetailedAssignmentsActivity.this,R.color.white))
                        .addSwipeRightActionIcon(R.drawable.baseline_edit_24)
                        .addSwipeRightLabel("Edit")
                        .setSwipeRightLabelColor(ContextCompat.getColor(DetailedAssignmentsActivity.this,R.color.black))
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }
        };



        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerviewAssignments);
    }

    private void init(){
        context= DetailedAssignmentsActivity.this;
        database= RoomDB.getInstance(getApplicationContext(),false);

        assignmentsList = database.assignmentDAO().getAssignmentsForStudent(sId);
    }

    private void refreshAssignmentList(){
        assignmentsList = database.assignmentDAO().getAssignmentsForStudent(sId);
        ArrayList<AssignmentModel> assignments= new ArrayList<>();
        assignments.addAll(assignmentsList);
        assignmentAdapter.setListOfAssignments(assignments);
        assignmentAdapter.notifyDataSetChanged();
    }

    private void navigateToAddAssignment(int sId){

        Intent viewAssignment=new Intent(DetailedAssignmentsActivity.this, AddUpdateViewAssignmentsActivity.class);
        viewAssignment.putExtra("action",AddUpdateViewAssignmentsActivity.ACTION_ADD);
        viewAssignment.putExtra("sId",sId);
        startActivity(viewAssignment);

        /*bottomSheet = new MyBottomSheetDialogFragment(false,sId);
        bottomSheet.setOnDismissListener(new MyBottomSheetDialogFragment.OnDismissListener() {
            @Override
            public void onDismiss() {
                refreshAssignmentList();
            }
        });
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());*/

    }

}