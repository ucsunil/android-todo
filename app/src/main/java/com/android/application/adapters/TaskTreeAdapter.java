package com.android.application.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.android.application.R;
import com.android.application.activities.ViewActivity;
import com.android.application.datamodels.Subtask;
import com.android.application.datamodels.Task;
import com.android.application.storage.DataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by umonssu on 10/14/14.
 */
public class TaskTreeAdapter extends BaseExpandableListAdapter implements View.OnLongClickListener {

    private Context context;
    private LayoutInflater inflater;
    private List<Task> tasks;

    public TaskTreeAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.tasks = tasks;
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
    public View getGroupView(int groupPosition, boolean expanded, View convertView, ViewGroup parent) {
        final int position = groupPosition;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.layout_task, parent,false);
        }
        TextView dateView = (TextView) convertView.findViewById(R.id.dateView);
        dateView.setText(((Task)getGroup(groupPosition)).getDate());
        TextView timeView = (TextView) convertView.findViewById(R.id.timeView);
        timeView.setText(((Task)getGroup(groupPosition)).getTime());
        TextView taskName = (TextView) convertView.findViewById(R.id.taskName);
        taskName.setText(((Task)getGroup(groupPosition)).getTask());
        CheckBox status = (CheckBox) convertView.findViewById(R.id.status);
        boolean currentStatus = ((Task) getGroup(groupPosition)).getStatus();
        if(currentStatus) {
            status.setChecked(true);
            status.setEnabled(false);
        }
        status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked) {
                    ((Task)getGroup(position)).setStatus(true);
                } else if(!checked) {
                    ((Task)getGroup(position)).setStatus(false);
                }

            }
        });
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.subtask_item, parent, false);
        }
        final Subtask subtask = (Subtask) getChild(groupPosition, childPosition);
        TextView title = (TextView) convertView.findViewById(R.id.subtask);
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

    private ArrayList<Subtask> getSubtaskListForGroup(int groupPosition) {
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

}
