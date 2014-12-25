package com.android.application;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

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

    @Override
    public void onCreate() {
        Intent intent = new Intent(getApplicationContext(), TaskAlertService.class);
        startService(intent);
    }
}
