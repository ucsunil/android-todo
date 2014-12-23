package com.android.application.adapters;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.android.application.R;
import com.android.application.activities.TasksDisplayActivity;
import com.android.application.activities.ViewActivity;
import com.android.application.datamodels.Subtask;
import com.android.application.datamodels.Task;
import com.android.application.helpers.TaskCompleteDialog;
import com.android.application.storage.DataProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by umonssu on 10/14/14.
 */
public class TaskTreeAdapter extends BaseExpandableListAdapter implements View.OnLongClickListener {

    private Context context;
    private LayoutInflater inflater;
    private List<Task> tasks;
    Activity tasksDisplayActivity;

    TextView taskName, dateView, timeView;

    public TaskTreeAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.tasks = tasks;
        this.tasksDisplayActivity = (TasksDisplayActivity) context;
    }

    @Override
    public int getGroupCount() {
        return tasks.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<Subtask> children = getSubtaskListForGroup(groupPosition);
        return children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return tasks.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<Subtask> children = getSubtaskListForGroup(groupPosition);
        return children.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        Task task = tasks.get(groupPosition);
        return task.getTaskId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        ArrayList<Subtask> children = getSubtaskListForGroup(groupPosition);
        Subtask subtask = children.get(childPosition);
        return subtask.getSubtaskId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean expanded, View convertView, ViewGroup parent) {
        final int position = groupPosition;
        convertView = inflater.inflate(R.layout.layout_task, parent,false);
        dateView = (TextView) convertView.findViewById(R.id.dateView);
        String dateText = ((Task)getGroup(groupPosition)).getDate();
        dateView.setText(dateText);
        timeView = (TextView) convertView.findViewById(R.id.timeView);
        String timeText = ((Task)getGroup(groupPosition)).getTime();
        String time = timeText;
        String dayPeriod = "AM";
        String[] timeParts = timeText.split(":");
        int hour = Integer.valueOf(timeParts[0]);
        if(hour > 12) {
            hour = hour - 12;
            dayPeriod = "PM";
            timeText = hour + ":" + timeParts[1] + " " + dayPeriod;
        } else {
            timeText = timeText + " " + dayPeriod;
        }
        timeView.setText(timeText);
        taskName = (TextView) convertView.findViewById(R.id.taskName);
        taskName.setText(((Task)getGroup(groupPosition)).getTask());
        CheckBox status = (CheckBox) convertView.findViewById(R.id.status);
        boolean currentStatus = ((Task) getGroup(groupPosition)).getStatus();
        if(currentStatus) {
            status.setChecked(true);
            status.setEnabled(false);
            taskName.setTextColor(Color.GRAY);
            dateView.setTextColor(Color.GRAY);
            timeView.setTextColor(Color.GRAY);
        }
        status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked) {
                    TaskCompleteDialog dialog = TaskCompleteDialog.newInstance(setTaskBundle(groupPosition));
                    dialog.show(tasksDisplayActivity.getFragmentManager(), "alertDialog");
                } else if(!checked) {
                    ((Task)getGroup(position)).setStatus(false);
                }

            }
        });
        boolean timeElapsed = isTimeElapsed(dateText, time);
        if(timeElapsed && !currentStatus) {
            taskName.setTextColor(Color.RED);
            dateView.setTextColor(Color.RED);
            timeView.setTextColor(Color.RED);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.subtask_item, parent, false);
        final Subtask subtask = (Subtask) getChild(groupPosition, childPosition);
        final TextView title = (TextView) convertView.findViewById(R.id.subtask);
        title.setText(subtask.getSubtask());
        final Switch status = (Switch) convertView.findViewById(R.id.status);
        status.setChecked(subtask.isStatus());
        status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                subtask.setStatus(checked);
                ContentValues values = new ContentValues();
                int value = checked ? 1 : 0;
                values.put("subtask_status", value);
                String where = "subtask_id=?";
                String[] whereArgs = {String.valueOf(subtask.getSubtaskId())};
                context.getContentResolver().update(DataProvider.SUBTASKS_URI, values, where, whereArgs);
            }
        });
        if(isParentComplete(groupPosition)) {
            title.setTextColor(Color.GRAY);
            status.setEnabled(false);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean onLongClick(View view) {
        if(view.getId() == R.id.taskName) {
            Intent intent = new Intent(context, ViewActivity.class);
            intent.putExtra("viewWhat", "viewTask");
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    public ArrayList<Subtask> getSubtaskListForGroup(int groupPosition) {
        Task task = tasks.get(groupPosition);
        int taskId = task.getTaskId();
        ArrayList<Subtask> subtasks = new ArrayList<Subtask>();

        String selection = "task_id=?";
        String[] selectionArgs = {String.valueOf(taskId)};
        Cursor cursor = context.getContentResolver().query(DataProvider.SUBTASKS_URI, null, selection, selectionArgs, null);

        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++) {
            Subtask subtask = new Subtask();
            subtask.setSubtaskId(cursor.getInt(0));
            subtask.setTaskId(cursor.getInt(1));
            subtask.setSubtask(cursor.getString(2));
            boolean bool = (cursor.getInt(3) == 1) ? true : false;
            subtask.setHasNote(bool);
            bool = (cursor.getInt(4) == 1) ? true : false;
            subtask.setStatus(bool);
            subtask.setDescription(cursor.getString(5));
            subtasks.add(subtask);

            if(cursor.isLast()) {
                break;
            }
            cursor.moveToNext();
        }
        return subtasks;
    }

    private boolean isParentComplete(int groupPosition) {
        Task task = tasks.get(groupPosition);
        return task.getStatus();
    }

    private boolean isTimeElapsed(String date, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        Date taskDate = null;
        try {
            taskDate = sdf.parse(date + " " + time);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        Date now = new Date();
        return now.after(taskDate);
    }

    private Bundle setTaskBundle(int groupPosition) {
        Task task = tasks.get(groupPosition);
        Bundle bundle = new Bundle();
        bundle.putInt("task_id", task.getTaskId());
        return bundle;
    }
}
