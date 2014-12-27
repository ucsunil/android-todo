package com.android.application.fragments.views;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.application.GlobalData;
import com.android.application.R;
import com.android.application.adapters.SubtaskAdapter;
import com.android.application.datamodels.Subtask;
import com.android.application.fragments.EditTaskFragment;
import com.android.application.helpers.ActionModeListener;
import com.android.application.helpers.TaskCompleteDialog;
import com.android.application.storage.DataProvider;

import java.util.ArrayList;

/**
 * Created by umonssu on 10/17/14.
 */
public class TaskViewFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    TextView dateView, timeView, taskTitle, taskDescription;
    Button edit, ok;
    Bundle taskBundle;
    ListView listView;
    ArrayList<Subtask> subtasks;
    SubtaskAdapter adapter;
    Switch taskStatus;
    private int taskId;
    private final int CONFIRMED_CODE = 1;
    private final int NOT_CONFIRMED_CODE = 2;
    private OnTaskCompleteListener taskCompleteListener;
    private int actionModeFlag = 1;

    public static TaskViewFragment getInstance(Bundle taskBundle) {
        TaskViewFragment taskViewFragment = new TaskViewFragment();
        taskViewFragment.setArguments(taskBundle);
        return taskViewFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            taskCompleteListener = (OnTaskCompleteListener) activity;
        } catch (ClassCastException ex) {
            throw new ClassCastException("Calling activity must implement OnTaskCompleteListener!!");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        taskBundle = getArguments();
        subtasks = new ArrayList<Subtask>();
        adapter = new SubtaskAdapter(getActivity(), subtasks);
        taskId = taskBundle.getInt("task_id");
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
        listView = (ListView) view.findViewById(R.id.subtasksList);
        edit = (Button) view.findViewById(R.id.edit);
        edit.setOnClickListener(this);
        // If this is being started from a notification, then disable the Edit button
        if(taskBundle.getBoolean("fromNotification", false)) {
            edit.setEnabled(false);
        }
        ok = (Button) view.findViewById(R.id.ok);
        ok.setOnClickListener(this);
        taskStatus = (Switch) view.findViewById(R.id.taskStatus);
        boolean status = taskBundle.getBoolean("task_status");
        if(status) {
            taskStatus.setChecked(true);
            taskStatus.setEnabled(false);
            adapter.setParentCompleteFlag(true);
            edit.setEnabled(false);
        }
        listView.setAdapter(adapter);
        taskStatus.setOnCheckedChangeListener(this);

        dateView.setText(taskBundle.getString("date"));
        timeView.setText(taskBundle.getString("time"));
        taskTitle.setText(taskBundle.getString("task"));
        if(TextUtils.isEmpty(taskBundle.getString("description"))) {
            taskDescription.setText(GlobalData.NO_DESCRIPTION);
        } else {
            taskDescription.setText(taskBundle.getString("description"));
        }
        listView.setMultiChoiceModeListener(new ActionModeListener(getActivity(), this, listView, actionModeFlag));
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                listView.setItemChecked(position, true);
                return true;
            }
        });
        initializeAdapter();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.edit) {
            EditTaskFragment editTaskFragment = EditTaskFragment.newInstance(taskBundle);
            getFragmentManager().beginTransaction().replace(R.id.content, editTaskFragment).commit();
        } else if(view.getId() == R.id.ok) {
            saveAllData();
            getActivity().finish();
        }
    }

    private void initializeAdapter() {
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
        adapter.notifyDataSetChanged();
    }

    private void saveAllData() {

        saveSubtasksStatusData();
    }

    private void saveSubtasksStatusData() {
        if(subtasks.size() == 0) {
            // There are no subtasks to save
            return;
        }
        ContentValues[] values = new ContentValues[subtasks.size()];
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        String selection = "subtask_id=?";

        for(int i = 0; i < subtasks.size(); i++) {
            values[i] = new ContentValues();

            Subtask subtask = subtasks.get(i);
            String selectionArgs[] = {String.valueOf(subtask.getSubtaskId())};
            int status = subtask.isStatus() ? 1 : 0;
            values[i].put("subtask_status", status);
            operations.add(ContentProviderOperation.newUpdate(DataProvider.SUBTASKS_URI)
                    .withValues(values[i]).withSelection(selection, selectionArgs)
                    .withYieldAllowed(false).build());
        }
        try {
            getActivity().getContentResolver().applyBatch(DataProvider.AUTHORITY, operations);
        } catch (OperationApplicationException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if(checked) {
            showAlertMessage();
        }
    }

    private void showAlertMessage() {
        TaskCompleteDialog dialog = TaskCompleteDialog.newInstance(taskBundle);
        dialog.setTargetFragment(this, CONFIRMED_CODE);
        dialog.show(getActivity().getFragmentManager(), "alertDialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == CONFIRMED_CODE) {
            taskCompleteListener.onTaskComplete(taskId);
            taskBundle.putBoolean("task_status", true);
            subtasks.clear();
            adapter.setParentCompleteFlag(true);
            initializeAdapter();
            taskStatus.setChecked(true);
            taskStatus.setEnabled(false);
            edit.setEnabled(false);
        } else if(resultCode == NOT_CONFIRMED_CODE) {
            taskStatus.setChecked(false);
        }
    }

    public boolean performActions(MenuItem item) {
        SparseBooleanArray checked = listView.getCheckedItemPositions();

        switch (item.getItemId()) {
            case R.id.delete:
                int count = 0;
                ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                String selection = "subtask_id=?";
                for (int i = 0; i < checked.size(); i++) {
                    int position = checked.keyAt(i);
                    Subtask subtask = subtasks.get(position - count);
                    int subtaskId = subtask.getSubtaskId();
                    String[] selectionArgs = {String.valueOf(subtaskId)};
                    subtasks.remove(position - count);
                    adapter.notifyDataSetChanged();
                    count++;
                    operations.add(ContentProviderOperation.newDelete(DataProvider.SUBTASKS_URI)
                            .withSelection(selection, selectionArgs).withYieldAllowed(false).build());
                }
                try {
                    getActivity().getContentResolver().applyBatch(DataProvider.AUTHORITY, operations);
                } catch (OperationApplicationException ex) {
                    ex.printStackTrace();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
                return true;
        }
        return false;
    }

    public interface OnTaskCompleteListener {
        public void onTaskComplete(int taskId);
    }
}
