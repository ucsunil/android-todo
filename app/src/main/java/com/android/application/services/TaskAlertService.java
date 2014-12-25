package com.android.application.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.android.application.storage.DataProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TaskAlertService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        MonitorThread thread = new MonitorThread();
        thread.start();
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
                Log.d("Service", "Message from service");
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

                    long difference = Math.abs((tDate-nDate)/(1000*24*60*60));
                    if(difference >= -(1000*30*60+2000) && difference <= -(1000*30*60-2000)) {
                        Integer taskId = cursor.getInt(0);
                        if(!thirtyMinuteSet.contains(taskId)) {
                            thirtyMinuteSet.add(taskId);
                            notifyThirtyMinutes();
                        }
                    }
                    if(difference >= -(1000*15*60+2000) && difference <= -(1000*15*60-2000)) {
                        Integer taskId = cursor.getInt(0);
                        if(!fifteenMinuteSet.contains(taskId)) {
                            fifteenMinuteSet.add(taskId);
                            notifyFifteenMinutes();
                        }
                    }
                    if(difference >= -(1000*5*60+2000) && difference <= -(1000*5*60-2000)) {
                        Integer taskId = cursor.getInt(0);
                        if(!fiveMinuteSet.contains(taskId)) {
                            fiveMinuteSet.add(taskId);
                            notifyFiveMinutes();
                        }
                    }
                    if(difference >= -(2000) && difference <= -(-2000)) {
                        Integer taskId = cursor.getInt(0);
                        if(!nowSet.contains(taskId)) {
                            nowSet.add(taskId);
                            notifyNow();
                        }
                    }
                    if(cursor.isLast()) {
                        isLast = true;
                    }
                    cursor.moveToNext();
                }
                SystemClock.sleep(500);
            }
        }

        private void notifyThirtyMinutes() {

        }

        private void notifyFifteenMinutes() {

        }

        private void notifyFiveMinutes() {

        }

        private void notifyNow() {

        }
    }
}
