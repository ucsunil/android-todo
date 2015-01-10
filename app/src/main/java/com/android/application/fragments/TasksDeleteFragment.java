package com.android.application.fragments;


import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.application.GlobalData;
import com.android.application.R;
import com.android.application.adapters.EditTasksListAdapter;
import com.android.application.datamodels.Task;
import com.android.application.storage.DataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class TasksDeleteFragment extends Fragment {

    ListView tasksList;
    EditTasksListAdapter adapter;
    List<Task> tasks;

    public static TasksDeleteFragment newInstance() {
        TasksDeleteFragment fragment = new TasksDeleteFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tasks = new ArrayList<Task>();
        adapter = new EditTasksListAdapter(getActivity(), tasks);
        initializeAdapter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasks_delete, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        tasksList = (ListView) view.findViewById(R.id.listTasks);
        tasksList.setAdapter(adapter);

        Button goBack = (Button) view.findViewById(R.id.goBack);
    }

    private void initializeAdapter() {
        Cursor cursor = getActivity().getContentResolver().query(DataProvider.TASKS_URI, null, null, null, null);
        if(!cursor.moveToFirst() || cursor.getCount() == 0) {
            Toast.makeText(getActivity(), GlobalData.NO_TASKS, Toast.LENGTH_LONG).show();
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
        adapter.notifyDataSetChanged();
    }

}
