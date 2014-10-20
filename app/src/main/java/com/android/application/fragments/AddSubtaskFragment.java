package com.android.application.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.application.GlobalData;
import com.android.application.R;
import com.android.application.adapters.SubtaskAdapter;
import com.android.application.datamodels.Subtask;
import com.android.application.storage.DataProvider;

import java.util.ArrayList;

/**
 * Created by umonssu on 10/14/14.
 */
public class AddSubtaskFragment extends Fragment implements View.OnClickListener {

    TextView header;
    Button addNote, save, add, discard;
    ArrayList<Subtask> subtasks;
    SubtaskAdapter subtaskAdapter;
    EditText title, description;
    ListView listView;
    private boolean noteFlag = false;
    private final int SUBTASK_CODE = 4;
    EditTaskFragment editTaskFragment;
    Bundle taskBundle;

    public static AddSubtaskFragment getInstance(Bundle taskBundle) {
        AddSubtaskFragment addSubtaskFragment = new AddSubtaskFragment();
        addSubtaskFragment.setArguments(taskBundle);
        return addSubtaskFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        taskBundle = getArguments();
        // subtasks = new ArrayList<Subtask>();
        // subtaskAdapter = new SubtaskAdapter(getActivity(), subtasks);
        if(taskBundle.getParcelableArrayList("subtasks_list") != null) {
            initializeSubtasksFromList();
        } else {
            initializeAdapter();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_subtask, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        header = (TextView) view.findViewById(R.id.header);
        header.setText(R.string.add_subtask);
        title = (EditText) view.findViewById(R.id.taskTitle);
        add = (Button) view.findViewById(R.id.add);
        add.setOnClickListener(this);
        description = (EditText) view.findViewById(R.id.taskDescription);
        addNote = (Button) view.findViewById(R.id.addNote);
        save = (Button) view.findViewById(R.id.save);
        save.setOnClickListener(this);
        discard = (Button) view.findViewById(R.id.discard);
        discard.setOnClickListener(this);

        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setAdapter(subtaskAdapter);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.add) {
            if(isDataCorrect()) {
                addSubtaskToList();
            }
        } else if(view.getId() == R.id.save) {
            saveSubtasksList();
            showEditTaskFragment();
        } else if(view.getId() == R.id.discard) {
            showEditTaskFragment();
        }
    }

    private void initializeSubtasksFromList() {
        subtasks = taskBundle.getParcelableArrayList("subtasks_list");
        subtaskAdapter = new SubtaskAdapter(getActivity(), subtasks);
        subtaskAdapter.notifyDataSetChanged();
    }

    /**
     * This method loads the subtasks that may have already been created for this task
     */
    private void initializeAdapter() {
        subtasks = new ArrayList<Subtask>();
        subtaskAdapter = new SubtaskAdapter(getActivity(), subtasks);
        String selection = "task_id=?";
        String[] selectionArgs = {String.valueOf(taskBundle.getInt("task_id"))};
        Cursor cursor = getActivity().getContentResolver().query(DataProvider.SUBTASKS_URI, null, selection, selectionArgs, null);
        if(!cursor.moveToFirst() || cursor.getCount() <= 0) {
            // Means there are no elements for this view
            return;
        }
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++) {
            Subtask subtask = new Subtask();
            subtask.setSubtaskId(cursor.getInt(0));
            subtask.setTaskId(cursor.getInt(1));
            subtask.setSubtask(cursor.getString(2));
            boolean bool = (cursor.getInt(3) == 1) ? true : false;
            subtask.setHasNote(bool);
            bool = (cursor.getInt(4) == 1) ? true : false;
            subtask.setStatus(bool);
            subtask.setDescription(cursor.getString(5));
            subtasks.add(subtask);

            if(cursor.isLast()) {
                break;
            }
            cursor.moveToNext();
        }
        subtaskAdapter.notifyDataSetChanged();
    }

    private boolean isDataCorrect() {
        String titleText = title.getText().toString();

        if(TextUtils.isEmpty(titleText)) {
            Toast.makeText(getActivity(), GlobalData.EMPTY_TITLE, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void addSubtaskToList() {
        String titleText = title.getText().toString();
        String descriptionText = description.getText().toString();
        Subtask subtask = new Subtask();
        subtask.setSubtask(titleText);
        subtask.setDescription(descriptionText);
        if(noteFlag) {
            subtask.setHasNote(noteFlag);
        }
        subtask.setBooleans();
        subtasks.add(subtask);
        subtaskAdapter.notifyDataSetChanged();
    }

    private void saveSubtasksList() {
        if(subtasks.size() == 0) {
            return;
        }
        taskBundle.putBoolean("subtasks", true);
        taskBundle.putParcelableArrayList("subtasks_list", subtasks);
        Intent intent = new Intent();
        intent.putExtras(taskBundle);
        getTargetFragment().onActivityResult(getTargetRequestCode(), SUBTASK_CODE, intent);
    }

    private void showEditTaskFragment() {
        if(editTaskFragment == null) {
            editTaskFragment = EditTaskFragment.newInstance(taskBundle);
        }
        if(!editTaskFragment.isVisible()) {
            getFragmentManager().beginTransaction().replace(R.id.content, editTaskFragment).commit();
        }
    }
}
