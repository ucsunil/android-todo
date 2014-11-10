package com.android.application.fragments;


import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.android.application.R;
import com.android.application.adapters.DeleteTasksListAdapter;
import com.android.application.datamodels.Task;
import com.android.application.storage.DataProvider;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class EditTasksListFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    Button goBack;
    ListView listView;
    Cursor cursor;
    ArrayList<Task> tasks;
    DeleteTasksListAdapter adapter;
    EditTaskFragment editTaskFragment;

    public static EditTasksListFragment newInstance() {
        EditTasksListFragment fragment = new EditTasksListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cursor = getActivity().getContentResolver().query(DataProvider.TASKS_URI, null, null, null, null);
        tasks = new ArrayList<Task>();
        if(!cursor.moveToFirst() || cursor.getCount() == 0) {
            return;
        }
        for(int i = 0; i < cursor.getCount(); i++) {
            Task task = new Task();
            task.setTaskId(cursor.getInt(0));
            task.setDate(cursor.getString(1));
            task.setTime(cursor.getString(2));
            task.setTask(cursor.getString(3));
            boolean hasNote = (cursor.getInt(4) == 1) ? true : false;
            task.setHasNote(hasNote);
            boolean status = (cursor.getInt(6) == 1) ? true : false;
            task.setStatus(status);
            task.setDescription(cursor.getString(7));
            tasks.add(task);
        }
        adapter = new DeleteTasksListAdapter(getActivity(), tasks);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_tasks_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        goBack = (Button) view.findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
        listView = (ListView) view.findViewById(R.id.editTaskList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.goBack) {

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Task task = tasks.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("task_id", task.getTaskId());

        if(editTaskFragment == null) {
            editTaskFragment = EditTaskFragment.newInstance(bundle);
        }
        if(!editTaskFragment.isVisible()) {
            getFragmentManager().beginTransaction().replace(R.id.content, editTaskFragment).commit();
        }
    }
}
