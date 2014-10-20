package com.android.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.application.R;
import com.android.application.activities.ViewActivity;
import com.android.application.datamodels.Task;

import java.util.List;

/**
 * Created by umonssu on 10/14/14.
 */
public class TaskTreeAdapter extends BaseExpandableListAdapter implements View.OnLongClickListener {

    private Activity context;
    private LayoutInflater inflater;
    private List<Task> tasks;

    public TaskTreeAdapter(Activity context, List<Task> tasks) {
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
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return tasks.get(groupPosition);
    }

    @Override
    public Object getChild(int i, int i2) {
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i2) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
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
    public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public long getCombinedChildId(long l, long l2) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long l) {
        return 0;
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
}
