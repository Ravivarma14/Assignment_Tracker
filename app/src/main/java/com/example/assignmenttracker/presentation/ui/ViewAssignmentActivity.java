package com.example.assignmenttracker.presentation.ui;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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
    final String TAG="VIEWASSIGNMENT";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityViewAssignmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        assignmentId=getIntent().getIntExtra("assignmentId",-1);

        init();
        if (assignment != null)
            setupAssignmentForView();

    }

    private void init(){
        context= ViewAssignmentActivity.this;
        database= RoomDB.getInstance(getApplicationContext(),false);

        if(assignmentId!=-1)
            assignment=database.assignmentDAO().getAssignmentById(assignmentId);
    }

    private void setupAssignmentForView(){
        binding.tvProjectNameView.setText(assignment.getProject() +" Details");
        binding.tvSemesterView.setText(assignment.getSemester());
        binding.tvSubjectView.setText(assignment.getSubject());
        binding.tvInDateView.setText(assignment.getInDate());
        binding.tvOutDateView.setText(assignment.getOutDate());
        binding.tvInputDocView.setText(assignment.getInputDoc());

        binding.tvInputDocView.setOnClickListener(v->{
            openPdfFile(assignment.getInputDoc(), true);
        });

        binding.ivAdvanceSs.setOnClickListener(v->{
            openPdfFile(assignment.getAdvancePaymentScreenshot(),false);
        });

        binding.ivFinalPaymentSs.setOnClickListener(v->{
            openPdfFile(assignment.getFinalPaymentScreenShot(), false);
        });
        Log.d(TAG, "setupAssignmentForView: advance ss: "+ assignment.getAdvancePaymentScreenshot());
        Log.d(TAG, "setupAssignmentForView: final ss: "+assignment.getFinalPaymentScreenShot());
        binding.ivAdvanceSs.setImageBitmap(loadBitmapFromFile(assignment.getAdvancePaymentScreenshot()));
        binding.ivFinalPaymentSs.setImageBitmap(loadBitmapFromFile(assignment.getFinalPaymentScreenShot()));

        binding.tvInputDocView.requestFocus();
    }

    public Bitmap loadBitmapFromFile(String filePath) {

        Log.d(TAG, "loadBitmapFromFile: path: " + filePath);
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

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);

        return scaledBitmap;
    }

    private void openPdfFile(String filePath, boolean isPDF) {
        File file = new File(filePath);

        // Check if the file exists
        if (file.exists()) {
            Uri fileUri;

            // Android 7.0+ requires "content://" URIs instead of "file://"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // Use FileProvider to get a content Uri for the file
                fileUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            } else {
                fileUri = Uri.fromFile(file);
            }

            // Create an Intent to view the PDF
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if(isPDF)
                intent.setDataAndType(fileUri, "application/pdf");
            else
                intent.setDataAndType(fileUri, "image/*");

            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Try to open the PDF file
            try {
                startActivity(Intent.createChooser(intent, "Open PDF with"));
            } catch (ActivityNotFoundException e) {
                // Handle the case where no PDF viewer is installed
                Toast.makeText(this, "No PDF viewer app installed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        }
    }

}