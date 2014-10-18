package com.android.application.fragments.views;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.application.R;

public class SubtaskViewFragment extends Fragment {

    public static SubtaskViewFragment newInstance(Bundle dataBundle) {
        SubtaskViewFragment fragment = new SubtaskViewFragment();
        fragment.setArguments(dataBundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_subtask, container, false);
    }

}
