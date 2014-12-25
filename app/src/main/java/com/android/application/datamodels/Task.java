package com.android.application.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sunil on 10/14/14.
 */
public class Task implements Parcelable{

    int taskId;
    String date, time, task, description;
    boolean hasNote, hasSubtasks, status;
    private boolean[] booleans = new boolean[3];

    public Task() {};

    private Task(Parcel in) {
        this.taskId = in.readInt();
        this.date = in.readString();
        this.time = in.readString();
        this.task = in.readString();
        this.description = in.readString();
        in.readBooleanArray(booleans);
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getHasNote() {
        return hasNote;
    }

    public void setHasNote(boolean hasNote) {
        this.hasNote = hasNote;
    }

    public boolean getHasSubtasks() {
        return hasSubtasks;
    }

    public void setHasSubtasks(boolean hasSubtasks) {
        this.hasSubtasks = hasSubtasks;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setBooleans() {
        this.booleans[0] = this.hasNote;
        this.booleans[1] = this.status;
        this.booleans[2] = this.hasSubtasks;
    }

    @Override
    public String toString() {
        return this.task;
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(taskId);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(task);
        dest.writeString(description);
        dest.writeBooleanArray(booleans);
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {

        @Override
        public Task createFromParcel(Parcel parcel) {
            return new Task(parcel);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
