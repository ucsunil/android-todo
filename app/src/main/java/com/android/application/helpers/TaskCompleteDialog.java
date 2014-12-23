package com.android.application.helpers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.android.application.GlobalData;
import com.android.application.activities.TasksDisplayActivity;
import com.android.application.activities.ViewActivity;
import com.android.application.storage.DataProvider;

/**
 * Created by umonssu on 10/19/14.
 */
public class TaskCompleteDialog extends DialogFragment {

    private final int CONFIRMED = 1;
    private final int NOT_CONFIRMED = 2;
    // Created this code exclusively for use within the TasksDisplayActivity
    // as the code '2' corresponding to NOT_CONFIRMED was already used to
    // describe TASK_COMPLETE_CONFIRMED
    private final int TDA_NOT_CONFIRMED_CODE = 3;
    Bundle taskBundle;

    public static TaskCompleteDialog newInstance(Bundle taskBundle) {
        TaskCompleteDialog fragment = new TaskCompleteDialog();
        fragment.setArguments(taskBundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        taskBundle = getArguments();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(GlobalData.CONFIRM_COMPLETE).setMessage(GlobalData.CONFIRM_COMPLETE_MESSAGE);
        alertDialog.setPositiveButton("Complete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                int taskId = taskBundle.getInt("task_id");
                String where = "task_id=?";
                String[] whereArgs = {String.valueOf(taskId)};
                ContentValues values = new ContentValues();
                values.put("subtask_status", 1);
                getActivity().getContentResolver().update(DataProvider.SUBTASKS_URI, values, where, whereArgs);
                ContentValues taskValues = new ContentValues();
                taskValues.put("task_status", 1);
                getActivity().getContentResolver().update(DataProvider.TASKS_URI, taskValues, where, whereArgs);
                if(getActivity() instanceof ViewActivity) {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), CONFIRMED, null);
                } else if(getActivity() instanceof TasksDisplayActivity) {
                    Intent intent = new Intent();
                    intent.putExtra("task_id", taskId);
                    ((TasksDisplayActivity) getActivity()).onActivityResult(getTargetRequestCode(), CONFIRMED, intent);
                }
                getDialog().dismiss();
            }
        }).setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (getActivity() instanceof ViewActivity) {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), NOT_CONFIRMED, null);
                } else if(getActivity() instanceof TasksDisplayActivity) {
                    ((TasksDisplayActivity) getActivity()).onActivityResult(getTargetRequestCode(), TDA_NOT_CONFIRMED_CODE, null);
                }
                dialogInterface.cancel();
                getDialog().cancel();
            }
        });
        return alertDialog.create();
    }
}
