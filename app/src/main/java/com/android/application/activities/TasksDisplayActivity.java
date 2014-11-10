package com.android.application.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.application.GlobalData;
import com.android.application.R;
import com.android.application.adapters.TaskTreeAdapter;
import com.android.application.datamodels.Task;
import com.android.application.helpers.DataProviderObserver;
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
    DataProviderObserver observer;

    private int positionBeingViewed = -1, taskIdPositionBeingViewed = -1;
    private final int TASK_COMPLETE_CONFIRMED = 2;
    private final int EDITED_TASK = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_tasks);
        tasksTree = (ExpandableListView) findViewById(R.id.tasksTree);
        tasks = new ArrayList<Task>();
        taskTreeAdapter = new TaskTreeAdapter(this, tasks);
        tasksTree.setAdapter(taskTreeAdapter);
        observer = new DataProviderObserver(this, new Handler());
        tasksTree.setOnChildClickListener(this);
        tasksTree.setOnGroupClickListener(this);
        tasksTree.setOnGroupExpandListener(this);
        tasksTree.setOnGroupCollapseListener(this);
        tasksTree.setOnItemLongClickListener(this);

        getContentResolver().registerContentObserver(DataProvider.TASKS_URI, true, observer);
        initializeAdapter();
    }

    public void initializeAdapter() {

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
        return false;
    }

    @Override
    public void onGroupCollapse(int groupPosition) {

        //lastExpandedGroupPosition = -1;
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
        Intent intent = new Intent(this, ViewActivity.class);
        intent.putExtra("viewWhat", "viewTask");
        Bundle bundle = new Bundle();
        Task task = tasks.get(position);
        positionBeingViewed = position;
        taskIdPositionBeingViewed = task.getTaskId();
        bundle.putInt("task_id", task.getTaskId());
        bundle.putString("date", task.getDate());
        bundle.putString("time", task.getTime());
        bundle.putString("task", task.getTask());
        bundle.putBoolean("has_note", task.getHasNote());
        bundle.putBoolean("task_status", task.getStatus());
        bundle.putString("description", task.getDescription());
        intent.putExtras(bundle);

        // Listen to the ViewActivity to find out if the task is updated
        startActivityForResult(intent, EDITED_TASK);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tasksdisplaymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                showTaskDeleteFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == resultCode) {
            if(resultCode == EDITED_TASK) {
                boolean edited = intent.getBooleanExtra("edited", false);
                if(edited) {
                    // Then we need to update the data for that task
                    String selection = "task_id=?";
                    String[] selectionArgs = {String.valueOf(intent.getIntExtra("task_id", -1))};
                    Cursor cursor = getContentResolver().query(DataProvider.TASKS_URI, null, selection,
                                                            selectionArgs, null);
                    cursor.moveToFirst();
                    boolean bool;
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
                    tasks.set(positionBeingViewed, task);
                    taskTreeAdapter.notifyDataSetChanged();
                }
            }
        } else if(resultCode == TASK_COMPLETE_CONFIRMED) {
            int index = 0;
            for (Task task : tasks) {
                if(task.getTaskId() == intent.getIntExtra("task_id", -1)) {
                    break;
                }
                ++index;
            }
            String selection = "task_id=?";
            String[] selectionArgs = {String.valueOf(intent.getIntExtra("task_id", -1))};

            Cursor cursor = getContentResolver().query(DataProvider.TASKS_URI, null, selection,
                    selectionArgs, null);
            cursor.moveToFirst();
            boolean bool;
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
            tasks.set(index, task);
            taskTreeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if(lastExpandedGroupPosition != -1) {
            // Collapse this group so that when the activity restarts, the children at
            // this group position are updated automatically
            int tempPosition = lastExpandedGroupPosition;
            tasksTree.collapseGroup(lastExpandedGroupPosition);
            lastExpandedGroupPosition = tempPosition;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(lastExpandedGroupPosition != -1) {
            // expand the children at this position
            // this automatically refreshes the children at this position
            tasksTree.expandGroup(lastExpandedGroupPosition);
        }
    }

    private void showTaskDeleteFragment() {
        Intent intent = new Intent(this, ViewActivity.class);
        intent.putExtra("viewWhat", "viewDeleteList");
        startActivity(intent);
    }

}
