package com.example.assignmenttracker.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmenttracker.databinding.ItemviewAssignmentBinding;
import com.example.assignmenttracker.databinding.ItemviewAssignmentNewBinding;
import com.example.assignmenttracker.models.AssignmentModel;
import com.example.assignmenttracker.presentation.ui.AddUpdateViewAssignmentsActivity;
import com.example.assignmenttracker.presentation.ui.DetailedAssignmentsActivity;
import com.example.assignmenttracker.presentation.ui.ViewAssignmentActivity;

import java.util.ArrayList;

public class AssignmentsRecyclerViewAdapter extends RecyclerView.Adapter<AssignmentsRecyclerViewAdapter.AssignmentViewHolder> {


    private ArrayList<AssignmentModel> listOfAssignments;
    private Context context;
    public AssignmentsRecyclerViewAdapter(Context context, ArrayList<AssignmentModel> list){
        this.context=context;
        this.listOfAssignments=list;
    }
    public void setListOfAssignments(ArrayList<AssignmentModel> listOfAssignments){
        this.listOfAssignments=listOfAssignments;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemviewAssignmentNewBinding binding= ItemviewAssignmentNewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AssignmentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        AssignmentModel assignment= listOfAssignments.get(holder.getAdapterPosition());

        holder.binding.tvProjectName.setText(assignment.getProject());
        //holder.binding.tvInDate.setText(assignment.getInDate());
        holder.binding.tvOutDate.setText(assignment.getOutDate());
        //holder.binding.tvPrice.setText(String.valueOf(assignment.getPrice()));
        //holder.binding.tvAdvance.setText(String.valueOf(assignment.getAdvancePayment()));
        //holder.binding.tvFinalPaymentStatus.setText(assignment.getFinalPayment()==0 ? "Pending": "Completed");
        holder.binding.tvStudentName.setText(assignment.getStudentName());
        holder.binding.tvSemester.setText(assignment.getSemester());

        holder.binding.getRoot().setOnClickListener(v->{
            Intent viewAssignment=new Intent(context, ViewAssignmentActivity.class);
            viewAssignment.putExtra("assignmentId",assignment.getAssignmentId());
            context.startActivity(viewAssignment);
        });
    }

    @Override
    public int getItemCount() {
        return listOfAssignments.size();
    }

    public class AssignmentViewHolder extends RecyclerView.ViewHolder{

        ItemviewAssignmentNewBinding binding;
        public AssignmentViewHolder(@NonNull ItemviewAssignmentNewBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
            binding.tvProjectName.post(()-> binding.tvProjectName.requestFocus());
        }
    }
}
