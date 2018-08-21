package io.github.mcvlaga.moonlauncher.home.tasks.list;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import io.github.mcvlaga.moonlauncher.R;
import io.github.mcvlaga.moonlauncher.home.tasks.AddEditTaskDialogFragment;
import io.github.mcvlaga.moonlauncher.home.tasks.TasksFilterType;
import io.github.mcvlaga.moonlauncher.home.tasks.data.Task;

import static io.github.mcvlaga.moonlauncher.home.tasks.TasksFilterType.ACTIVE_TASKS;
import static io.github.mcvlaga.moonlauncher.home.tasks.TasksFilterType.ALL_TASKS;
import static io.github.mcvlaga.moonlauncher.home.tasks.TasksFilterType.COMPLETED_TASKS;

public class TasksListFragment extends Fragment implements TasksListContract.View, View.OnClickListener,
        AdapterView.OnItemClickListener, Spinner.OnItemSelectedListener {

    TasksListContract.Presenter mPresenter;

    TasksListener mTasksListener = new TasksListener() {

        @Override
        public void onActivateTaskClick(Task task) {
            mPresenter.activateTask(task);
        }

        @Override
        public void onCompleteTaskClick(Task task) {
            mPresenter.completeTask(task);
        }
    };

    private static final int ADD_EDIT_DIALOG_REQUEST_CODE = 1;

    private TasksFilterType filterType;

    private FloatingActionButton addTaskFab;
    private FloatingActionButton removeCompletedTasksFab;
    private Spinner listsSpinner;
    private ListView mTasksListView;
    private TasksListAdapter mAdapter;
    private ImageButton tasksListSpinnerButton;

    public TasksListFragment() {
    }

    public static TasksListFragment newInstance() {
        return new TasksListFragment();
    }

    @Override
    public void setPresenter(TasksListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tasks_list_fragment, container, false);

        mTasksListView = root.findViewById(R.id.tasks_list_view);
        addTaskFab = root.findViewById(R.id.add_task_fab);
        removeCompletedTasksFab = root.findViewById(R.id.remove_completed_tasks_fab);

        listsSpinner = root.findViewById(R.id.tasks_list_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.tasks_list_options, R.layout.tasks_list_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listsSpinner.setAdapter(adapter);
        tasksListSpinnerButton = root.findViewById(R.id.tasks_list_spinner_button);

        addTaskFab.setOnClickListener(this);
        removeCompletedTasksFab.setOnClickListener(this);
        if (filterType == COMPLETED_TASKS) {
            addTaskFab.hide();
            removeCompletedTasksFab.show();
            listsSpinner.setSelection(2, false);
        } else if (filterType == ACTIVE_TASKS) {
            removeCompletedTasksFab.hide();
            addTaskFab.show();
            listsSpinner.setSelection(1, false);
        } else {
            removeCompletedTasksFab.hide();
            addTaskFab.show();
            listsSpinner.setSelection(0, false);
        }
        listsSpinner.setOnItemSelectedListener(this);
        mAdapter = new TasksListAdapter(mTasksListView, new ArrayList<Task>(), getActivity(), mTasksListener);
        mTasksListView.setAdapter(mAdapter);
        mTasksListView.setOnItemClickListener(this);
        return root;
    }

    private void showAddEditTaskDialog(Task task) {
        DialogFragment dialogFrag = new AddEditTaskDialogFragment();
        Bundle args = new Bundle();
        if (task != null) {
            args.putString(AddEditTaskDialogFragment.ID_KEY, task.getId());
            args.putString(AddEditTaskDialogFragment.DIALOG_TITLE_KEY, getString(R.string.edit_task_title));
            args.putString(AddEditTaskDialogFragment.TITLE_KEY, task.getTitle());
            args.putLong(AddEditTaskDialogFragment.TIME_KEY, task.getTime());
            args.putLong(AddEditTaskDialogFragment.DATE_KEY, task.getDate());
            args.putInt(AddEditTaskDialogFragment.COLOR_KEY, task.getColor());
        } else {
            args.putString(AddEditTaskDialogFragment.DIALOG_TITLE_KEY, getString(R.string.add_task_title));
        }
        dialogFrag.setArguments(args);
        dialogFrag.setTargetFragment(this, ADD_EDIT_DIALOG_REQUEST_CODE);
        dialogFrag.show(getFragmentManager(), null);
    }

    private void showRemoveCompletedDialog() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete all completed to-dos");
        builder.setMessage("Are you sure you wanna delete all?")
                .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mPresenter.removeCompletedTasks();
                    }
                })
                .setNegativeButton("Cancel", null);
        AlertDialog dialog =  builder.create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_EDIT_DIALOG_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String title = data.getStringExtra(AddEditTaskDialogFragment.TITLE_KEY);
                long date = data.getLongExtra(AddEditTaskDialogFragment.DATE_KEY, 0);
                long time = data.getLongExtra(AddEditTaskDialogFragment.TIME_KEY, 0);
                int color = data.getIntExtra(AddEditTaskDialogFragment.COLOR_KEY,
                        getActivity().getResources().getColor(R.color.colorTasksOne));
                String id = data.getStringExtra(AddEditTaskDialogFragment.ID_KEY);
                if (id == null) {
                    mPresenter.saveTask(
                            new Task(title, date, time, color, false));
                } else {
                    mPresenter.saveTask(
                            new Task(id, title, date, time, color, false));
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_task_fab:
                showAddEditTaskDialog(null);
                break;
            case R.id.remove_completed_tasks_fab:
                showRemoveCompletedDialog();
                break;
        }
    }

    @Override
    public void showTasks(Cursor tasksCursor) {
        if (tasksCursor != null) {
            tasksCursor.moveToPosition(-1);
            ArrayList<Task> tasksData = new ArrayList<>();
            while (tasksCursor.moveToNext()) {
                tasksData.add(Task.from(tasksCursor));
            }
            mAdapter.setData(tasksData);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Task task = mAdapter.getItem(position);
        showAddEditTaskDialog(task);
    }

    @Override
    public void showUndoSnackBar(final Task task) {
        String undo = getActivity().getString(R.string.undo_complete_task);
        String taskIsDeleted = getActivity().getString(R.string.task_completed);

        Snackbar snackbar = Snackbar
                .make(getActivity().findViewById(android.R.id.content), taskIsDeleted, Snackbar.LENGTH_LONG)
                .setAction(undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPresenter.saveTask(task);
                    }
                });
        snackbar.show();
    }

    @Override
    public void showAllFilterLabel() {
        filterType = ALL_TASKS;
    }

    @Override
    public void showCompletedFilterLabel() {
        filterType = COMPLETED_TASKS;
    }

    @Override
    public void showActiveFilterLabel() {
        filterType = ACTIVE_TASKS;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = parent.getItemAtPosition(position).toString();
        if (selectedItem.equals("All tasks")) {
            mPresenter.setFilterType(ALL_TASKS);
            removeCompletedTasksFab.hide();
            addTaskFab.show();
        } else if (selectedItem.equals("Active tasks")) {
            mPresenter.setFilterType(ACTIVE_TASKS);
            removeCompletedTasksFab.hide();
            addTaskFab.show();
        } else if (selectedItem.equals("Completed tasks")) {
            mPresenter.setFilterType(COMPLETED_TASKS);
            addTaskFab.hide();
            removeCompletedTasksFab.show();
        }
        tasksListSpinnerButton.performClick();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface TasksListener {
        void onActivateTaskClick(Task task);
        void onCompleteTaskClick(Task task);
    }
}
