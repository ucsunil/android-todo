package com.android.application;

import android.app.AlarmManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.android.application.receivers.PollReceiver;
import com.android.application.services.TaskAlertService;

/**
 * Created by umonssu on 10/12/14.
 */
public class GlobalData extends Application {

    public static final String EMPTY_DATETIMETITLE = "The Date, Time and Title fields are empty. Please " +
            "set their values and try again.";
    public static final String EMPTY_DATETIME = "The Date and Time fields are empty. Please " +
            "set their values and try again.";
    public static final String EMPTY_TIMETITLE = "The Time and Title fields are empty. Please " +
            "set their values and try again.";
    public static final String EMPTY_DATETITLE = "The Date and Title fields are empty. Please " +
            "set their values and try again.";
    public static final String EMPTY_DATE = "The Date field is empty. Please set its value before continuing";
    public static final String EMPTY_TIME = "The Time field is empty. Please set its value before continuing";
    public static final String EMPTY_TITLE = "The Title field is empty. Please set its value before continuing";
    public static final String NO_TASKS = "There are no tasks to display at this time!";
    public static final String NO_DESCRIPTION = "There is no description available for this task";
    public static final String CONFIRM_COMPLETE = "Confirm task complete";
    public static final String CONFIRM_COMPLETE_MESSAGE = "Are you sure you want to mark this task " +
            "as completed? This will mark any subtask within this task as completed as well and you will " +
            "not be able to edit it in the future.";
    public static final String THIRTY_MINUTE_BROADCAST = "Thirty minutes to task";
    public static final String FIFTEEN_MINUTE_BROADCAST = "Fifteen minutes to task";
    public static final String FIVE_MINUTE_BROADCAST = "Five minutes to task";
    public static final String NOW_BROADCAST = "Task starts now";

    public static final long INTERVAL_DAY = 24*60*60*1000;
    public static final long INTERVAL_THREE_DAYS = 3*24*60*60*1000;
    public static final long INTERVAL_FIFTEEN_DAYS = 15*24*60*60*1000;

    @Override
    public void onCreate() {
        Intent intent = new Intent(getApplicationContext(), TaskAlertService.class);
        startService(intent);

        // Verify if this is the first time starting the app to ensure that the alarms are
        // scheduled only once
        if(hasAppRunBefore(getApplicationContext())) {
            PollReceiver.scheduleAlarms(getApplicationContext());
        }
    }

    /**
     * This method is used to find out if the app has run before. If it has not run before, it
     * returns false and creates a preference file that now sets a key "hasRun" to true
     *
     * @param context - the context that should be passed to this static method
     * @return - whether the app has run before or not
     */
    public static boolean hasAppRunBefore(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        boolean hasRun = preferences.getBoolean("hasRun", false);
        if(!hasRun) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("hasRun", true);
            editor.commit();
        }
        return hasRun;
    }
}
