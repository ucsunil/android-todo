package com.android.application.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.IBinder;
import android.os.SystemClock;

import com.android.application.GlobalData;
import com.android.application.receivers.TaskNotificationReceiver;
import com.android.application.datamodels.Task;
import com.android.application.storage.DataProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * This service runs a query on the Tasks table every 500ms and monitors
 * the ResultSet for any upcoming tasks in a separate thread and then sends
 * a broadcast for upcoming tasks.
 */
public class TaskAlertService extends Service {

    TaskNotificationReceiver taskNotificationReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        taskNotificationReceiver = new TaskNotificationReceiver();
        registerReceiver(taskNotificationReceiver, new IntentFilter(GlobalData.THIRTY_MINUTE_BROADCAST));
        registerReceiver(taskNotificationReceiver, new IntentFilter(GlobalData.FIFTEEN_MINUTE_BROADCAST));
        registerReceiver(taskNotificationReceiver, new IntentFilter(GlobalData.FIVE_MINUTE_BROADCAST));
        registerReceiver(taskNotificationReceiver, new IntentFilter(GlobalData.NOW_BROADCAST));

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class MonitorThread extends Thread {

        @Override
        public void run() {
            // The following are the sets which maintain information on whether the user has been
            // notified or not of the upcoming task. If the taskId of a task is present in the set,
            // then it means that the user has been notified for that minute mark
            Set<Integer> thirtyMinuteSet = new HashSet<Integer>();
            Set<Integer> fifteenMinuteSet = new HashSet<Integer>();
            Set<Integer> fiveMinuteSet = new HashSet<Integer>();
            Set<Integer> nowSet = new HashSet<Integer>();

            boolean shouldRun = true;
            while (shouldRun) {
                Cursor cursor = getContentResolver().query(DataProvider.TASKS_URI, null, null, null, null);
                if(cursor.getCount() == 0) {
                    SystemClock.sleep(500);
                    continue;
                }
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
                    if(difference >= -(1000 * 30 * 60 + 2000) && difference <= -(1000 * 30 * 60 - 2000)) {
                        Integer taskId = cursor.getInt(0);
                        Task task = new Task();
                        boolean bool;
                        task.setTaskId(taskId);
                        task.setDate(cursor.getString(1));
                        task.setTime(cursor.getString(2));
                        task.setTask(cursor.getString(3));
                        bool = cursor.getInt(4) == 0 ? false : true;
                        task.setHasNote(bool);
                        bool = cursor.getInt(5) == 0 ? false : true;
                        task.setHasSubtasks(bool);
                        bool = cursor.getInt(6) == 0? false : true;
                        task.setStatus(bool);
                        task.setDescription(cursor.getString(7));
                        task.setBooleans();
                        if(!thirtyMinuteSet.contains(taskId)) {
                            thirtyMinuteSet.add(taskId);
                            notifyThirtyMinutes(task);
                        }
                    }
                    if(difference >= -(1000*15*60+2000) && difference <= -(1000*15*60-2000)) {
                        Integer taskId = cursor.getInt(0);
                        Task task = new Task();
                        boolean bool;
                        task.setTaskId(taskId);
                        task.setDate(cursor.getString(1));
                        task.setTime(cursor.getString(2));
                        task.setTask(cursor.getString(3));
                        bool = cursor.getInt(4) == 0 ? false : true;
                        task.setHasNote(bool);
                        bool = cursor.getInt(5) == 0 ? false : true;
                        task.setHasSubtasks(bool);
                        bool = cursor.getInt(6) == 0? false : true;
                        task.setStatus(bool);
                        task.setDescription(cursor.getString(7));
                        task.setBooleans();
                        if(!fifteenMinuteSet.contains(taskId)) {
                            fifteenMinuteSet.add(taskId);
                            notifyFifteenMinutes(task);
                        }
                    }
                    if(difference >= -(1000*5*60+2000) && difference <= -(1000*5*60-2000)) {
                        Integer taskId = cursor.getInt(0);
                        Task task = new Task();
                        boolean bool;
                        task.setTaskId(taskId);
                        task.setDate(cursor.getString(1));
                        task.setTime(cursor.getString(2));
                        task.setTask(cursor.getString(3));
                        bool = cursor.getInt(4) == 0 ? false : true;
                        task.setHasNote(bool);
                        bool = cursor.getInt(5) == 0 ? false : true;
                        task.setHasSubtasks(bool);
                        bool = cursor.getInt(6) == 0? false : true;
                        task.setStatus(bool);
                        task.setDescription(cursor.getString(7));
                        task.setBooleans();
                        if(!fiveMinuteSet.contains(taskId)) {
                            fiveMinuteSet.add(taskId);
                            notifyFiveMinutes(task);
                        }
                    }
                    if(difference >= -(2000) && difference <= -(-2000)) {
                        Integer taskId = cursor.getInt(0);
                        Task task = new Task();
                        boolean bool;
                        task.setTaskId(taskId);
                        task.setDate(cursor.getString(1));
                        task.setTime(cursor.getString(2));
                        task.setTask(cursor.getString(3));
                        bool = cursor.getInt(4) == 0 ? false : true;
                        task.setHasNote(bool);
                        bool = cursor.getInt(5) == 0 ? false : true;
                        task.setHasSubtasks(bool);
                        bool = cursor.getInt(6) == 0? false : true;
                        task.setStatus(bool);
                        task.setDescription(cursor.getString(7));
                        task.setBooleans();
                        if(!nowSet.contains(taskId)) {
                            nowSet.add(taskId);
                            notifyNow(task);
                        }
                    }
                    if(cursor.isLast()) {
                        isLast = true;
                    }
                    cursor.moveToNext();
                }
                cursor.close();
                SystemClock.sleep(500);
            }
        }

        private void notifyThirtyMinutes(Task task) {
            Intent thirtyMinuteIntent = new Intent(GlobalData.THIRTY_MINUTE_BROADCAST);
            thirtyMinuteIntent.putExtra("task", task);
            sendBroadcast(thirtyMinuteIntent);
        }

        private void notifyFifteenMinutes(Task task) {
            Intent fifteenMinuteIntent = new Intent(GlobalData.FIFTEEN_MINUTE_BROADCAST);
            fifteenMinuteIntent.putExtra("task", task);
            sendBroadcast(fifteenMinuteIntent);
        }

        private void notifyFiveMinutes(Task task) {
            Intent fiveMinuteIntent = new Intent(GlobalData.FIVE_MINUTE_BROADCAST);
            fiveMinuteIntent.putExtra("task", task);
            sendBroadcast(fiveMinuteIntent);
        }

        private void notifyNow(Task task) {
            Intent nowIntent = new Intent(GlobalData.NOW_BROADCAST);
            nowIntent.putExtra("task", task);
            sendBroadcast(nowIntent);
        }
    }
}
