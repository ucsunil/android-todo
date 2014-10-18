package com.android.application.helpers;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import com.android.application.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class TimeDialogFragment extends DialogFragment implements View.OnClickListener {

    TimePicker timePicker;
    Button ok, cancel;
    private final int TIME_CODE = 2;
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";

    public static TimeDialogFragment getInstance() {
        TimeDialogFragment timeDialogFragment = new TimeDialogFragment();
        return timeDialogFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        ok = (Button) view.findViewById(R.id.ok);
        ok.setOnClickListener(this);
        cancel = (Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.time);
        return dialog;
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.ok) {
            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();
            Intent intent = new Intent();
            intent.putExtra(HOUR, hour);
            intent.putExtra(MINUTE, minute);
            sendResult(intent, TIME_CODE);
            this.dismiss();
        } else if(view.getId() == R.id.cancel) {
            this.dismiss();
        }
    }

    private void sendResult(Intent intent, int code) {
        getTargetFragment().onActivityResult(getTargetRequestCode(), code, intent);
    }
}
