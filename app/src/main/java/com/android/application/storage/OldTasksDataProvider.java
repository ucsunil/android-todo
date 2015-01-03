package com.android.application.storage;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Sunil on 12/31/2014.
 */
public class OldTasksDataProvider extends ContentProvider {

    private static final String TAG = "OldTasksDataProvider";

    private OldTasksHelper db = null;
    public static final String AUTHORITY = "com.android.application.storage.OldTasksDataProvider";

    public static final Uri OLD_TASKS_URI =
            Uri.parse("content://com.android.application.storage.OldTasksDataProvider/oldtasks");
    public static final Uri OLD_SUBTASKS_URI =
            Uri.parse("content://com.android.application.storage.OldTasksDataProvider/oldsubtasks");

    private static final String TABLE_OLD_TASKS = "oldtasks";
    private static final String TABLE_OLD_SUBTASKS = "oldsubtasks";

    private static final int OLD_TASKS_TABLE = 41;
    private static final int OLD_SUBTASKS_TABLE = 42;

    // Columns in table "oldtasks"
    private static final int CODE_TASK_ID = 51;
    private static final int CODE_DATE = 52;
    private static final int CODE_TASK = 53;
    private static final int CODE_SUBTASKS = 55;
    private static final int CODE_TASK_STATUS = 56;
    private static final int CODE_TASK_DESCRIPTION = 57;

    // Columns in table "oldsubtasks"
    private static final int CODE_SUBTASK_ID = 61;
    private static final int CODE_SUBTASK_TASK_ID = 62;
    private static final int CODE_SUBTASK = 63;
    private static final int CODE_SUBTASK_STATUS = 65;
    private static final int CODE_SUBTASK_DESCRIPTION = 66;

    private static final UriMatcher MATCHER;

    static {
        MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

        MATCHER.addURI(AUTHORITY, TABLE_OLD_TASKS, OLD_TASKS_TABLE);
        MATCHER.addURI(AUTHORITY, TABLE_OLD_SUBTASKS, OLD_SUBTASKS_TABLE);

        MATCHER.addURI(AUTHORITY, "oldtasks/#", CODE_TASK_ID);
        MATCHER.addURI(AUTHORITY, "oldtasks/date", CODE_DATE);
        MATCHER.addURI(AUTHORITY, "oldtasks/task", CODE_TASK);
        MATCHER.addURI(AUTHORITY, "oldtasks/#", CODE_TASK_STATUS);
        MATCHER.addURI(AUTHORITY, "oldtasks/description", CODE_TASK_DESCRIPTION);

        MATCHER.addURI(AUTHORITY, "oldsubtasks/#", CODE_SUBTASK_ID);
        MATCHER.addURI(AUTHORITY, "oldsubtasks/#", CODE_SUBTASK_TASK_ID);
        MATCHER.addURI(AUTHORITY, "oldsubtasks/subtask", CODE_SUBTASK);
        MATCHER.addURI(AUTHORITY, "oldsubtasks/#", CODE_SUBTASK_STATUS);
        MATCHER.addURI(AUTHORITY, "oldsubtasks/description", CODE_SUBTASK_DESCRIPTION);
    }
    @Override
    public boolean onCreate() {
        db = new OldTasksHelper(getContext());

        return ((db == null) ? false : true);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        Cursor cursor = null;
        int match = MATCHER.match(uri);
        switch(match) {
            case 41:
                qb.setTables(TABLE_OLD_TASKS);
                cursor = qb.query(db.getReadableDatabase(), projection, selection, selectionArgs, null, null, sort);
                return cursor;
            case 42:
                qb.setTables(TABLE_OLD_SUBTASKS);
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
    public Uri insert(Uri uri, ContentValues values) {
        Uri insertedPosition = null;
        long rowId = 0;
        int match = MATCHER.match(uri);
        switch (match) {
            case OLD_TASKS_TABLE:
                rowId = db.getWritableDatabase().insert(TABLE_OLD_TASKS, null, values);
                break;
            case OLD_SUBTASKS_TABLE:
                rowId = db.getWritableDatabase().insert(TABLE_OLD_SUBTASKS, null, values);
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
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = MATCHER.match(uri);
        int count = 0;
        switch (match) {
            case OLD_TASKS_TABLE:
                count = db.getWritableDatabase().delete(TABLE_OLD_TASKS, selection, selectionArgs);
                break;
            case OLD_SUBTASKS_TABLE:
                count = db.getWritableDatabase().delete(TABLE_OLD_SUBTASKS, selection, selectionArgs);
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
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = MATCHER.match(uri);
        int count = 0;
        switch (match) {
            case OLD_TASKS_TABLE:
                count = db.getWritableDatabase().update(TABLE_OLD_TASKS, values, selection, selectionArgs);
                break;
            case OLD_SUBTASKS_TABLE:
                count = db.getWritableDatabase().update(TABLE_OLD_SUBTASKS, values, selection, selectionArgs);
                break;
            default:
                // Do nothing
        }
        if(count <= 0) {
            Log.v(TAG, "No rows were modified!!");
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

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        SQLiteDatabase database = db.getWritableDatabase();
        ContentProviderResult[] results = new ContentProviderResult[operations.size()];
        try {
            database.beginTransaction();
            for(int i = 0; i < operations.size(); i++) {
                ContentProviderOperation operation = operations.get(i);
                results[i] = operation.apply(this, results, i);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        return results;
    }
}
