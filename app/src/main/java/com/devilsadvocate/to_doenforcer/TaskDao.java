package com.devilsadvocate.to_doenforcer;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM task ORDER BY priority DESC")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM task WHERE completed = :completedStatus ORDER BY priority DESC, created_date ASC")
    LiveData<List<Task>> getAllTasksByCompletedStatus(boolean completedStatus);

    @Query("SELECT * FROM task WHERE uid IN (:taskIds) ORDER BY priority DESC")
    LiveData<List<Task>> getAllTasksByIds(int[] taskIds);

    @Query("SELECT * FROM task WHERE repeat_schedule like :day")
    LiveData<List<Task>> getAllTasksByRepeatDay(String day);

    @Query("DELETE FROM task")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Task... tasks);

    @Delete
    void delete(Task task);

    @Update
    void completeAll(Task... params);
}
