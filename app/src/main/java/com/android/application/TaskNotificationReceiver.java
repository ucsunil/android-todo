package com.android.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.application.datamodels.Task;

public class TaskNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(GlobalData.THIRTY_MINUTE_BROADCAST)) {
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

    private void thirtyMinuteAlert(Task task) {

    }

    private void fifteenMinuteAlert(Task task) {

    }

    private void fiveMinuteAlert(Task task) {

    }

    private void nowAlert(Task task) {

    }
}
