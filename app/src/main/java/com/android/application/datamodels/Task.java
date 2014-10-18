package com.android.application.datamodels;

/**
 * Created by umonssu on 10/14/14.
 */
public class Task {

    int taskId;
    String date, time, task, description;
    boolean hasNote, hasSubtasks, status;

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

    @Override
    public String toString() {
        return this.task;
    }
}
