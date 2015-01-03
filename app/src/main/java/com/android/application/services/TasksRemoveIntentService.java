package com.android.application.services;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;

import com.android.application.GlobalData;
import com.android.application.storage.DataProvider;
import com.android.application.storage.OldTasksDataProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This service is used to periodically go through the database tables and remove
 * old tasks. It first moves completed tasks older than 1 day and incomplete tasks
 * older than 3 days to the old tasks database tables. It then deletes tasks that are
 * older than 15 days from the old tasks database.
 *
 * Created by Sunil on 1/2/2015.
 */
public class TasksRemoveIntentService extends IntentService {

    Cursor cursor, subtasksCursor;

    public TasksRemoveIntentService() {
        super("TasksRemoveIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        /**
         * The following sections searches the tasks database to determine which tasks
         * need to be moved to the oldtasks database
         */
        cursor = getContentResolver().query(DataProvider.TASKS_URI, null, null, null, null);
        if(cursor.getCount() > 0) {
            ArrayList<ContentProviderOperation> operationTasks = new ArrayList<ContentProviderOperation>();
            ArrayList<ContentProviderOperation> operationSubtasks = new ArrayList<ContentProviderOperation>();
            cursor.moveToFirst();
            boolean isLast = false;
            ArrayList<ContentValues> tableOldTaskValues = new ArrayList<ContentValues>();
            ArrayList<ContentValues> tableOldSubtaskValues = new ArrayList<ContentValues>();
            while(!isLast) {
                String date = cursor.getString(1);
                String time = cursor.getString(2);

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                Date taskDate = null;
                try {
                    taskDate = sdf.parse(date + " " + time);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                long tDate = taskDate.getTime();
                Date nowDate = new Date();
                long nDate = nowDate.getTime();

                long difference = nDate - tDate;
                // Find out if the status of the task is complete
                boolean bool = cursor.getInt(6) == 0? false : true;
                if(difference >= (GlobalData.INTERVAL_DAY) && bool) {
                    ContentValues taskValues = new ContentValues();
                    taskValues.put("task_id", cursor.getInt(0));
                    taskValues.put("date", cursor.getString(1));
                    taskValues.put("time", cursor.getString(2));
                    taskValues.put("task", cursor.getString(3));
                    taskValues.put("subtasks", cursor.getInt(5));
                    taskValues.put("task_status", cursor.getInt(6));
                    taskValues.put("description", cursor.getInt(7));

                    // This section is to queue this task to be deleted from the
                    // TASKS table
                    String selection = "task_id=?";
                    String[] selectionArgs = {String.valueOf(cursor.getInt(0))};
                    operationTasks.add(ContentProviderOperation.newDelete(DataProvider.TASKS_URI)
                            .withSelection(selection, selectionArgs)
                            .withYieldAllowed(false).build());

                    tableOldTaskValues.add(taskValues);

                    subtasksCursor = getContentResolver().query(DataProvider.SUBTASKS_URI, null, selection, selectionArgs, null);
                    if(subtasksCursor.getCount() > 0) {
                        subtasksCursor.moveToFirst();
                        boolean isLastSubtask = false;
                        while(!isLastSubtask) {
                            ContentValues subtaskValues = new ContentValues();
                            subtaskValues.put("subtask_id", subtasksCursor.getInt(0));
                            subtaskValues.put("task_id", subtasksCursor.getInt(1));
                            subtaskValues.put("subtask", subtasksCursor.getString(2));
                            subtaskValues.put("subtask_status", subtasksCursor.getInt(4));
                            subtaskValues.put("subtask_description", subtasksCursor.getString(5));

                            // This section is to queue up these subtasks to be deleted from the
                            // SUBTASKS table
                            operationSubtasks.add(ContentProviderOperation.newDelete(DataProvider.SUBTASKS_URI)
                                    .withSelection(selection, selectionArgs)
                                    .withYieldAllowed(false).build());

                            tableOldSubtaskValues.add(subtaskValues);

                            if(subtasksCursor.isLast()) {
                                isLastSubtask = true;
                            }
                            subtasksCursor.moveToNext();
                        }
                        subtasksCursor.close();
                    }
                }
                if(difference >= (GlobalData.INTERVAL_THREE_DAYS) && !bool) {
                    ContentValues taskValues = new ContentValues();
                    taskValues.put("task_id", cursor.getInt(0));
                    taskValues.put("date", cursor.getString(1));
                    taskValues.put("time", cursor.getString(2));
                    taskValues.put("task", cursor.getString(3));
                    taskValues.put("subtasks", cursor.getInt(5));
                    taskValues.put("task_status", cursor.getInt(6));
                    taskValues.put("description", cursor.getInt(7));

                    // This section is to queue this task to be deleted from the
                    // TASKS table
                    String selection = "task_id=?";
                    String[] selectionArgs = {String.valueOf(cursor.getInt(0))};
                    operationTasks.add(ContentProviderOperation.newDelete(DataProvider.TASKS_URI)
                            .withSelection(selection, selectionArgs)
                            .withYieldAllowed(false).build());

                    tableOldTaskValues.add(taskValues);

                    subtasksCursor = getContentResolver().query(DataProvider.SUBTASKS_URI, null, selection, selectionArgs, null);
                    if(subtasksCursor.getCount() > 0) {
                        subtasksCursor.moveToFirst();
                        boolean isLastSubtask = false;
                        while(!isLastSubtask) {
                            ContentValues subtaskValues = new ContentValues();
                            subtaskValues.put("subtask_id", subtasksCursor.getInt(0));
                            subtaskValues.put("task_id", subtasksCursor.getInt(1));
                            subtaskValues.put("subtask", subtasksCursor.getString(2));
                            subtaskValues.put("subtask_status", subtasksCursor.getInt(4));
                            subtaskValues.put("subtask_description", subtasksCursor.getString(5));

                            // This section is to queue up these subtasks to be deleted from the
                            // SUBTASKS table
                            operationSubtasks.add(ContentProviderOperation.newDelete(DataProvider.SUBTASKS_URI)
                                    .withSelection(selection, selectionArgs)
                                    .withYieldAllowed(false).build());

                            tableOldSubtaskValues.add(subtaskValues);

                            if(subtasksCursor.isLast()) {
                                isLastSubtask = true;
                            }
                            subtasksCursor.moveToNext();
                        }
                        subtasksCursor.close();
                    }
                }
                if(cursor.isLast()) {
                    isLast = true;

                    // The following try/catch block does a delete on the TASKS and SUBTASKS tables
                    try {
                        getContentResolver().applyBatch(DataProvider.AUTHORITY, operationTasks);
                        getContentResolver().applyBatch(DataProvider.AUTHORITY, operationSubtasks);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    } catch (OperationApplicationException ex) {
                        ex.printStackTrace();
                    }

                    // The following code inserts the old data into the OLD_TASKS and OLD_SUBTASKS table
                    getContentResolver().bulkInsert(OldTasksDataProvider.OLD_TASKS_URI,
                            tableOldTaskValues.toArray(new ContentValues[tableOldTaskValues.size()]));
                    getContentResolver().bulkInsert(OldTasksDataProvider.OLD_SUBTASKS_URI,
                            tableOldSubtaskValues.toArray(new ContentValues[tableOldSubtaskValues.size()]));
                }
                cursor.moveToNext();
            }
        }
        cursor.close();

        /**
         * The following section searches through the oldtasks database to determine which tasks
         * need to be deleted. Tasks older than 15 days are deleted irrespective of whether they
         * are complete or not.
         */
        cursor = getContentResolver().query(OldTasksDataProvider.OLD_TASKS_URI, null, null, null, null);
        if(cursor.getCount() > 0) {
            ArrayList<ContentProviderOperation> operationTasks = new ArrayList<ContentProviderOperation>();
            ArrayList<ContentProviderOperation> operationSubtasks = new ArrayList<ContentProviderOperation>();
            cursor.moveToFirst();
            boolean isLast = false;
            while(!isLast) {
                String date = cursor.getString(1);
                String time = cursor.getString(2);

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                Date taskDate = null;
                try {
                    taskDate = sdf.parse(date + " " + time);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                long tDate = taskDate.getTime();
                Date nowDate = new Date();
                long nDate = nowDate.getTime();

                long difference = nDate - tDate;
                // If more than 15 days has elapsed since the task's due time, then
                // delete the task and all its subtasks
                if(difference >= GlobalData.INTERVAL_FIFTEEN_DAYS) {
                    // This section is to queue this task to be deleted from the
                    // TASKS table
                    String selection = "task_id=?";
                    String[] selectionArgs = {String.valueOf(cursor.getInt(0))};
                    operationTasks.add(ContentProviderOperation.newDelete(DataProvider.TASKS_URI)
                            .withSelection(selection, selectionArgs)
                            .withYieldAllowed(false).build());

                    subtasksCursor = getContentResolver().query(DataProvider.SUBTASKS_URI, null, selection, selectionArgs, null);
                    if(subtasksCursor.getCount() > 0) {
                        subtasksCursor.moveToFirst();
                        boolean isLastSubtask = false;
                        while(!isLastSubtask) {
                            // This section is to queue up these subtasks to be deleted from the
                            // SUBTASKS table
                            operationSubtasks.add(ContentProviderOperation.newDelete(DataProvider.SUBTASKS_URI)
                                    .withSelection(selection, selectionArgs)
                                    .withYieldAllowed(false).build());

                            if(subtasksCursor.isLast()) {
                                isLastSubtask = true;
                            }
                            subtasksCursor.moveToNext();
                        }
                        subtasksCursor.close();
                    }
                }

                if(cursor.isLast()) {
                    isLast = true;

                    // The following try/catch block does a delete on the TASKS and SUBTASKS tables
                    try {
                        getContentResolver().applyBatch(OldTasksDataProvider.AUTHORITY, operationTasks);
                        getContentResolver().applyBatch(OldTasksDataProvider.AUTHORITY, operationSubtasks);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    } catch (OperationApplicationException ex) {
                        ex.printStackTrace();
                    }
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
    }
}
