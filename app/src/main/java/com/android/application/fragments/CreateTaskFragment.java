package com.android.application.fragments;


import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.application.GlobalData;
import com.android.application.R;
import com.android.application.helpers.CalendarDialogFragment;
import com.android.application.helpers.NoteFragment;
import com.android.application.helpers.TimeDialogFragment;
import com.android.application.storage.DataProvider;

public class CreateTaskFragment extends Fragment implements View.OnClickListener {

    TextView header;
    Switch status;
    private Button calendar, clock, save, cancel;
    private Button addNote, addSubtask;
    private EditText dateText, timeText, titleText, descriptionText;
    Spinner ampm;
    private final int DATE_CODE = 1;
    private final int TIME_CODE = 2;
    private final int NOTE_CODE = 3;
    private boolean noteFlag = false, completed = false;
    ContentValues tableTaskValues, tableNoteValues;
    Bundle notesBundle;

    HomeFragment homeFragment;

    public static CreateTaskFragment newInstance() {
        CreateTaskFragment fragment = new CreateTaskFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tableTaskValues = new ContentValues();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_edit_task, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        header = (TextView) view.findViewById(R.id.header);
        header.setText(R.string.create_task);
        status = (Switch) view.findViewById(R.id.status);
        status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
                if(status) {
                    completed = true;
                } else {
                    completed = false;
                }
            }
        });
        calendar = (Button) view.findViewById(R.id.calendar);
        calendar.setOnClickListener(this);
        clock = (Button) view.findViewById(R.id.time);
        clock.setOnClickListener(this);
        save = (Button) view.findViewById(R.id.save);
        save.setOnClickListener(this);
        cancel = (Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);

        dateText = (EditText) view.findViewById(R.id.taskDate);
        timeText = (EditText) view.findViewById(R.id.taskTime);
        titleText = (EditText) view.findViewById(R.id.taskTitle);
        descriptionText = (EditText) view.findViewById(R.id.taskDescription);
        ampm = (Spinner) view.findViewById(R.id.ampm);
        ampm.setSelection(0);
        addNote = (Button) view.findViewById(R.id.addNote);
        addNote.setOnClickListener(this);
        addSubtask = (Button) view.findViewById(R.id.addSubtask);
        addSubtask.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.calendar) {
            showCalendarDialogFragment();
        } else if(view.getId() == R.id.time) {
            showTimeDialogFragment();
        } else if(view.getId() == R.id.save) {
            if(isDataCorrect()) {
                saveData();
            }
            // test();
            goBack();
        } else if(view.getId() == R.id.addNote) {
            showAddNoteFragment();
        } else if(view.getId() == R.id.cancel) {
            goBack();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == resultCode) {
            if(resultCode == DATE_CODE) {
                setDate(intent);
            } else if(resultCode == TIME_CODE) {
                setTime(intent);
            } else if(resultCode == NOTE_CODE) {
                notesBundle = intent.getExtras();
                boolean hasNote = notesBundle.getBoolean("noteFlag");
                if(hasNote) {
                    noteFlag = true;
                } else {
                    noteFlag = false;
                }
            }
        }
    }

    private void showCalendarDialogFragment() {
        CalendarDialogFragment calendarDialogFragment = CalendarDialogFragment.getInstance();
        calendarDialogFragment.setTargetFragment(this, DATE_CODE);
        calendarDialogFragment.show(getActivity().getFragmentManager(), "calendarDialog");
    }

    private void showTimeDialogFragment() {
        TimeDialogFragment timeDialogFragment = TimeDialogFragment.getInstance();
        timeDialogFragment.setTargetFragment(this, TIME_CODE);
        timeDialogFragment.show(getActivity().getFragmentManager(), "timeDialog");
    }

    private void showAddNoteFragment() {
        NoteFragment noteFragment = NoteFragment.getFragment(null);
        noteFragment.setTargetFragment(this, NOTE_CODE);
        noteFragment.show(getActivity().getFragmentManager(), "noteFragment");
    }

    private void setDate(Intent intent) {
        int date = intent.getIntExtra("date", 0);
        int month = intent.getIntExtra("month", 0);
        int year = intent.getIntExtra("year", 0);

        if(date < 10 && month < 10) {
            dateText.setText(new StringBuilder().append("0").append(date).append("/").
                    append("0").append(month).append("/").append(year).toString());
        } else if(date < 10 && month >= 10){
            dateText.setText(new StringBuilder().append("0").append(date).append("/").
                    append(month).append("/").append(year).toString());
        }else if(date >= 10 && month < 10) {
            dateText.setText(new StringBuilder().append(date).append("/").append("0").
                    append(month).append("/").append(year).toString());
        }else if(date >= 10 && month >= 10) {
            dateText.setText(new StringBuilder().append(date).append("/").append(month).
                    append("/").append(year).toString());
        }
    }

    private void setTime(Intent intent) {
        int hour = intent.getIntExtra("hour", -1);
        int minute = intent.getIntExtra("minute", -1);
        if(hour < 12) {
            ampm.setSelection(0);
        } else {
            ampm.setSelection(1);
            hour = hour - 12;
        }

        if(hour < 10 && minute < 10) {
            timeText.setText(new StringBuilder().append("0").append(hour).append(":").
                    append("0").append(minute).toString());
        } else if(hour < 10 && minute >=10) {
            timeText.setText(new StringBuilder().append("0").append(hour).append(":").
                    append(minute).toString());
        } else if(hour >= 10 && minute < 10) {
            timeText.setText(new StringBuilder().append(hour).append(":").append("0").
                    append(minute).toString());
        } else if(hour >=10 && minute >= 10) {
            timeText.setText(new StringBuilder().append(hour).append(":").append(minute).toString());
        }
    }

    private boolean isDataCorrect() {
        String date = dateText.getText().toString();
        String time = timeText.getText().toString();
        String task = titleText.getText().toString();

        if(TextUtils.isEmpty(date) && TextUtils.isEmpty(time) && TextUtils.isEmpty(task)) {
            Toast.makeText(getActivity(), GlobalData.EMPTY_DATETIMETITLE, Toast.LENGTH_LONG).show();
        } else if(TextUtils.isEmpty(date) && TextUtils.isEmpty(time) && !TextUtils.isEmpty(task)) {
            Toast.makeText(getActivity(), GlobalData.EMPTY_DATETIME, Toast.LENGTH_LONG).show();
        } else if(!TextUtils.isEmpty(date) && TextUtils.isEmpty(time) && TextUtils.isEmpty(task)) {
            Toast.makeText(getActivity(), GlobalData.EMPTY_TIMETITLE, Toast.LENGTH_LONG);
        } else if(TextUtils.isEmpty(date) && !TextUtils.isEmpty(time) && TextUtils.isEmpty(task)) {
            Toast.makeText(getActivity(), GlobalData.EMPTY_DATETITLE, Toast.LENGTH_LONG).show();
        } else if(!TextUtils.isEmpty(date) && !TextUtils.isEmpty(time) && TextUtils.isEmpty(task)) {
            Toast.makeText(getActivity(), GlobalData.EMPTY_TITLE, Toast.LENGTH_LONG).show();
        } else if(!TextUtils.isEmpty(date) && TextUtils.isEmpty(time) && !TextUtils.isEmpty(task)) {
            Toast.makeText(getActivity(), GlobalData.EMPTY_TIME, Toast.LENGTH_LONG).show();
        } else if(TextUtils.isEmpty(date) && !TextUtils.isEmpty(time) && !TextUtils.isEmpty(task)) {
            Toast.makeText(getActivity(), GlobalData.EMPTY_DATE, Toast.LENGTH_LONG).show();
        } else if(!TextUtils.isEmpty(date) && !TextUtils.isEmpty(time) && !TextUtils.isEmpty(task)) {
            return true;
        }
        return false;
    }

    private void saveData() {
        String date = dateText.getText().toString();
        String time = timeText.getText().toString();
        String task = titleText.getText().toString();
        String taskDescription = descriptionText.getText().toString();

        tableTaskValues.put("date", date);
        tableTaskValues.put("time", time);
        tableTaskValues.put("task", task);
        tableTaskValues.put("description", taskDescription);
        tableTaskValues.put("subtasks", 0);
        if(noteFlag) {
            tableTaskValues.put("has_note", 1);
        }  else {
            tableTaskValues.put("has_note", 0);
        }
        if(completed) {
            tableTaskValues.put("task_status", 1);
        } else {
            tableTaskValues.put("task_status", 0);
        }

        Uri uri = getActivity().getContentResolver().insert(DataProvider.TASKS_URI, tableTaskValues);
        if(noteFlag) {
            String rowId = uri.getLastPathSegment();
            saveNote(Integer.valueOf(rowId));
        }
        Log.i("TAG", uri.toString());
    }

    private void saveNote(int taskId) {
        tableNoteValues = new ContentValues();
        tableNoteValues.put("task_id", taskId);
        tableNoteValues.put("note", notesBundle.getString("note"));

        getActivity().getContentResolver().insert(DataProvider.NOTES_URI, tableNoteValues);
    }

    private void test() {
        Cursor cursor = getActivity().getContentResolver().query(DataProvider.TASKS_URI,
                null, null, null, null);
        Log.d("CreateTaskFragment", "Cursor size = " + cursor.getCount());
        cursor.moveToFirst();
        while(true) {
            Log.d("TAG", ""+cursor.getInt(0));
            Log.d("TAG", ""+cursor.getString(1));
            if(cursor.isLast()) {
                break;
            }
            cursor.moveToNext();
        }

    }

    private void goBack() {
        if(homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        if(!homeFragment.isVisible()) {
            getFragmentManager().beginTransaction().replace(R.id.content, homeFragment).commit();
        }
    }
}
