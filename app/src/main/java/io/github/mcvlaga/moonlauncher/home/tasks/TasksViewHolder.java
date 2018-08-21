package io.github.mcvlaga.moonlauncher.home.tasks;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.mcvlaga.moonlauncher.R;
import io.github.mcvlaga.moonlauncher.home.HomeFragment;
import io.github.mcvlaga.moonlauncher.home.tasks.data.Task;

public class TasksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    public ListView mTasksListView;
    public TasksAdapter mAdapter;
    private TextView mEmptyListTextView;

    private final HomeFragment.TasksListener mTasksListener;

    public TasksViewHolder(View view, HomeFragment.TasksListener tasksListener) {
        super(view);
        mTasksListView = view.findViewById(R.id.tasks_list_view);
        mTasksListener = tasksListener;
        mAdapter = new TasksAdapter(mTasksListView, new ArrayList<Task>(), view.getContext(), tasksListener);
        mTasksListView.setAdapter(mAdapter);
        mTasksListView.setVerticalScrollBarEnabled(false);

        ImageButton addButton = view.findViewById(R.id.add_button);
        ImageButton listButton = view.findViewById(R.id.list_button);
        addButton.setOnClickListener(this);
        listButton.setOnClickListener(this);

        mEmptyListTextView = view.findViewById(R.id.empty_tasks_list);
        mTasksListView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_button:
                mTasksListener.onAddTaskClick(null);
                break;
            case R.id.list_button:
                mTasksListener.onTasksListClick();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Task task = mAdapter.getItem(position);
        mTasksListener.onEditTaskClick(task);
    }

    public void displayEmptyListMessage(boolean display) {
        if (display) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyListTextView.setVisibility(View.GONE);
        }
    }
}
