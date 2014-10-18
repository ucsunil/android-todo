package com.android.application.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.application.R;
import com.android.application.datamodels.Task;

import java.util.ArrayList;

/**
 * Created by umonssu on 10/17/14.
 */
public class EditTasksListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Task> tasks;

    public EditTasksListAdapter(Context context, ArrayList<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
    }
    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.layout_task, parent, false);
        CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.status);
        checkBox.setVisibility(View.GONE);

        Task task = tasks.get(position);

        TextView dateView = (TextView) rowView.findViewById(R.id.dateView);
        dateView.setText(task.getDate());
        TextView timeView = (TextView) rowView.findViewById(R.id.timeView);
        timeView.setText(task.getTime());
        TextView taskName = (TextView) rowView.findViewById(R.id.taskName);
        taskName.setText(task.getTask());
        return rowView;
    }
}
