package com.android.application.storage;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by umonssu on 10/13/14.
 */
public class DataProvider extends ContentProvider {

    private DatabaseHelper db = null;
    public static final String AUTHORITY = "com.android.application.storage.DataProvider";

    public static final Uri TASKS_URI =
            Uri.parse("content://com.android.application.storage.DataProvider/tasks");
    public static final Uri SUBTASKS_URI =
            Uri.parse("content://com.android.application.storage.DataProvider/subtasks");
    public static final Uri NOTES_URI =
            Uri.parse("content://com.android.application.storage.DataProvider/notes");

    private static final String TABLE_TASKS = "tasks";
    private static final String TABLE_SUBTASKS = "subtasks";
    private static final String TABLE_NOTES = "notes";

    private static final int TASKS_TABLE = 1;
    private static final int SUBTASKS_TABLE = 2;
    private static final int NOTES_TABLE = 3;

    // Columns in table "tasks"
    private static final String TASK_ID = "task_id";
    private static final String DATE = "date";
    private static final String TASK = "task";
    private static final String HAS_NOTE = "has_note";
    private static final String SUBTASKS = "subtasks";
    private static final String TASK_STATUS = "task_status";
    private static final String TASK_DESCRIPTION = "description";

    private static final int CODE_TASK_ID = 11;
    private static final int CODE_DATE = 12;
    private static final int CODE_TASK = 13;
    private static final int CODE_HAS_NOTE = 14;
    private static final int CODE_SUBTASKS = 15;
    private static final int CODE_TASK_STATUS = 16;
    private static final int CODE_TASK_DESCRIPTION = 17;

    // Columns in table "subtasks"
    private static final String SUBTASK_ID = "subtask_id";
    private static final String SUBTASK = "subtask";
    private static final String SUBTASK_HAS_NOTE = "subtask_has_note";
    private static final String SUBTASK_STATUS = "subtask_status";
    private static final String SUBTASK_DESCRIPTION = "subtask_description";

    private static final int CODE_SUBTASK_ID = 21;
    private static final int CODE_SUBTASK_TASK_ID = 22;
    private static final int CODE_SUBTASK = 23;
    private static final int CODE_SUBTASK_HAS_NOTE = 24;
    private static final int CODE_SUBTASK_STATUS = 25;
    private static final int CODE_SUBTASK_DESCRIPTION = 26;

    // Columns in table "notes"
    private static final String NOTE_ID = "note_id";
    private static final String NOTE = "note";

    private static final int CODE_NOTE_ID = 31;
    private static final int CODE_NOTE_TASK_ID = 32;
    private static final int CODE_NOTE_SUBTASK_ID = 33;
    private static final int CODE_NOTE = 34;

    private static final UriMatcher MATCHER;

    static {
        MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

        MATCHER.addURI(AUTHORITY, TABLE_TASKS, TASKS_TABLE);
        MATCHER.addURI(AUTHORITY, TABLE_SUBTASKS, SUBTASKS_TABLE);
        MATCHER.addURI(AUTHORITY, TABLE_NOTES, NOTES_TABLE);

        MATCHER.addURI(AUTHORITY, "tasks/#", CODE_TASK_ID);
        MATCHER.addURI(AUTHORITY, "tasks/date", CODE_DATE);
        MATCHER.addURI(AUTHORITY, "tasks/task", CODE_TASK);
        MATCHER.addURI(AUTHORITY, "tasks/#", CODE_HAS_NOTE);
        MATCHER.addURI(AUTHORITY, "tasks/#", CODE_TASK_STATUS);
        MATCHER.addURI(AUTHORITY, "tasks/description", CODE_TASK_DESCRIPTION);

        MATCHER.addURI(AUTHORITY, "subtasks/#", CODE_SUBTASK_ID);
        MATCHER.addURI(AUTHORITY, "subtasks/#", CODE_SUBTASK_TASK_ID);
        MATCHER.addURI(AUTHORITY, "subtasks/subtask", CODE_SUBTASK);
        MATCHER.addURI(AUTHORITY, "subtasks/subtask_has_note", CODE_SUBTASK_HAS_NOTE);
        MATCHER.addURI(AUTHORITY, "subtasks/#", CODE_SUBTASK_STATUS);
        MATCHER.addURI(AUTHORITY, "subtasks/description", CODE_SUBTASK_DESCRIPTION);

        MATCHER.addURI(AUTHORITY, "notes/#", CODE_NOTE_ID);
        MATCHER.addURI(AUTHORITY, "notes/#", CODE_NOTE_TASK_ID);
        MATCHER.addURI(AUTHORITY, "notes/#", CODE_NOTE_SUBTASK_ID);
        MATCHER.addURI(AUTHORITY, "notes/note", CODE_NOTE);
    }

    @Override
    public boolean onCreate() {
        db = new DatabaseHelper(getContext());

        return ((db == null) ? false : true);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        Cursor cursor = null;
        int match = MATCHER.match(uri);
        switch(match) {
            case 1:
                qb.setTables(TABLE_TASKS);
                cursor = qb.query(db.getReadableDatabase(), projection, selection, selectionArgs, null, null, sort);
                return cursor;
            case 2:
                qb.setTables(TABLE_SUBTASKS);
                cursor = qb.query(db.getReadableDatabase(), projection, selection, selectionArgs, null, null, sort);
                return cursor;
            default:
                // Do nothing
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        throw new SQLException("The data being requested is not valid");
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Uri insertedPosition = null;
        long rowId = 0;
        int match = MATCHER.match(uri);
        switch (match) {
            case TASKS_TABLE:
                rowId = db.getWritableDatabase().insert(TABLE_TASKS, null, contentValues);
                break;
            case SUBTASKS_TABLE:
                rowId = db.getWritableDatabase().insert(TABLE_SUBTASKS, null, contentValues);
                break;
            case NOTES_TABLE:
                rowId = db.getWritableDatabase().insert(TABLE_NOTES, null, contentValues);
                break;
            default:
                // Do nothing
        }
        if(rowId <= 0) {
            throw new SQLException("Error in writing to database!!");
        } else if(rowId > 0) {
            insertedPosition = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(insertedPosition, null);
        }
        return insertedPosition;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        int match = MATCHER.match(uri);
        int count = 0;
        switch (match) {
            case TASKS_TABLE:
                count = db.getWritableDatabase().delete(TABLE_TASKS, where, whereArgs);
                break;
            case SUBTASKS_TABLE:
                count = db.getWritableDatabase().delete(TABLE_SUBTASKS, where, whereArgs);
                break;
            case NOTES_TABLE:
                count = db.getWritableDatabase().delete(TABLE_NOTES, where, whereArgs);
                break;

        }
        if(count < 0) {
            throw new SQLException("Error while deleting!!");
        } else {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] selectionArgs) {
        Uri updatedPosition = null;
        int match = MATCHER.match(uri);
        int count = 0;
        switch (match) {
            case TASKS_TABLE:
                count = db.getWritableDatabase().update(TABLE_TASKS, values, where, selectionArgs);
                break;
            case SUBTASKS_TABLE:
                count = db.getWritableDatabase().update(TABLE_SUBTASKS, values, where, selectionArgs);
                break;
            case NOTES_TABLE:
                count = db.getWritableDatabase().update(TABLE_NOTES, values, where, selectionArgs);
                break;
            default:
                // Do nothing
        }
        if(count <= 0) {
            throw new SQLException("Error in updating!!");
        } else {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] contentValues) {
        SQLiteDatabase database = db.getWritableDatabase();
        int count = 0;
        try {
            database.beginTransaction();
            for(ContentValues values : contentValues) {
                Uri resultUri = insert(uri, values);
                if(resultUri != null) {
                    count++;
                } else {
                    count = 0;
                    throw new SQLException("Error in bulk insert!!");
                }
            }
            database.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri, null);
        } finally {
            database.endTransaction();
        }
        return count;
    }
}
