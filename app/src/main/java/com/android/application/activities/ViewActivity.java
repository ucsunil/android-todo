package com.android.application.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.android.application.R;
import com.android.application.fragments.TasksDeleteFragment;
import com.android.application.fragments.views.SubtaskViewFragment;
import com.android.application.fragments.views.TaskViewFragment;

public class ViewActivity extends Activity implements TaskViewFragment.OnTaskCompleteListener {

    TaskViewFragment taskViewFragment;
    SubtaskViewFragment subtaskViewFragment;
    TasksDeleteFragment tasksDeleteFragment;
    Bundle dataBundle;
    private final int TASK_COMPLETE_CONFIRMED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        String viewWhat = getIntent().getStringExtra("viewWhat");
        if(viewWhat.equals("viewTask")) {
            dataBundle = getIntent().getExtras();
            showTaskViewFragment(dataBundle);
        } else if(viewWhat.equals("viewSubtask")) {
            dataBundle = getIntent().getExtras();
            showSubtaskViewFragment(dataBundle);
        } else if(viewWhat.equals("viewDeleteList")) {
            showTaskDeleteFragment();
        }

    }

    private void showTaskViewFragment(Bundle dataBundle) {
        if(taskViewFragment == null) {
            taskViewFragment = TaskViewFragment.getInstance(dataBundle);
        }
        if(!taskViewFragment.isVisible()) {
            getFragmentManager().beginTransaction().replace(R.id.content, taskViewFragment).commit();
        }
    }

    private void showSubtaskViewFragment(Bundle dataBundle) {
        if(subtaskViewFragment == null) {
            subtaskViewFragment = SubtaskViewFragment.newInstance(dataBundle);
        }
        if(!subtaskViewFragment.isVisible()) {
            getFragmentManager().beginTransaction().replace(R.id.content, subtaskViewFragment).commit();
        }
    }

    private void showTaskDeleteFragment() {
        if(tasksDeleteFragment == null) {
            tasksDeleteFragment = TasksDeleteFragment.newInstance();
        }
        if(!tasksDeleteFragment.isVisible()) {
            getFragmentManager().beginTransaction().replace(R.id.content, tasksDeleteFragment).commit();
        }
    }

    @Override
    public void onTaskComplete(int taskId) {
        Intent intent = new Intent();
        intent.putExtra("task_id", taskId);
        setResult(TASK_COMPLETE_CONFIRMED, intent);
    }
}
