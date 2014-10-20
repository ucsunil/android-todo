package com.android.application.helpers;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

/**
 * Created by umonssu on 10/18/14.
 */
public class DataProviderObserver extends ContentObserver {

    private Context context;

    public DataProviderObserver(Context context, Handler handler) {
        super(handler);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfchange, Uri uri) {
        Log.d("DataProviderObserver", uri.toString());
        // ((TasksDisplayActivity)context).initializeAdapter();
    }
}
