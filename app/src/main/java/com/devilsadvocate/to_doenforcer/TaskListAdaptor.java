package com.devilsadvocate.to_doenforcer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskListAdaptor extends RecyclerView.Adapter<TaskListAdaptor.TaskViewHolder> {

    TaskListAdaptor(Context context) {mInflater = LayoutInflater.from(context); }

    TaskListAdaptor(Context context, TaskAdaptorListener listener)
    {
        mInflater = LayoutInflater.from(context);
        this.taskOnClickListener = listener;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView taskItemView;
        private final Button completeTaskButton;

        private TaskViewHolder(View itemView) {
            super(itemView);
            taskItemView = itemView.findViewById(R.id.task_title);
            completeTaskButton = itemView.findViewById(R.id.task_complete);
        }
    }

    private final LayoutInflater mInflater;
    private List<Task> mTasks; // Cached copy of tasks

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.activity_listview_listitem, parent, false);
        return new TaskViewHolder(itemView);
    }

    public TaskAdaptorListener taskOnClickListener;

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position)
    {
        if (mTasks != null)
        {
            final Task current = mTasks.get(position);
            holder.taskItemView.setText(current.taskName);
            holder.completeTaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taskOnClickListener.completeTaskOnClick(current);
                }
            });
        }
        else
        {
            // Covers the case of data not being ready yet.
            holder.taskItemView.setText("No Tasks");
        }
    }

    void setTasks(List<Task> tasks){
        mTasks = tasks;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mTasks has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mTasks != null)
            return mTasks.size();
        else return 0;
    }

    public interface TaskAdaptorListener
    {
        void completeTaskOnClick(Task task);
    }
}