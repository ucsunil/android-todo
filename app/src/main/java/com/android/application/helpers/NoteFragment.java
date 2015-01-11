package com.android.application.helpers;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.application.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class NoteFragment extends DialogFragment implements View.OnClickListener {

    private EditText descriptiveNote;
    private Button save, discard;
    private final int NOTE_CODE = 3;
    private boolean noteCreated, noteEdited, noteDeleted;


    public static NoteFragment getFragment(Bundle bundle) {
        NoteFragment noteFragment = new NoteFragment();
        if(bundle != null) {
            noteFragment.setArguments(bundle);
        }
        return noteFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        descriptiveNote = (EditText) view.findViewById(R.id.descriptiveNote);
        save = (Button) view.findViewById(R.id.save);
        save.setOnClickListener(this);
        discard = (Button) view.findViewById(R.id.discard);
        discard.setOnClickListener(this);
        if(this.getArguments() != null) {
            initializeNote();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.enter_note);
        return dialog;
    }

    /**
     * Overriding this method to ensure that the dialog window is big enough when it is
     * displayed.
     */
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.save) {
            if(!TextUtils.isEmpty(descriptiveNote.getText().toString()) && this.getArguments() == null) {
                // Means that no note existed and now a note was created
                noteCreated = true;
                returnResult();
            } else if(this.getArguments() != null) {
                // Means that a note was already there
                // So it needs to be found whether this note was modified
                if(TextUtils.isEmpty(descriptiveNote.getText().toString())) {
                    // This means that all the text in the note were deleted
                    noteDeleted = true;
                    returnResult();
                } else if(descriptiveNote.getText().toString().equals(this.getArguments().getString("notes"))) {
                    // Means that the note was not modified
                    noteEdited = false;
                    returnResult();
                } else if(!descriptiveNote.getText().toString().equals(this.getArguments().getString("notes"))) {
                    // Means that the note was modified
                    noteEdited = true;
                    returnResult();
                }
            } else if(TextUtils.isEmpty(descriptiveNote.getText().toString()) && this.getArguments() == null) {
                // Means that the user did not create a note and there was
                // not note attached to this task
                returnNoNoteResult();
            }
            this.dismiss();
        } else if(view.getId() == R.id.discard) {
            this.dismiss();
        }
    }

    /**
     * This method is called when a note has been previously created for this task/subtask. This
     * method sets the content of the edittext field to the value for this note passed from the
     * calling fragment.
     */
    private void initializeNote() {
        Bundle bundle = this.getArguments();
        String text = bundle.getString("notes");
        descriptiveNote.setText(text);
    }

    private void returnNoNoteResult() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean("noteFlag", false);
        intent.putExtras(bundle);
        getTargetFragment().onActivityResult(getTargetRequestCode(), NOTE_CODE, intent);
    }

    private void returnResult() {
        String note = descriptiveNote.getText().toString();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean("noteFlag", true);
        bundle.putBoolean("noteCreated", noteCreated);
        bundle.putBoolean("noteEdited", noteEdited);
        bundle.putBoolean("noteDeleted", noteDeleted);
        bundle.putString("note", note);
        intent.putExtras(bundle);
        getTargetFragment().onActivityResult(getTargetRequestCode(), NOTE_CODE, intent);
    }

}
