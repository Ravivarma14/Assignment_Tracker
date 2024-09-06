package com.example.assignmenttracker.presentation.ui;

import static com.example.assignmenttracker.database.RoomDB.databaseName;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.assignmenttracker.R;
import com.example.assignmenttracker.database.RoomDB;
import com.example.assignmenttracker.databinding.ActivitySettingsBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    Context context;
    RoomDB database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        binding.btnBackup.setOnClickListener(v->{
            LocalDateTime now = null;
            String fileName="backup.db";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = now.format(formatter);

                 fileName= "backup"+formattedDateTime+ ".db";
            }
            File backupFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileName);
            backupDatabase(context, databaseName, backupFile);
        });

        binding.btnRestore.setOnClickListener(v->{

            File backupFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), binding.tvSelectedDb.getText().toString());
                restoreDatabase(context, backupFile, databaseName);
        });

        binding.btnChooseFile.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/.db");
            startActivityForResult(Intent.createChooser(intent, "Select DB file"), 120);
        });
    }

    private void init(){
        context= SettingsActivity.this;

        database= RoomDB.getInstance(context);
    }


    public static void backupDatabase(Context context, String databaseName, File backupFile){
        try {
            File dbFile = context.getDatabasePath(databaseName);
            if (dbFile.exists()) {
                try (InputStream inputStream = new FileInputStream(dbFile);
                     OutputStream outputStream = new FileOutputStream(backupFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                }
            } else {
                Toast.makeText(context,"No DB file exists",Toast.LENGTH_SHORT).show();
            }
        }
        catch (IOException e){
            Toast.makeText(context,"Error while backup",Toast.LENGTH_SHORT).show();
        }
    }

    public static void restoreDatabase(Context context, File backupFile, String databaseName){
        File dbFile = context.getDatabasePath(databaseName);
        try (InputStream inputStream = new FileInputStream(backupFile);
             OutputStream outputStream = new FileOutputStream(dbFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
        catch (IOException e){
            Toast.makeText(context,"Error while backup",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedUri = null;

        if (requestCode == 120 && resultCode == RESULT_OK && data != null) {
            selectedUri = data.getData();
            String fileName = getFileName(selectedUri);
            binding.tvSelectedDb.setText(fileName);
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