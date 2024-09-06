package com.example.assignmenttracker.presentation.ui;

import static com.example.assignmenttracker.presentation.ui.HomeScreenActivity.database;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.assignmenttracker.R;
import com.example.assignmenttracker.databinding.FragmentAddAssignmentBinding;
import com.example.assignmenttracker.databinding.FragmentAddStudentBinding;
import com.example.assignmenttracker.models.AssignmentModel;
import com.example.assignmenttracker.models.StudentModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public boolean isAddStudent=true;
    public int studentId;
    FragmentAddStudentBinding fragmentAddStudentBinding;
    FragmentAddAssignmentBinding fragmentAddAssignmentBinding;
    private OnDismissListener onDismissListener;

    public MyBottomSheetDialogFragment(boolean isAddStudent, int sId){
        this.isAddStudent=isAddStudent;
        this.studentId=sId;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        if(isAddStudent){
            fragmentAddStudentBinding= FragmentAddStudentBinding.inflate(inflater,container,false);
            return fragmentAddStudentBinding.getRoot();
            }
        else {
            fragmentAddAssignmentBinding=FragmentAddAssignmentBinding.inflate(inflater,container,false);
            return fragmentAddAssignmentBinding.getRoot();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(isAddStudent)
            setAddStudentListerners();
        else
            setAddAssignmentListerners();
    }

    private void setAddStudentListerners(){
        fragmentAddStudentBinding.btnAddStudent.setOnClickListener(v->{
            StudentModel studentModel= new StudentModel();

            String studentName=fragmentAddStudentBinding.etStudentName.getText().toString();
            String universityName=fragmentAddStudentBinding.etUniversityName.getText().toString();
            String phoneNo=fragmentAddStudentBinding.etPhoneNumber.getText().toString();
            String referBy=fragmentAddStudentBinding.etReferBy.getText().toString();

            studentModel.setsName(studentName);
            studentModel.setsUniversityName(universityName);
            studentModel.setsMobileNumber(phoneNo);
            studentModel.setsReferBy(referBy);

            database.studentDAO().insertStudent(studentModel);

            onDismiss(this.getDialog());
        });
    }

    private void setAddAssignmentListerners(){

    /*    fragmentAddAssignmentBinding.btnSelectInputDoc.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), 100);
        });

        fragmentAddAssignmentBinding.btnAddAssignment.setOnClickListener(v->{
            if(studentId!=-1) {
                AssignmentModel assignmentModel = new AssignmentModel();

                assignmentModel.setsId(studentId);
                assignmentModel.setSemester(fragmentAddAssignmentBinding.etSemester.getText().toString());
                assignmentModel.setSubject(fragmentAddAssignmentBinding.etSubject.getText().toString());
                assignmentModel.setProject(fragmentAddAssignmentBinding.etProject.getText().toString());
                assignmentModel.setInDate(fragmentAddAssignmentBinding.etAutoFillInDate.getText().toString());
                assignmentModel.setOutDate(fragmentAddAssignmentBinding.etOutDate.getText().toString());
                assignmentModel.setPrice(Integer.parseInt(fragmentAddAssignmentBinding.etPrice.getText().toString()));
                assignmentModel.setAdvancePayment(Integer.parseInt(fragmentAddAssignmentBinding.etAdvance.getText().toString()));
                assignmentModel.setFinalPayment(Integer.parseInt(fragmentAddAssignmentBinding.etFinalPayment.getText().toString()));


                assignmentModel.setInputDoc(path);
                assignmentModel.setFinalPaymentScreenShot(finalPaymentSSPath);
                assignmentModel.setAdvancePaymentScreenshot(advancePaymentSSPath);

                database.assignmentDAO().insertAssignment(assignmentModel);
            }
        });*/

    }



    public interface OnDismissListener {
        void onDismiss();
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.onDismissListener = listener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(onDismissListener!=null)
            onDismissListener.onDismiss();
    }

}
