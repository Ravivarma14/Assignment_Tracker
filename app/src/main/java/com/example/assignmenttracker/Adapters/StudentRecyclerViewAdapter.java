package com.example.assignmenttracker.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmenttracker.databinding.ItemviewStudentBinding;
import com.example.assignmenttracker.models.StudentModel;
import com.example.assignmenttracker.presentation.ui.DetailedAssignmentsActivity;

import java.util.ArrayList;

public class StudentRecyclerViewAdapter extends RecyclerView.Adapter<StudentRecyclerViewAdapter.StudentViewHolder> {

    private ArrayList<StudentModel> listOfStudents;
    private Context context;
    public StudentRecyclerViewAdapter(Context context, ArrayList<StudentModel> list){
        this.context=context;
        this.listOfStudents=list;
    }
    public void setListOfStudents(ArrayList<StudentModel> listOfStudents){
        this.listOfStudents=listOfStudents;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemviewStudentBinding binding= ItemviewStudentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new StudentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentModel student= listOfStudents.get(holder.getAdapterPosition());

        holder.binding.tvStudentName.setText(student.getsName());
        holder.binding.tvMobileNo.setText(student.getsMobileNumber());
        holder.binding.tvUniversityName.setText(student.getsUniversityName());
        holder.binding.tvAssignmentCount.setText("5"); //Count get from another table

        holder.binding.getRoot().setOnClickListener(v->{
            Intent assignmentsDetails=new Intent(context, DetailedAssignmentsActivity.class);
            assignmentsDetails.putExtra("sId",student.getsId());
            context.startActivity(assignmentsDetails);
        });
    }

    @Override
    public int getItemCount() {
        return listOfStudents.size();
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder{
        ItemviewStudentBinding binding;
        public StudentViewHolder(@NonNull ItemviewStudentBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
