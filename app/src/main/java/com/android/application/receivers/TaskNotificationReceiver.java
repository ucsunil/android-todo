package com.android.application.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.application.GlobalData;
import com.android.application.HomeActivity;
import com.android.application.R;
import com.android.application.activities.TasksDisplayActivity;
import com.android.application.activities.ViewActivity;
import com.android.application.datamodels.Task;

public class TaskNotificationReceiver extends BroadcastReceiver {

    private Context context;
    private NotificationManager notificationManager;
    private TaskStackBuilder stackBuilder;
    Intent homeIntent, taskListIntent, taskIntent;
    private PendingIntent pendingIntent;
    private final String toDo_thirty = "Due in 30 minutes: ";
    private final String toDo_fifteen = "Due in 15 minutes: ";
    private final String toDo_five = "Due in 5 minutes: ";
    private final String toDo_now = "Due now: ";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(this.context == null) {
            this.context = context;
        }
        pendingIntent = setupTaskStackBuilder(intent);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String action = intent.getAction();
        if(action.equals(GlobalData.THIRTY_MINUTE_BROADCAST)) {
            Log.d("BR", "Thirty minute call");
            Task task = intent.getParcelableExtra("task");
            thirtyMinuteAlert(task);
        } else if(action.equals(GlobalData.FIFTEEN_MINUTE_BROADCAST)) {
            Task  task = intent.getParcelableExtra("task");
            fifteenMinuteAlert(task);
        } else if(action.equals(GlobalData.FIVE_MINUTE_BROADCAST)) {
            Task task = intent.getParcelableExtra("task");
            fiveMinuteAlert(task);
        } else if(action.equals(GlobalData.NOW_BROADCAST)) {
            Task task = intent.getParcelableExtra("task");
            nowAlert(task);
        } else {
            // This is unrecognized
        }
    }

    /**
     * This method sets up the back stack for the activities that should exist when the
     * ViewActivity is started from the notification and return the PendingIntent
     * obtained from this stack builder as the result.
     */
    private PendingIntent setupTaskStackBuilder(Intent intent) {
        stackBuilder = TaskStackBuilder.create(context);
        homeIntent = new Intent(context, HomeActivity.class);
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(homeIntent);
        taskListIntent = new Intent(context, TasksDisplayActivity.class);
        stackBuilder.addParentStack(TasksDisplayActivity.class);
        stackBuilder.addNextIntent(taskListIntent);
        taskIntent = new Intent(context, ViewActivity.class);

        // Add the Task details to the ViewActivity class
        taskIntent.putExtra("viewWhat", "viewTask");
        Bundle bundle = new Bundle();
        Task task = intent.getParcelableExtra("task");
        bundle.putInt("task_id", task.getTaskId());
        bundle.putString("date", task.getDate());
        bundle.putString("time", task.getTime());
        bundle.putString("task", task.getTask());
        bundle.putBoolean("has_note", task.getHasNote());
        bundle.putBoolean("task_status", task.getStatus());
        bundle.putString("description", task.getDescription());
        bundle.putBoolean("fromNotification", true);
        taskIntent.putExtras(bundle);
        stackBuilder.addParentStack(ViewActivity.class);
        stackBuilder.addNextIntent(taskIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private void thirtyMinuteAlert(Task task) {
        Log.d("BR", "Thirty minute notification");
        Notification.Builder notification = new Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.perm_group_system_clock)
                .setContentTitle(toDo_thirty + task.getTask())
                .setContentText(task.getDescription());
        notificationManager.notify(0, notification.build());
    }

    private void fifteenMinuteAlert(Task task) {
        Notification.Builder notification = new Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.perm_group_system_clock)
                .setContentTitle(toDo_fifteen + task.getTask())
                .setContentText(task.getDescription());
        notificationManager.notify(0, notification.build());
    }

    private void fiveMinuteAlert(Task task) {
        Notification.Builder notification = new Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.perm_group_system_clock)
                .setContentTitle(toDo_five + task.getTask())
                .setContentText(task.getDescription());
        notificationManager.notify(0, notification.build());
    }

    private void nowAlert(Task task) {
        Notification.Builder notification = new Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.perm_group_system_clock)
                .setContentTitle(toDo_now + task.getTask())
                .setContentText(task.getDescription());
        notificationManager.notify(0, notification.build());
    }
}
