package com.android.application.fragments;


import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.application.GlobalData;
import com.android.application.R;
import com.android.application.datamodels.Subtask;
import com.android.application.helpers.CalendarDialogFragment;
import com.android.application.helpers.NoteFragment;
import com.android.application.helpers.TimeDialogFragment;
import com.android.application.storage.DataProvider;

import java.util.ArrayList;

public class EditTaskFragment extends Fragment implements View.OnClickListener {

    TextView header;
    Switch status;
    private Button calendar, clock, save, cancel;
    private Button addNote, editNote, addSubtask;
    private EditText dateText, timeText, titleText, descriptionText;
    Spinner ampm;
    private final int DATE_CODE = 1;
    private final int TIME_CODE = 2;
    private final int NOTE_CODE = 3;
    private final int SUBTASK_CODE = 4;
    private boolean updatedFlag = false, noteFlag = false, subtasksFlag = false, completed = false;
    Bundle notesBundle, taskBundle;
    private int taskId = -1;
    private int EDITED_CODE = 5;

    AddSubtaskFragment addSubtaskFragment;

    public static EditTaskFragment newInstance(Bundle dataBundle) {
        EditTaskFragment fragment = new EditTaskFragment();
        fragment.setArguments(dataBundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        taskBundle = getArguments();
        taskId = taskBundle.getInt("task_id");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_edit_task, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        header = (TextView) view.findViewById(R.id.header);
        status = (Switch) view.findViewById(R.id.status);
        status.setVisibility(View.GONE);
        header.setText(R.string.edit_task);
        calendar = (Button) view.findViewById(R.id.calendar);
        calendar.setOnClickListener(this);
        clock = (Button) view.findViewById(R.id.time);
        clock.setOnClickListener(this);
        save = (Button) view.findViewById(R.id.save);
        save.setOnClickListener(this);
        cancel = (Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);

        dateText = (EditText) view.findViewById(R.id.taskDate);
        dateText.setText(taskBundle.getString("date"));
        timeText = (EditText) view.findViewById(R.id.taskTime);

        titleText = (EditText) view.findViewById(R.id.taskTitle);
        titleText.setText(taskBundle.getString("task"));
        descriptionText = (EditText) view.findViewById(R.id.taskDescription);
        descriptionText.setText(taskBundle.getString("description"));
        ampm = (Spinner) view.findViewById(R.id.ampm);
        String[] times = taskBundle.getString("time").split(":");
        if(Integer.valueOf(times[0]) <= 12) {
            timeText.setText(taskBundle.getString("time"));
            ampm.setSelection(0);
        } else {
            int hour = Integer.valueOf(times[0]) - 12;
            if(hour < 10) {
                String time = "0" + hour + ":" + times[1];
                timeText.setText(time);
            } else {
                String time = hour + ":" + times[1];
                timeText.setText(time);
            }
            ampm.setSelection(1);
        }

        addSubtask = (Button) view.findViewById(R.id.addSubtask);
        addSubtask.setOnClickListener(this);

        int note = 0;// getArguments().getInt("has_note");
        if(note == 0) {
            noteFlag = false;
            addNote = (Button) view.findViewById(R.id.addNote);
            addNote.setOnClickListener(this);
        } else if(note == 1) { // means that this task has an attached note
            noteFlag = true;
            notesBundle = new Bundle();
            String notes = getNotesForTask();
            editNote = (Button) view.findViewById(R.id.addNote);
            editNote.setText(R.string.edit_note);
            editNote.setOnClickListener(this);
        }
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
            Intent intent = new Intent();
            intent.putExtra("edited", updatedFlag);
            intent.putExtra("task_id", taskBundle.getInt("task_id"));
            getActivity().setResult(EDITED_CODE, intent);
            getActivity().finish();
        } else if(view.getId() == R.id.addNote) {
            if(noteFlag) {
                showAddNoteFragment(notesBundle);
            } else {
                showAddNoteFragment(null);
            }
        } else if(view.getId() == R.id.addSubtask) {
            showAddSubTaskFragment();
        } else if(view.getId() == R.id.cancel) {
            getActivity().finish();
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
            } else if(resultCode == SUBTASK_CODE) {
                taskBundle = intent.getExtras();
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

    private void showAddNoteFragment(Bundle bundle) {
        NoteFragment noteFragment = NoteFragment.getFragment(bundle);
        noteFragment.setTargetFragment(this, NOTE_CODE);
        noteFragment.show(getActivity().getFragmentManager(), "noteFragment");
    }

    /**
     * Set the date from the Calendar Fragment
     * @param intent - the intent returned form the Calendar Fragment
     */
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

    private void showAddSubTaskFragment() {
        if(addSubtaskFragment == null) {
            addSubtaskFragment = AddSubtaskFragment.getInstance(taskBundle);
        }
        addSubtaskFragment.setTargetFragment(this, SUBTASK_CODE);
        if(!addSubtaskFragment.isVisible()) {
            getActivity().getFragmentManager().beginTransaction()
                    .replace(R.id.content, addSubtaskFragment).commit();
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

        // Verify that the data actually changed before updating the database record
        if(date.equals(taskBundle.getString("date")) && time.equals(taskBundle.getString("time"))
                && task.equals(taskBundle.getString("task")) && taskDescription.equals(taskBundle.getString("description"))
                && (noteFlag == taskBundle.getBoolean("has_note")) && (completed == taskBundle.getBoolean("task_status"))
                && (subtasksFlag == taskBundle.getBoolean("subtasks"))) {
            // Means that no information has changed
            return;
        } else {
            // Means the task item is edited
            updatedFlag = true;
            subtasksFlag = taskBundle.getBoolean("subtasks");
        }

        ContentValues values = new ContentValues();
        values.put("date", date);
        if(ampm.getSelectedItem().toString().equals("AM")) {
            values.put("time", time);
        } else {
            String[] times = time.split(":");
            int hour = Integer.valueOf(times[0]);
            if(hour != 12) {
                hour = hour + 12;
            }
            time = hour + ":" + times[1];
            values.put("time", time);
        }
        values.put("task", task);
        values.put("description", taskDescription);
        if(noteFlag) {
            values.put("has_note", 1);
        }  else {
            values.put("has_note", 0);
        }
        if(subtasksFlag) {
            values.put("subtasks", 1);
        } else {
            values.put("subtasks", 0);
        }
        if(completed) {
            values.put("task_status", 1);
        } else {
            values.put("task_status", 0);
        }

        saveSubtasksData();

        // Updating the table data
        String selection = "task_id=?";
        String[] selectionArgs = {String.valueOf(taskId)};
        getActivity().getContentResolver().update(DataProvider.TASKS_URI, values, selection, selectionArgs);
    }

    private void saveSubtasksData() {
        deleteSubtasksListBeforeSave();
        if(taskBundle.getParcelableArrayList("subtasks_list") == null) {
            // There are no subtasks to save
            return;
        }
        ArrayList<Subtask> subtasks = taskBundle.getParcelableArrayList("subtasks_list");
        ContentValues[] values = new ContentValues[subtasks.size()];

        for(int i = 0; i < subtasks.size(); i++) {
            values[i] = new ContentValues();
            values[i].put("task_id", taskId);

            Subtask subtask = subtasks.get(i);
            values[i].put("subtask", subtask.getSubtask());
            values[i].put("subtask_description", subtask.getDescription());
            int has_note = subtask.isHasNote() ? 1 : 0;
            values[i].put("subtask_has_note", has_note);
            int status = subtask.isStatus() ? 1 : 0;
            values[i].put("subtask_status", status);
        }
        // Do a bulk insert
        getActivity().getContentResolver().bulkInsert(DataProvider.SUBTASKS_URI, values);
    }

    private String getNotesForTask() {
        return null;
    }

    private void deleteSubtasksListBeforeSave() {
        String where = "task_id=?";
        String[] whereArgs = {String.valueOf(taskId)};
        getActivity().getContentResolver().delete(DataProvider.SUBTASKS_URI, where, whereArgs);
    }

}
