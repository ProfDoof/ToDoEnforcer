package com.devilsadvocate.to_doenforcer;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskRepository {
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

    private static class insertAsyncTask extends AsyncTask<Task, Void, Void> {

        private TaskDao mAsyncTaskDao;

        insertAsyncTask(TaskDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Task... params) {
            mAsyncTaskDao.insertAll(params);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao mAsyncTaskDao;

        updateAsyncTask(TaskDao dao) { mAsyncTaskDao = dao; }

        @Override
        protected Void doInBackground(final Task... params)
        {
            for (Task task : params)
            {
                task.completed = true;
            }
            mAsyncTaskDao.completeAll(params);
            return null;
        }
    }
}
