package com.android.application.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.android.application.services.TaskNotifyService;

/**
 * Created by umonssu on 10/18/14.
 */
public class DataProviderObserver extends ContentObserver {

    private Context context;
    private TaskNotifyService taskNotifyService;

    public DataProviderObserver(Context context, Handler handler) {
        super(handler);
        this.context = context;
        Intent intent = new Intent(this.context, TaskNotifyService.class);
        this.context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onChange(boolean selfchange, Uri uri) {
        Log.d("DataProviderObserver", uri.toString() + "was where the insert went to!!");
        String taskId = uri.getLastPathSegment().toString();
        taskNotifyService.scheduleTaskAlarms(taskId);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TaskNotifyService.LocalBinder binder = (TaskNotifyService.LocalBinder) service;
            taskNotifyService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
