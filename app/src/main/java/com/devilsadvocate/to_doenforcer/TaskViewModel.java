package com.devilsadvocate.to_doenforcer;

import android.app.Application;
import android.view.View;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskViewModel extends AndroidViewModel
{
    private TaskRepository mRepository;
    private LiveData<List<Task>> mAllTasks;
    private LiveData<List<Task>> mUncompletedTasks;

    public TaskViewModel (Application application)
    {
        super(application);
        mRepository = new TaskRepository(application);
        mAllTasks = mRepository.getAllTasks();
        mUncompletedTasks = mRepository.getAllUncompletedTasks();
    }

    public LiveData<List<Task>> getAllTasks() { return mAllTasks; }

    public LiveData<List<Task>> getAllUncompletedTasks() { return mUncompletedTasks; }

    public void insert(Task... tasks) { mRepository.insert(tasks);}

    public void completeTask(Task task) { mRepository.completeTask(task); }
}
