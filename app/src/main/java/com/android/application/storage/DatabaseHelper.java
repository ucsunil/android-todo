package com.android.application.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by umonssu on 10/12/14.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "data.db";
    private static final String TABLE_TASKS = "tasks";
    private static final String TABLE_SUBTASKS = "subtasks";
    private static final String TABLE_NOTES = "notes";

    // Columns in table "tasks"
    private static final String TASK_ID = "task_id";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String TASK = "task";
    private static final String HAS_NOTE = "has_note";
    private static final String SUBTASKS = "subtasks";
    private static final String TASK_STATUS = "task_status";
    private static final String TASK_DESCRIPTION = "description";

    // Columns in table "subtasks"
    private static final String SUBTASK_ID = "subtask_id";
    private static final String SUBTASK = "subtask";
    private static final String SUBTASK_HAS_NOTE = "subtask_has_note";
    private static final String SUBTASK_STATUS = "subtask_status";
    private static final String SUBTASK_DESCRIPTION = "subtask_description";

    // Columns in table "notes"
    private static final String NOTE_ID = "note_id";
    private static final String NOTE = "note";

    // Create table "tasks"
    private static final String CREATE_TABLE_TASK = "CREATE TABLE " + TABLE_TASKS + " ("
                    + TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DATE + " TEXT, " + TIME + " TEXT, "
                    + TASK + " TEXT, "  + HAS_NOTE + " INTEGER DEFAULT 0, "+ SUBTASKS + " INTEGER DEFAULT 0, "
                    + TASK_STATUS + " INTEGER DEFAULT 0, " + TASK_DESCRIPTION + " TEXT);";

    // Create table "subtasks"
    private static final String CREATE_TABLE_SUBTASKS = "CREATE TABLE " + TABLE_SUBTASKS + " ("
                    + SUBTASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK_ID + " INTEGER NOT NULL, "
                    + SUBTASK + " TEXT NOT NULL, " + SUBTASK_HAS_NOTE + " INTEGER DEFAULT 0, "
                    + SUBTASK_STATUS + " INTEGER DEFAULT 0, " + SUBTASK_DESCRIPTION + " TEXT, "
                    + " FOREIGN KEY(" + TASK_ID + ") REFERENCES " + TABLE_TASKS + "(" + TASK_ID + "));";

    // Create table "notes"
    private static final String CREATE_TABLE_NOTES = "CREATE TABLE " + TABLE_NOTES + " ("
                    + NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK_ID + " INTEGER DEFAULT -1, "
                    + SUBTASK_ID + " INTEGER DEFAULT -1, " + NOTE + " TEXT, "
                    + " FOREIGN KEY(" + TASK_ID + ") REFERENCES " + TABLE_TASKS + "(" + TASK_ID + "), "
                    + " FOREIGN KEY(" + SUBTASK_ID + ") REFERENCES " + TABLE_SUBTASKS + "(" + SUBTASK_ID + "));";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TASK);
        db.execSQL(CREATE_TABLE_SUBTASKS);
        db.execSQL(CREATE_TABLE_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
