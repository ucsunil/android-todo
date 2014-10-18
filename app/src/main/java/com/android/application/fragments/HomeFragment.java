package com.android.application.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.application.R;
import com.android.application.activities.TasksDisplayActivity;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class HomeFragment extends Fragment {

    Button openCreateTask, openDisplayTask, editTask, editTaskList;
    CreateTaskFragment createTaskFragment;
    EditTaskFragment editTaskFragment;
    EditTasksListFragment editTaskListFragment;

    public static HomeFragment getInstance() {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        openCreateTask = (Button) view.findViewById(R.id.openCreateTask);
        openCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateTaskFragment();
            }
        });
        openDisplayTask = (Button) view.findViewById(R.id.openDisplayTask);
        openDisplayTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TasksDisplayActivity.class);
                startActivity(intent);
            }
        });

        editTaskList = (Button) view.findViewById(R.id.editTaskList);
        editTaskList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditTaskListFragment();
            }
        });
    }

    private void showCreateTaskFragment() {
        if(createTaskFragment == null) {
            createTaskFragment = CreateTaskFragment.newInstance();
        }
        if(!createTaskFragment.isVisible()) {
            getFragmentManager().beginTransaction().replace(R.id.content, createTaskFragment).commit();
        }
    }

    private void showEditTaskListFragment() {
        if(editTaskListFragment == null) {
            editTaskListFragment = EditTasksListFragment.newInstance();
        }
        if(!editTaskListFragment.isVisible()) {
            getFragmentManager().beginTransaction().replace(R.id.content, editTaskListFragment).commit();
        }
    }




}
