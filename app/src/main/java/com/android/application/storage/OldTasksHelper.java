package com.android.application.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class creates a database with tables that store all the old data. Data
 * for tasks in the past are initially moved to this database so that the user
 * can still see them if necessary. The data in the tables in this database
 * are periodically deleted by the RemoveTasksService.
 *
 * When details about old tasks are moved to the oldtasks database, the notes
 * contained within them are deleted and are lost.
 *
 * Created by Sunil on 12/31/2014.
 */
public class OldTasksHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "olddata.db";
    private static final String TABLE_OLD_TASKS = "oldtasks";
    private static final String TABLE_OLD_SUBTASKS = "oldsubtasks";

    // Columns in table "tasks"
    private static final String TASK_ID = "task_id";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String TASK = "task";
    private static final String SUBTASKS = "subtasks";
    private static final String TASK_STATUS = "task_status";
    private static final String TASK_DESCRIPTION = "description";

    // Columns in table "subtasks"
    private static final String SUBTASK_ID = "subtask_id";
    private static final String SUBTASK = "subtask";
    private static final String SUBTASK_STATUS = "subtask_status";
    private static final String SUBTASK_DESCRIPTION = "subtask_description";

    // Create table "tasks"
    private static final String CREATE_TABLE_OLDTASK = "CREATE TABLE " + TABLE_OLD_TASKS + " ("
            + TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DATE + " TEXT, " + TIME + " TEXT, "
            + TASK + " TEXT, "  + SUBTASKS + " INTEGER DEFAULT 0, "
            + TASK_STATUS + " INTEGER DEFAULT 0, " + TASK_DESCRIPTION + " TEXT);";

    // Create table "subtasks"
    private static final String CREATE_TABLE_OLDSUBTASKS = "CREATE TABLE " + TABLE_OLD_SUBTASKS + " ("
            + SUBTASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK_ID + " INTEGER NOT NULL, "
            + SUBTASK + " TEXT NOT NULL, " + SUBTASK_STATUS + " INTEGER DEFAULT 0, "
            + SUBTASK_DESCRIPTION + " TEXT, " + " FOREIGN KEY(" + TASK_ID + ") REFERENCES "
            + TABLE_OLD_TASKS + "(" + TASK_ID + "));";

    public OldTasksHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_OLDTASK);
        db.execSQL(CREATE_TABLE_OLDSUBTASKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
