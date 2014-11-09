package com.android.application.helpers;

import android.app.Activity;
import android.app.Fragment;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

import com.android.application.R;
import com.android.application.fragments.views.TaskViewFragment;

/**
 * Created by umonssu on 11/9/14.
 */
public class ActionModeListener implements AbsListView.MultiChoiceModeListener {

    Activity context;
    ActionMode actionMode;
    ListView listView;
    TaskViewFragment taskViewFragment;
    private int fragmentFlag;

    public ActionModeListener(Activity context, Fragment fragment, ListView listView, int fragmentFlag) {
        this.context = context;
        this.listView = listView;
        this.fragmentFlag = fragmentFlag;
        if(fragmentFlag == 1) {
            taskViewFragment = (TaskViewFragment) fragment;
        }
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = context.getMenuInflater();
        inflater.inflate(R.menu.context, menu);
        actionMode = mode;
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        boolean result = false;
        if(fragmentFlag == 1) {
            result = taskViewFragment.performActions(menuItem);
        }
        actionMode.finish();
        return result;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {

    }
}
