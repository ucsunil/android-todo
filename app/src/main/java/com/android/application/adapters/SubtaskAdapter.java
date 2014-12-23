package com.android.application.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.android.application.R;
import com.android.application.datamodels.Subtask;

import java.util.ArrayList;

/**
 * Created by umonssu on 10/14/14.
 */
public class SubtaskAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Subtask> subtasks;
    boolean parentCompleteFlag = false;

    public SubtaskAdapter(Context context, ArrayList<Subtask> subtasks) {
        this.context = context;
        this.subtasks = subtasks;
    }

    @Override
    public int getCount() {
        return subtasks.size();
    }

    @Override
    public Object getItem(int position) {
        return subtasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.subtask_item, parent,false);
        final Subtask subtask = subtasks.get(position);

        TextView title = (TextView) rowView.findViewById(R.id.subtask);
        title.setText(subtask.getSubtask());
        final Switch status = (Switch) rowView.findViewById(R.id.status);
        status.setChecked(subtask.isStatus());
        status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                subtask.setStatus(checked);
            }
        });
        if(parentCompleteFlag) {
            title.setTextColor(Color.GRAY);
            status.setEnabled(false);
        }
        return rowView;
    }

    public void setParentCompleteFlag(boolean flag) {
        parentCompleteFlag = flag;
    }
}
