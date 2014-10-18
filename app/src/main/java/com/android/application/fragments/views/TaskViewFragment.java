package com.android.application.fragments.views;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.application.GlobalData;
import com.android.application.R;
import com.android.application.fragments.EditTaskFragment;

/**
 * Created by umonssu on 10/17/14.
 */
public class TaskViewFragment extends Fragment implements View.OnClickListener {

    TextView dateView, timeView, taskTitle, taskDescription;
    Button edit, ok;
    Bundle taskBundle;

    public static TaskViewFragment getInstance(Bundle taskBundle) {
        TaskViewFragment taskViewFragment = new TaskViewFragment();
        taskViewFragment.setArguments(taskBundle);
        return taskViewFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        taskBundle = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_task, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        dateView = (TextView) view.findViewById(R.id.dateView);
        timeView = (TextView) view.findViewById(R.id.timeView);
        taskTitle = (TextView) view.findViewById(R.id.taskTitle);
        taskDescription = (TextView) view.findViewById(R.id.taskDescription);
        edit = (Button) view.findViewById(R.id.edit);
        edit.setOnClickListener(this);
        ok = (Button) view.findViewById(R.id.ok);
        ok.setOnClickListener(this);

        dateView.setText(taskBundle.getString("date"));
        timeView.setText(taskBundle.getString("time"));
        taskTitle.setText(taskBundle.getString("task"));
        if(TextUtils.isEmpty(taskBundle.getString("description"))) {
            taskDescription.setText(GlobalData.NO_DESCRIPTION);
        } else {
            taskDescription.setText(taskBundle.getString("description"));
        }

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.edit) {
            EditTaskFragment editTaskFragment = EditTaskFragment.newInstance(taskBundle);
            getFragmentManager().beginTransaction().replace(R.id.content, editTaskFragment).commit();
        } else if(view.getId() == R.id.ok) {
            getActivity().finish();
        }
    }
}
