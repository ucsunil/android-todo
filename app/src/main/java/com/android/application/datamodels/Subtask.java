package com.android.application.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sunil on 10/14/14.
 */
public class Subtask implements Parcelable {

    private int subtaskId, taskId;
    private String subtask;
    private boolean hasNote = false, status = false;
    private String description;
    private boolean[] booleans = new boolean[2];

    public Subtask() {}

    private Subtask(Parcel in) {
        this.subtaskId = in.readInt();
        this.taskId = in.readInt();
        this.subtask = in.readString();
        this.description = in.readString();
        in.readBooleanArray(booleans);
    }

    public int getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(int subtaskId) {
        this.subtaskId = subtaskId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getSubtask() {
        return subtask;
    }

    public void setSubtask(String subtask) {
        this.subtask = subtask;
    }

    public boolean isHasNote() {
        return hasNote;
    }

    public void setHasNote(boolean hasNote) {
        this.hasNote = hasNote;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBooleans() {
        this.booleans[0] = this.hasNote;
        this.booleans[1] = this.status;
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(taskId);
        out.writeInt(subtaskId);
        out.writeString(subtask);
        out.writeString(description);
        out.writeBooleanArray(booleans);
    }

    public static final Creator<Subtask> CREATOR = new Creator<Subtask>() {

        @Override
        public Subtask createFromParcel(Parcel parcel) {
            return new Subtask(parcel);
        }

        @Override
        public Subtask[] newArray(int size) {
            return new Subtask[size];
        }
    };
}
