package com.example.assignmenttracker.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.assignmenttracker.models.AssignmentModel;
import com.example.assignmenttracker.models.StudentModel;
import com.example.assignmenttracker.presentation.ui.SettingsActivity;

@Database(entities = {StudentModel.class, AssignmentModel.class}, version = 1, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    private static RoomDB database;
    public static String databaseName="AssignmentDB.db";

    public synchronized static RoomDB getInstance(Context context, boolean isRestore){
        if(isRestore){
            database= Room.databaseBuilder(context.getApplicationContext(), RoomDB.class, databaseName)
                    .createFromAsset(SettingsActivity.restoreFilePath)
                    .build();
            return database;
        }
        else {
            if (database == null) {
                database = Room.databaseBuilder(context.getApplicationContext(), RoomDB.class, databaseName)
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
            }
            return database;
        }
    }

    public abstract StudentDAO studentDAO();
    public abstract AssignmentDAO assignmentDAO();
}
