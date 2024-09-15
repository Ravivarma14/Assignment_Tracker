package com.example.assignmenttracker.presentation.ui;

import static com.example.assignmenttracker.database.RoomDB.databaseName;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.assignmenttracker.R;
import com.example.assignmenttracker.database.AssignmentDAO;
import com.example.assignmenttracker.database.RoomDB;
import com.example.assignmenttracker.database.StudentDAO;
import com.example.assignmenttracker.databinding.ActivitySettingsBinding;
import com.example.assignmenttracker.models.AssignmentModel;
import com.example.assignmenttracker.models.StudentModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    Context context;
    RoomDB database;
    public static String restoreFilePath;
    public Handler mainHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainHandler= new Handler(Looper.getMainLooper());

        init();

        binding.btnBackup.setOnClickListener(v->{
            File newFolder= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"Assignment_Backup");
            if(!newFolder.exists()){
                if(!newFolder.mkdirs()){

                }
            }

            File backupFile = new File(newFolder, "StudentsBackup.csv");
            exportDatabase(getApplicationContext(),database.studentDAO(),database.assignmentDAO(),backupFile.getAbsolutePath());

            addFileToMediaStore(getApplicationContext(),backupFile.getAbsolutePath());



            /*LocalDateTime now = null;
            String fileName="backup.db";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = now.format(formatter);

                 fileName= "backup.db";
            }
            File newFolder= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"Assignment_Backup");
            if(!newFolder.exists()){
                if(!newFolder.mkdirs()){

                }
            }

            File backupFile = new File(newFolder, fileName);

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



                    Toast.makeText(context, "Backup successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,"No DB file exists",Toast.LENGTH_SHORT).show();
                }

            }
            catch (IOException e){
                e.printStackTrace();
                Toast.makeText(context,"Error while backup",Toast.LENGTH_SHORT).show();
            }



            restoreFilePath= backupFile.getAbsolutePath();
            //backupDatabase(getApplicationContext(), databaseName, backupFile);

            addFileToMediaStore(getApplicationContext(),backupFile.getAbsolutePath());
*/
        });

        binding.btnRestore.setOnClickListener(v->{
            File newFolder= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"Assignment_Backup");
            File backupFile = new File(newFolder, binding.tvSelectedDb.getText().toString());

            importCsvToDatabase(getApplicationContext(),database.studentDAO(),database.assignmentDAO(),backupFile.getAbsolutePath());
            //restoreFilePath= backupFile.getAbsolutePath();
                //restoreDatabase(getApplicationContext(), backupFile, databaseName);


            /*File dbFile = context.getDatabasePath(databaseName);
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
*//*

            File dbFile = context.getDatabasePath(databaseName);
            try (FileInputStream fis = new FileInputStream(backupFile);
            FileOutputStream fos = new FileOutputStream(dbFile)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                Toast.makeText(context, "Restore successful", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Restore failed", Toast.LENGTH_SHORT).show();
            }


            RoomDB.getInstance(getApplicationContext(),true);*/


        });

        binding.btnChooseFile.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "Select DB file"), 120);
        });
    }


    public void exportDatabaseToCsv(Context context, ArrayList<StudentModel> data, ArrayList<AssignmentModel> assignments, String filePath) {
        File file = new File(filePath);
        try (FileWriter writer = new FileWriter(file)) {
            // Write the header
            writer.append("id,name,value\n");

            // Write data
            for (StudentModel entity : data) {
                writer.append(
                        String.valueOf(entity.getsId())).append(',')
                        .append(entity.getsName()).append(',')
                        .append(entity.getsUniversityName()).append(',')
                        .append(entity.getsMobileNumber()).append(',')
                        .append(entity.getsReferBy()).append(',')
                        .append('\n');
            }

            for (AssignmentModel entity : assignments) {
                writer.append(
                        String.valueOf(entity.getAssignmentId())).append(',')
                        .append(String.valueOf(entity.getsId())).append(',')
                        .append(entity.getSemester()).append(',')
                        .append(entity.getSubject()).append(',')
                        .append(entity.getProject()).append(',')
                        .append(entity.getInDate()).append(',')
                        .append(entity.getOutDate()).append(',')
                        .append(entity.getInputDoc()).append(',')
                        .append(String.valueOf(entity.getPrice())).append(',')
                        .append(String.valueOf(entity.getAdvancePayment())).append(',')
                        .append(entity.getAdvancePaymentScreenshot()).append(',')
                        .append(String.valueOf(entity.getFinalPayment())).append(',')
                        .append(entity.getFinalPaymentScreenShot()).append(',')
                        .append('\n');
            }

            mainHandler.post(() -> Toast.makeText(context.getApplicationContext(), "Export successful", Toast.LENGTH_SHORT).show());


        } catch (IOException e) {
            e.printStackTrace();
            mainHandler.post(()-> Toast.makeText(context.getApplicationContext(), "Export failed", Toast.LENGTH_SHORT).show());
        }
    }

    public void exportDatabase(Context context, StudentDAO exampleDao, AssignmentDAO assignmentDAO, String filePath) {
        new Thread(() -> {
            List<StudentModel> data = exampleDao.getAllStudents();
            ArrayList<StudentModel> list = new ArrayList<>();
            list.addAll(data);

            List<AssignmentModel> data2 = assignmentDAO.getAllAssignments();
            ArrayList<AssignmentModel> list2 = new ArrayList<>();
            list2.addAll(data2);
            exportDatabaseToCsv(context, list,list2, filePath);
        }).start();
    }




    public void importCsvToDatabase(Context context, StudentDAO exampleDao,AssignmentDAO assignmentDAO, String filePath) {
        File file = new File(filePath);
        List<StudentModel> entities = new ArrayList<>();
        List<AssignmentModel> entities2 = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Skip header line
            reader.readLine();

            // Read each line of the CSV
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length == 5) {
                    Log.d("TAG", "importCsvToDatabase: student imported");
                    StudentModel entity = new StudentModel();
                    entity.setsId(Integer.parseInt(columns[0]));
                    entity.setsName(columns[1]);
                    entity.setsUniversityName(columns[2]);
                    entity.setsMobileNumber(columns[3]);
                    entity.setsReferBy(columns[4]);
                    entities.add(entity);
                }

                else {
                    Log.d("TAG", "importCsvToDatabase: assignment imported");
                    AssignmentModel entity = new AssignmentModel();
                    entity.setAssignmentId(Integer.parseInt(columns[0]));
                    entity.setsId(Integer.parseInt(columns[1]));
                    entity.setSemester(columns[2]);
                    entity.setSubject(columns[3]);
                    entity.setProject(columns[4]);
                    entity.setInDate(columns[5]);
                    entity.setOutDate(columns[6]);
                    entity.setInputDoc(columns[7]);
                    entity.setPrice(Integer.parseInt(columns[8]));
                    entity.setAdvancePayment(Integer.parseInt(columns[9]));
                    entity.setAdvancePaymentScreenshot(columns[10]);
                    entity.setFinalPayment(Integer.parseInt(columns[11]));
                    entity.setAdvancePaymentScreenshot(columns[12]);

                    entities2.add(entity);
                }

            }

            // Insert data into the database
            new Thread(() -> {
                exampleDao.insertAll(entities);
                assignmentDAO.insertAllAssignments(entities2);
                mainHandler.post(()->Toast.makeText(context, "Import successful", Toast.LENGTH_SHORT).show());
                finish();
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
            mainHandler.post(()->Toast.makeText(context, "Import failed", Toast.LENGTH_SHORT).show());
        }
    }



    ////////////////////////////////////
    public static void addFileToMediaStore(Context context, String filePath) {
        MediaScannerConnection.scanFile(context,
                new String[]{filePath},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        // File has been scanned and is now visible to other apps
                    }
                });
    }

    private void init(){
        context= SettingsActivity.this;

        database= RoomDB.getInstance(getApplicationContext(),false);
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