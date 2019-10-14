package com.devilsadvocate.to_doenforcer;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TaskRepository
{
    private TaskDao mTaskDao;
    private LiveData<List<Task>> mAllTasks;
    private LiveData<List<Task>> mUncompletedTasks;

    TaskRepository(Application application)
    {
        AppDatabase db = AppDatabase.getDatabase(application);
        mTaskDao = db.taskDao();
        mAllTasks = mTaskDao.getAllTasks();
        mUncompletedTasks = mTaskDao.getAllTasksByCompletedStatus(false);
    }

    public LiveData<List<Task>> getAllTasks()
    {
        return mAllTasks;
    }

    public LiveData<List<Task>> getAllUncompletedTasks() { return mUncompletedTasks; }

    public void insert (Task... task)
    {
        new insertAsyncTask(mTaskDao).execute(task);
    }

    public void completeTask(Task task) { new updateAsyncTask(mTaskDao).execute(task); }

    public void uncompleteTasks(Task... tasks) { new updateAsyncTask(mTaskDao).execute(tasks); }

    public List<Task> getRepeatTasksByDay(String day)
    {
        try
        {
            return (new getRepeatsAsyncTask(mTaskDao)).execute("%"+day+"%").get();
        }
        catch (ExecutionException | InterruptedException e)
        {
            return new ArrayList<>();
        }
    }

    private static class getRepeatsAsyncTask extends AsyncTask<String, Void, List<Task>>
    {
        private TaskDao mAsyncTaskDao;

        getRepeatsAsyncTask(TaskDao dao) { mAsyncTaskDao = dao; }

        @Override
        protected List<Task> doInBackground(final String... params)
        {
            return mAsyncTaskDao.getAllTasksByRepeatDay(params[0]);
        }
    }

    private static class insertAsyncTask extends AsyncTask<Task, Void, Void>
    {

        private TaskDao mAsyncTaskDao;

        insertAsyncTask(TaskDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Task... params)
        {
            mAsyncTaskDao.insertAll(params);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<Task, Void, Void>
    {
        private TaskDao mAsyncTaskDao;

        updateAsyncTask(TaskDao dao) { mAsyncTaskDao = dao; }

        @Override
        protected Void doInBackground(final Task... params)
        {
            for (Task task : params)
            {
                task.completed = !task.completed;
            }
            mAsyncTaskDao.completeAll(params);
            return null;
        }
    }
}
