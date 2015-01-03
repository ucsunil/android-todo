package com.android.application.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

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
            scheduleAlarms(context);
        }
        context.startService(new Intent(context, TaskAlertService.class));
    }

    public static void scheduleAlarms(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TasksRemoveIntentService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);
        manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), GlobalData.INTERVAL_DAY, pi);
    }
}
