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
    private final String toDo_ten = "Due in 10 minutes: ";
    private final String toDo_now = "Due now: ";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(this.context == null) {
            this.context = context;
        }
        pendingIntent = setupTaskStackBuilder(intent);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String action = intent.getAction();
        if(action.equals(GlobalData.TEN_MINUTE_BROADCAST)) {
            Task  task = intent.getParcelableExtra("task");
            tenMinuteAlert(task);
        } else if(action.equals(GlobalData.NOW_BROADCAST)) {
            Task task = intent.getParcelableExtra("task");
            nowAlert(task);
        } else {
            // This is unrecognized
        }
    }

    /**
     * This method sets up the back stack for the activities that should exist when the
     * ViewActivity is started from the notification and returns the PendingIntent
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

    private void tenMinuteAlert(Task task) {
        Notification.Builder notification = new Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.perm_group_system_clock)
                .setContentTitle(toDo_ten + task.getTask())
                .setContentText(task.getDescription())
                .setAutoCancel(true);
        notificationManager.notify(0, notification.build());
    }

    private void nowAlert(Task task) {
        Notification.Builder notification = new Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.perm_group_system_clock)
                .setContentTitle(toDo_now + task.getTask())
                .setContentText(task.getDescription())
                .setAutoCancel(true);
        notificationManager.notify(0, notification.build());
    }
}
