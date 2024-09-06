package com.example.assignmenttracker.presentation.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.assignmenttracker.R;
import com.example.assignmenttracker.database.RoomDB;
import com.example.assignmenttracker.databinding.ActivityViewAssignmentBinding;
import com.example.assignmenttracker.models.AssignmentModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ViewAssignmentActivity extends AppCompatActivity {

    ActivityViewAssignmentBinding binding;
    int assignmentId=-1;
    Context context;
    RoomDB database;
    AssignmentModel assignment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityViewAssignmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        assignmentId=getIntent().getIntExtra("assignmentId",-1);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    150);
        }
        else {
            init();
            if (assignment != null)
                setupAssignmentForView();
        }
    }

    private void init(){
        context= ViewAssignmentActivity.this;
        database= RoomDB.getInstance(context);

        if(assignmentId!=-1)
            assignment=database.assignmentDAO().getAssignmentById(assignmentId);
    }

    private void setupAssignmentForView(){
        binding.tvProjectNameView.setText(assignment.getProject());
        binding.tvSemesterView.setText(assignment.getSemester());
        binding.tvSubjectView.setText(assignment.getSubject());
        binding.tvInDateView.setText(assignment.getInDate());
        binding.tvOutDateView.setText(assignment.getOutDate());
        binding.tvInputDocView.setText(assignment.getInputDoc());

        binding.ivAdvanceSs.setImageBitmap(loadBitmapFromFile(assignment.getAdvancePaymentScreenshot()));
        binding.ivFinalPaymentSs.setImageBitmap(loadBitmapFromFile(assignment.getFinalPaymentScreenShot()));

        binding.tvInputDocView.requestFocus();
    }

    public Bitmap loadBitmapFromFile(String filePath) {
        Bitmap bitmap = null;
        File file = new File(filePath);

        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                bitmap = BitmapFactory.decodeStream(fis);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println("File does not exist.");
            Toast.makeText(ViewAssignmentActivity.this,"File does not exist.",Toast.LENGTH_SHORT).show();
        }

        return bitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 150) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
                init();
                if (assignment != null)
                    setupAssignmentForView();
            } else {
                // Permission denied
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}