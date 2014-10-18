package com.android.application.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.application.GlobalData;
import com.android.application.R;
import com.android.application.adapters.TaskTreeAdapter;
import com.android.application.datamodels.Task;
import com.android.application.storage.DataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by umonssu on 10/16/14.
 */
public class TasksDisplayActivity extends Activity implements
        ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener,
        ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupCollapseListener, AdapterView.OnItemLongClickListener {

    List<Task> tasks;
    TaskTreeAdapter taskTreeAdapter;
    ExpandableListView tasksTree;
    private int lastExpandedGroupPosition = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_tasks);
        tasksTree = (ExpandableListView) findViewById(R.id.tasksTree);
        tasks = new ArrayList<Task>();
        taskTreeAdapter = new TaskTreeAdapter(this, tasks);
        tasksTree.setAdapter(taskTreeAdapter);
        tasksTree.setOnChildClickListener(this);
        tasksTree.setOnGroupClickListener(this);
        tasksTree.setOnGroupExpandListener(this);
        tasksTree.setOnGroupCollapseListener(this);
        tasksTree.setOnItemLongClickListener(this);
        loadTasksFromDatabase();
    }

    private void loadTasksFromDatabase() {
        Cursor cursor = getContentResolver().query(DataProvider.TASKS_URI, null, null, null, null);
        if(!cursor.moveToFirst() || cursor.getCount() == 0) {
            Toast.makeText(this, GlobalData.NO_TASKS, Toast.LENGTH_LONG).show();
            return;
        }
        cursor. moveToFirst();
        boolean bool = false;
        while(true) {
            Task task = new Task();
            task.setTaskId(cursor.getInt(0));
            task.setDate(cursor.getString(1));
            task.setTime(cursor.getString(2));
            task.setTask(cursor.getString(3));
            bool = cursor.getInt(4) == 0 ? false : true;
            task.setHasNote(bool);
            bool = cursor.getInt(5) == 0 ? false : true;
            task.setHasSubtasks(bool);
            bool = cursor.getInt(6) == 0? false : true;
            task.setStatus(bool);
            task.setDescription(cursor.getString(7));
            tasks.add(task);

            if(cursor.isLast()) {
                break;
            }
            cursor.moveToNext();
        }
        taskTreeAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition,
                                                        int childPosition, long id) {
        return false;
    }

    @Override
    public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long id) {
        Toast.makeText(this, "Something", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onGroupCollapse(int groupPosition) {

    }

    @Override
    public void onGroupExpand(int groupPosition) {
        if(groupPosition != lastExpandedGroupPosition) {
            tasksTree.collapseGroup(lastExpandedGroupPosition);
        }
        lastExpandedGroupPosition = groupPosition;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ViewActivity.class);
        intent.putExtra("viewWhat", "viewTask");

        Bundle bundle = new Bundle();
        Task task = tasks.get(position);
        bundle.putInt("task_id", task.getTaskId());
        bundle.putString("date", task.getDate());
        bundle.putString("time", task.getTime());
        bundle.putString("task", task.getTask());
        bundle.putBoolean("has_note", task.getHasNote());
        bundle.putBoolean("task_status", task.getStatus());
        bundle.putString("description", task.getDescription());
        intent.putExtras(bundle);
        startActivity(intent);
        return true;
    }
}
