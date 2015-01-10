package com.android.application.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.android.application.GlobalData;
import com.android.application.services.TaskAlertService;
import com.android.application.services.TasksRemoveIntentService;

/**
 * Created by Sunil on 1/2/2015.
 */
public class PollReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(GlobalData.hasAppRunBefore(context)) {
            scheduleDeleteTaskAlarm(context);
        }
        context.startService(new Intent(context, TaskAlertService.class));
    }

    public static void scheduleDeleteTaskAlarm(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TasksRemoveIntentService.class);
        boolean deleteTaskalarmUp = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE) != null;
        if(deleteTaskalarmUp) {
            // All the alarms are up and do not have to be scheduled
            return;
        }
        // The DeleteTaskAlarm needs to be set
        PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), GlobalData.INTERVAL_DAY, pi);
    }
}
