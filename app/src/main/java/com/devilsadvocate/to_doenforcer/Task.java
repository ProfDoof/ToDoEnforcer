package com.devilsadvocate.to_doenforcer;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Task {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "task_name")
    @NonNull
    public String taskName;

    @ColumnInfo(name = "task_description")
    public String taskDescription;

    @ColumnInfo(name = "completed")
    public boolean completed;

    @ColumnInfo(name = "repeat_schedule")
    public String repeatSchedule;

    @ColumnInfo(name = "created_date")
    @NonNull
    public Date created_date;

    @ColumnInfo(name = "priority")
    public int priority;
}
