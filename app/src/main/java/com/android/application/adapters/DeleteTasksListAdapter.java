package com.android.application.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.application.R;
import com.android.application.datamodels.Task;
import com.android.application.storage.DataProvider;

import java.util.List;

/**
 * This class is the backing adapter for the delete tasks list activity
 *
 * Created by Sunil on 10/17/14.
 */
public class DeleteTasksListAdapter extends BaseAdapter {

    private Context context;
    private List<Task> tasks;

    public DeleteTasksListAdapter(Context context, List<Task> tasks) {
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
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.layout_task_delete, parent, false);
        ImageView delete = (ImageView) rowView.findViewById(R.id.delete);

        final Task task = tasks.get(position);

        TextView dateView = (TextView) rowView.findViewById(R.id.dateView);
        dateView.setText(task.getDate());
        TextView timeView = (TextView) rowView.findViewById(R.id.timeView);
        timeView.setText(task.getTime());
        TextView taskName = (TextView) rowView.findViewById(R.id.taskName);
        taskName.setText(task.getTask());
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selection = "task_id=?";
                String[] selectionArgs = {String.valueOf(task.getTaskId())};
                context.getContentResolver().delete(DataProvider.TASKS_URI, selection, selectionArgs);
                context.getContentResolver().delete(DataProvider.SUBTASKS_URI, selection, selectionArgs);
                tasks.remove(position);
                notifyDataSetChanged();
            }
        });
        return rowView;
    }
}
