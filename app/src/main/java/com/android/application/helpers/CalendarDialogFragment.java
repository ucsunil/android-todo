package com.android.application.helpers;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;

import com.android.application.R;


public class CalendarDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "CalendarDialogFragment";

    DatePicker datePicker;
    Button ok, cancel;
    private final int DATE_CODE = 1;
    private static final String DATE = "date";
    private static final String MONTH = "month";
    private static final String YEAR = "year";

    public static CalendarDialogFragment getInstance() {
        CalendarDialogFragment calendarDialogFragment = new CalendarDialogFragment();
        return calendarDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        ok = (Button) view.findViewById(R.id.ok);
        ok.setOnClickListener(this);
        cancel = (Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        initializeDatePicker();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.calendar_title);

        return dialog;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.ok) {
            int date = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();

            Intent intent = new Intent();
            intent.putExtra(DATE, date);
            intent.putExtra(MONTH, month);
            intent.putExtra(YEAR, year);
            sendResult(intent, DATE_CODE);
            this.dismiss();
        } else if(view.getId() == R.id.cancel) {
            this.dismiss();
        }

    }

    private void initializeDatePicker() {
        datePicker.setMinDate(System.currentTimeMillis() - 1000);

        // Setting the CalendarView of the DatePicker object
        CalendarView calendarView = datePicker.getCalendarView();
        calendarView.setFirstDayOfWeek(1);
        calendarView.setShowWeekNumber(false);
    }

    private void sendResult(Intent intent, int code) {
        Log.i(TAG, "Sending result to fragment");
        getTargetFragment().onActivityResult(getTargetRequestCode(), code, intent);
    }
}
