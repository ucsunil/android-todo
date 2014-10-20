package com.android.application.helpers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.android.application.GlobalData;

/**
 * Created by umonssu on 10/19/14.
 */
public class TaskCompleteDialog extends DialogFragment {

    private final int CONFIRMED = 1;

    public static TaskCompleteDialog newInstance() {
        TaskCompleteDialog fragment = new TaskCompleteDialog();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(GlobalData.CONFIRM_COMPLETE).setMessage(GlobalData.CONFIRM_COMPLETE_MESSAGE);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Complete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), CONFIRMED, null);
            }
        }).setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.cancel();
                getDialog().cancel();
            }
        });
        return alertDialog.create();
    }
}
