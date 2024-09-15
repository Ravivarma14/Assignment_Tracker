package com.example.assignmenttracker.presenentation.new_ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmenttracker.Adapters.AssignmentsRecyclerViewAdapter;
import com.example.assignmenttracker.R;
import com.example.assignmenttracker.database.AssignmentDAO;
import com.example.assignmenttracker.database.RoomDB;
import com.example.assignmenttracker.database.StudentDAO;
import com.example.assignmenttracker.databinding.ActivityHomeScreen2Binding;
import com.example.assignmenttracker.models.AssignmentModel;
import com.example.assignmenttracker.models.StudentModel;
import com.example.assignmenttracker.presentation.ui.AddUpdateViewAssignmentsActivity;
import com.example.assignmenttracker.presentation.ui.DetailedAssignmentsActivity;
import com.example.assignmenttracker.presentation.ui.SettingsActivity;
import com.example.assignmenttracker.utils.PathUtils;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class HomeScreenActivity extends AppCompatActivity {

    ActivityHomeScreen2Binding binding;
    private Context context;
    RoomDB database;
    List<AssignmentModel> assignmentsList;
    AssignmentsRecyclerViewAdapter assignmentAdapter;
    ProgressDialog progressDialog=null;
    static String zipFilePath="";
    String folderPath=(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator+ "Assignment_Backup");
    Handler mainHandler=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityHomeScreen2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        askForPermissions();
        setupActionBar();

        setSearchView();

        mainHandler= new Handler(Looper.getMainLooper());
    }

    private void setupActionBar(){
        // Set up the custom action bar
        //Toolbar toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(binding.customToolbar);

        // Customize the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);  // Hide the default title
            actionBar.setDisplayShowCustomEnabled(true);  // Enable custom view
            actionBar.setCustomView(R.layout.action_bar_layout);  // Set custom layout

            TextView titleTextView= (TextView)findViewById(R.id.actionbar_title);
            titleTextView.setText("Assignments");
            //findViewById(R.id.actionbar_title).setVisibility(View.INVISIBLE);
        }
    }

    public void askForPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
                return;
            }
            //createDir();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        init();
//        currentStudent= database.studentDAO().getStudentById(sId);
//        setCurrentStudent();
        setupRecyclerViewAdapter();

        binding.searchView.clearFocus();

        binding.fabAddAssignment.setOnClickListener(v->{navigateToAddAssignment();});

    }
    private void init(){
        context= HomeScreenActivity.this;
        database= RoomDB.getInstance(getApplicationContext(),false);

        assignmentsList = database.assignmentDAO().getAllAssignments();
    }

    private  void setupRecyclerViewAdapter(){

        ArrayList<AssignmentModel> assignments= new ArrayList<>();
        assignments.addAll(assignmentsList);

        assignmentAdapter = new AssignmentsRecyclerViewAdapter(HomeScreenActivity.this,assignments);
        binding.recyclerviewAssignments.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerviewAssignments.setAdapter(assignmentAdapter);

        setupSwipeListener();
    }

    private void refreshAssignmentList(ArrayList<AssignmentModel> filteredList){
        //assignmentsList = database.assignmentDAO().getAssignmentsForStudent(sId);
//        ArrayList<AssignmentModel> assignments= new ArrayList<>();
//        assignments.addAll(assignmentsList);
        if(filteredList!=null) {
            assignmentAdapter.setListOfAssignments(filteredList);
            assignmentAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        // Handle menu item clicks
        if (id == R.id.action_backup) {
            // Perform action for Settings
            backupData();
            return true;
        } else if (id == R.id.action_restore) {
            // Perform action for

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "Select ZIP fie"), 100);

            //unZipFile(zipFilePath);
            return true;
        } else if (id== R.id.action_students_list) {
            Intent intent= new Intent(HomeScreenActivity.this, StudentsListActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri selectedUri= data.getData();
            String fileName = null; //getFileName(selectedUri);
            try {
                fileName = PathUtils.getPath(HomeScreenActivity.this,selectedUri);
                Toast.makeText(HomeScreenActivity.this,"file path: "+ fileName,Toast.LENGTH_SHORT).show();
            } catch (URISyntaxException e) {
                Toast.makeText(HomeScreenActivity.this,"error getting image path",Toast.LENGTH_SHORT).show();
            }

            unZipFile(fileName);
        }
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

                    ArrayList<AssignmentModel> filteredList = new ArrayList<>();

                    //filter by project name, student name, subject, university, semester, pn no
                    for (AssignmentModel assigment : assignmentsList) {

                        StudentModel student = database.studentDAO().getStudentById(assigment.getsId());
                        if (assigment.getProject().toLowerCase().contains(searchText) || assigment.getStudentName().toLowerCase().contains(searchText)
                                || assigment.getSubject().toLowerCase().contains(searchText) || assigment.getSemester().toLowerCase().contains(searchText)
                                || student.getsUniversityName().toLowerCase().contains(searchText) || student.getsMobileNumber().toLowerCase().contains(searchText)) {
                            filteredList.add(assigment);
                        }
                    }
                    refreshAssignmentList(filteredList);

                } else{
                    refreshAssignmentList((ArrayList<AssignmentModel>) assignmentsList);
                }
                return false;
            }
        });
    }

    private void navigateToAddAssignment() {

        Intent viewAssignment = new Intent(HomeScreenActivity.this, AddUpdateViewAssignmentsActivity.class);
        viewAssignment.putExtra("action", AddUpdateViewAssignmentsActivity.ACTION_ADD);
        startActivity(viewAssignment);
    }

    private void backupData(){

        progressDialog = new ProgressDialog(HomeScreenActivity.this);
        progressDialog.setMessage("Backing up please wait...");
        progressDialog.show();

        File newFolder= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"Assignment_Backup");
        if(!newFolder.exists()){
            if(!newFolder.mkdirs()){

            }
        }

        File backupFile = new File(newFolder, "StudentsBackup.csv");

        new Handler().postDelayed(()->{
            exportDatabase(getApplicationContext(),database.studentDAO(),database.assignmentDAO(),backupFile.getAbsolutePath());
        },3000);
        //exportDatabase(getApplicationContext(),database.studentDAO(),database.assignmentDAO(),backupFile.getAbsolutePath());

    }

    public void exportDatabase(Context context, StudentDAO studentDAO, AssignmentDAO assignmentDAO, String filePath) {
        //new Thread(() -> {
            List<StudentModel> data = studentDAO.getAllStudents();

            List<AssignmentModel> data2 = assignmentDAO.getAllAssignments();

            exportDatabaseToCsv(context, (ArrayList<StudentModel>) data, (ArrayList<AssignmentModel>) data2, filePath);
       // }).start();
    }

    public void exportDatabaseToCsv(Context context, ArrayList<StudentModel> data, ArrayList<AssignmentModel> assignments, String filePath) {
        File file = new File(filePath);
        try (FileWriter writer = new FileWriter(file)) {
            // Write the header
            writer.append("id,name,university,mobileno,referby\n");

            // Write data1
            for (StudentModel entity : data) {
                writer.append(
                                String.valueOf(entity.getsId())).append(',')
                        .append(entity.getsName()).append(',')
                        .append(entity.getsUniversityName()).append(',')
                        .append(entity.getsMobileNumber()).append(',')
                        .append(entity.getsReferBy()).append(',')
                        .append('\n');
            }

            writer.append("id,studentId,student,semester,subject,project,inDate,outData,inputDoc, price, advancePayment, advancePaymentSSPath, finalPayment, finalPaymentSSPath\n");

            ArrayList<String> fileToZip= new ArrayList<>();

            for (AssignmentModel entity : assignments) {

                fileToZip.add(entity.getInputDoc());
                fileToZip.add(entity.getAdvancePaymentScreenshot());
                fileToZip.add(entity.getFinalPaymentScreenShot());
                Log.d("TAG", "exportDatabaseToCsv: final ss: "+ entity.getFinalPaymentScreenShot());
                Log.d("TAG", "exportDatabaseToCsv: final ss: "+ folderPath+File.separator+ "Unzipped/"+entity.getFinalPaymentScreenShot().substring(entity.getFinalPaymentScreenShot().lastIndexOf('/')));

                writer.append(
                                String.valueOf(entity.getAssignmentId())).append(',')
                        .append(String.valueOf(entity.getsId())).append(',')
                        .append(entity.getStudentName()).append(',')
                        .append(entity.getSemester()).append(',')
                        .append(entity.getSubject()).append(',')
                        .append(entity.getProject()).append(',')
                        .append(entity.getInDate()).append(',')
                        .append(entity.getOutDate()).append(',')
                        .append(folderPath+File.separator+ "Unzipped/"+entity.getInputDoc().substring(entity.getInputDoc().lastIndexOf('/'))).append(',')
                        .append(String.valueOf(entity.getPrice())).append(',')
                        .append(String.valueOf(entity.getAdvancePayment())).append(',')
                        .append(folderPath+File.separator+ "Unzipped/"+entity.getAdvancePaymentScreenshot().substring(entity.getAdvancePaymentScreenshot().lastIndexOf('/'))).append(',')
                        .append(String.valueOf(entity.getFinalPayment())).append(',')
                        .append(folderPath+File.separator+ "Unzipped/"+entity.getFinalPaymentScreenShot().substring(entity.getFinalPaymentScreenShot().lastIndexOf('/'))).append(',')
                        .append('\n');

            }

            writer.close();

            SettingsActivity.addFileToMediaStore(HomeScreenActivity.this, filePath);
            fileToZip.add(file.getAbsolutePath());

            String zipFileName="Assignmets_zip.zip";
            createZipfile(fileToZip, zipFileName);

            SettingsActivity.addFileToMediaStore(HomeScreenActivity.this, zipFilePath);

            //Toast.makeText(HomeScreenActivity.this, "Export successful", Toast.LENGTH_SHORT).show();
            mainHandler.post(() -> Toast.makeText(context.getApplicationContext(), "Export successful", Toast.LENGTH_SHORT).show());

            if(progressDialog!=null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog=null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            //Toast.makeText(HomeScreenActivity.this, "Export failed", Toast.LENGTH_SHORT).show();
            mainHandler.post(()-> Toast.makeText(context.getApplicationContext(), "Export failed", Toast.LENGTH_SHORT).show());
        }
    }

    private void createZipfile(ArrayList<String> files, String zipFile){
        try{
            File zip= new File(folderPath, File.separator+ zipFile);

            zipFilePath=zip.getAbsolutePath();

            byte[] buffer= new byte[1024];

            FileOutputStream fos = new FileOutputStream(zip.getAbsolutePath());
            ZipOutputStream zos= new ZipOutputStream(fos);

            for(String filePath : files){
                File file= new File(filePath);
                FileInputStream fis= new FileInputStream(file);

                ZipEntry zipEntry= new ZipEntry(file.getName());
                zos.putNextEntry(zipEntry);

                int lenght;
                while((lenght= fis.read(buffer)) >0 ){
                    zos.write(buffer,0,lenght);
                }

                zos.closeEntry();
                fis.close();

            }

            zos.close();
            fos.close();
        }
         catch (Exception e) {
            e.printStackTrace();
            mainHandler.post(()->Toast.makeText(HomeScreenActivity.this, "Error making zip file: "+ e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void unZipFile(String filePath){

        progressDialog = new ProgressDialog(HomeScreenActivity.this);
        progressDialog.setMessage("Backing up please wait...");
        progressDialog.show();


        byte[] buffer= new byte[1024];
        File dir= new File(folderPath+File.separator+ "Unzipped");
        if(!dir.exists()){
            dir.mkdirs();
        }

        String csvFileToRestore="";

        try {
            FileInputStream fis= new FileInputStream(filePath);
            ZipInputStream zis= new ZipInputStream(fis);

            ZipEntry zipEntry= new ZipEntry(zis.getNextEntry());
            while(zipEntry!=null){
                File file= new File(dir,zipEntry.getName());
                if(file.getAbsolutePath().contains(".csv")){
                    csvFileToRestore= file.getAbsolutePath();
                }
                FileOutputStream fos= new FileOutputStream(file);

                int lenght=0;
                while((lenght= zis.read(buffer))>0){
                    fos.write(buffer,0, lenght);
                }
                fos.close();
                SettingsActivity.addFileToMediaStore(HomeScreenActivity.this, file.getAbsolutePath());

                zipEntry= zis.getNextEntry();
            }

            fis.close();
            zis.close();


            if(!csvFileToRestore.isEmpty())
                importCsvToDatabase(HomeScreenActivity.this, database.studentDAO(),database.assignmentDAO(), csvFileToRestore);

            dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(HomeScreenActivity.this, "Error unzipping file: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
                if(columns[0].equals("id")){
                    continue;
                }
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
                    entity.setStudentName(columns[2]);
                    entity.setSemester(columns[3]);
                    entity.setSubject(columns[4]);
                    entity.setProject(columns[5]);
                    entity.setInDate(columns[6]);
                    entity.setOutDate(columns[7]);
                    entity.setInputDoc(columns[8]);
                    entity.setPrice(Integer.parseInt(columns[9]));
                    entity.setAdvancePayment(Integer.parseInt(columns[10]));
                    entity.setAdvancePaymentScreenshot(columns[11]);
                    entity.setFinalPayment(Integer.parseInt(columns[12]));
                    entity.setFinalPaymentScreenShot(columns[13]);

                    entities2.add(entity);
                }

            }

            // Insert data into the database
            new Thread(() -> {
                exampleDao.insertAll(entities);
                assignmentDAO.insertAllAssignments(entities2);
                mainHandler.post(()->Toast.makeText(context, "Import successful", Toast.LENGTH_SHORT).show());
                mainHandler.post(()->{
                    assignmentsList = database.assignmentDAO().getAllAssignments();
                    refreshAssignmentList((ArrayList<AssignmentModel>) assignmentsList);
                });
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
            mainHandler.post(()->Toast.makeText(context, "Import failed", Toast.LENGTH_SHORT).show());
        }
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenActivity.this);

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

                        //assignmentsList = database.assignmentDAO().getAllAssignments();
                        refreshAssignmentList((ArrayList<AssignmentModel>) assignmentsList);
                        //Toast.makeText(DetailedAssignmentsActivity.this,"Assignment Deleted",Toast.LENGTH_SHORT).show();

                        Snackbar.make(binding.recyclerviewAssignments, deleteData.getProject()+" deleted", Snackbar.LENGTH_LONG).setAction("undo", v -> {
                            assignmentsList.add(deleteData);
                            database.assignmentDAO().insertAssignment(deleteData);
                            refreshAssignmentList((ArrayList<AssignmentModel>) assignmentsList);
                        }).show();


                        dialog.dismiss();
                    });

                    // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                    builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                        // If user click no then dialog box is canceled.
                        refreshAssignmentList((ArrayList<AssignmentModel>) assignmentsList);
                        dialog.dismiss();
                    });

                    // Create the Alert dialog
                    AlertDialog alertDialog = builder.create();
                    // Show the Alert Dialog box
                    alertDialog.show();


                } else if (direction == ItemTouchHelper.RIGHT) {
                    Intent viewAssignment = new Intent(HomeScreenActivity.this, AddUpdateViewAssignmentsActivity.class);
                    viewAssignment.putExtra("action", AddUpdateViewAssignmentsActivity.ACTION_UPDATE);
                    viewAssignment.putExtra("sId", assignmentsList.get(position).getsId());
                    viewAssignment.putExtra("assignmentId", assignmentsList.get(position).getAssignmentId());
                    startActivity(viewAssignment);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(HomeScreenActivity.this, R.color.red))
                        .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                        .addSwipeLeftLabel("Delete")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(HomeScreenActivity.this, R.color.white))
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(HomeScreenActivity.this, R.color.white))
                        .addSwipeRightActionIcon(R.drawable.baseline_edit_24)
                        .addSwipeRightLabel("Edit")
                        .setSwipeRightLabelColor(ContextCompat.getColor(HomeScreenActivity.this, R.color.black))
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerviewAssignments);

    }


        private void dismissProgressDialog(){
        if(progressDialog!=null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog=null;
        }
    }

}