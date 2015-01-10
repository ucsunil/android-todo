package com.android.application.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.android.application.GlobalData;
import com.android.application.HomeActivity;
import com.android.application.activities.TasksDisplayActivity;
import com.android.application.activities.ViewActivity;
import com.android.application.datamodels.Task;
import com.android.application.receivers.TaskNotificationReceiver;
import com.android.application.storage.DataProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskNotifyService extends Service {

    private IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public TaskNotifyService getService() {
            return TaskNotifyService.this;
        }
    }

    public void scheduleTaskAlarms(String taskId) {
        CreateAlarmsThread alarmsThread = new CreateAlarmsThread(taskId);
        alarmsThread.start();
    }

    private class CreateAlarmsThread extends Thread {

        private String taskId;
        private Cursor cursor;
        Intent homeIntent, taskListIntent, taskIntent;
        TaskStackBuilder stackBuilder;
        long nDate;

        public CreateAlarmsThread(String taskId) {
            this.taskId = taskId;
            Date nowDate = new Date();
            nDate = nowDate.getTime();
        }

        @Override
        public void run() {
            String selection = "task_id=?";
            String[] selectionArgs = {taskId};
            cursor = getContentResolver().query(DataProvider.TASKS_URI, null, selection, selectionArgs, null);
            cursor.moveToFirst();
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
            scheduleTenMinuteAlarm(cursor, tDate);
            scheduleNowAlarm(cursor, tDate);
        }

        /**
         * This method first calculates the time ten minutes prior
         * to the task and then sets an alarm for that time.
         *
         * @param cursor the Cursor object with the result set
         *               containing data about the task
         * @param tDate  the actual time of the task
         */
        private void scheduleTenMinuteAlarm(Cursor cursor, long tDate) {
            if ((tDate - 10 * 60 * 1000) <= nDate) {
                // Means the ten minute mark has already elapsed
                // Do not do anything
                return;
            }
            long tenMinutePrior = tDate - (10 * 60 * 1000);
            AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Task task = createTaskFromCursor(cursor);
            Intent intent = new Intent(getApplicationContext(), TaskNotificationReceiver.class);
            intent.setAction(GlobalData.TEN_MINUTE_BROADCAST);
            intent.putExtra("task", task);
            PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            manager.setExact(AlarmManager.RTC_WAKEUP, tenMinutePrior, pi);
        }

        /**
         * This method sets an alarm for the time the task is
         * supposed to start.
         *
         * @param cursor the Cursor object with the result set
         *               containing data about the task
         * @param tDate  the actual time of the task
         */
        private void scheduleNowAlarm(Cursor cursor, long tDate) {
            if (tDate <= nDate) {
                // Means the task start time has already elapsed
                // This should only happen in extreme corner cases
                // Do not do anything
                return;
            }
            AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Task task = createTaskFromCursor(cursor);
            Intent intent = new Intent(getApplicationContext(), TaskNotificationReceiver.class);
            intent.setAction(GlobalData.NOW_BROADCAST);
            intent.putExtra("task", task);
            PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            manager.setExact(AlarmManager.RTC_WAKEUP, tDate, pi);
        }

        private Task createTaskFromCursor(Cursor cursor) {
            cursor.moveToFirst();
            Task task = new Task();
            boolean bool;
            task.setTaskId(Integer.valueOf(taskId));
            task.setDate(cursor.getString(1));
            task.setTime(cursor.getString(2));
            task.setTask(cursor.getString(3));
            bool = cursor.getInt(4) == 0 ? false : true;
            task.setHasNote(bool);
            bool = cursor.getInt(5) == 0 ? false : true;
            task.setHasSubtasks(bool);
            bool = cursor.getInt(6) == 0 ? false : true;
            task.setStatus(bool);
            task.setDescription(cursor.getString(7));
            task.setBooleans();
            return task;
        }

    }
}
