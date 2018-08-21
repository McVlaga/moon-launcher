package io.github.mcvlaga.moonlauncher.home;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import io.github.mcvlaga.moonlauncher.R;
import io.github.mcvlaga.moonlauncher.home.apps.data.AppModel;
import io.github.mcvlaga.moonlauncher.home.apps.quick.AppsQuickAdapter;
import io.github.mcvlaga.moonlauncher.home.tasks.AddEditTaskDialogFragment;
import io.github.mcvlaga.moonlauncher.home.tasks.data.Task;
import io.github.mcvlaga.moonlauncher.home.tasks.list.TasksListActivity;
import io.github.mcvlaga.moonlauncher.utils.DragAndDropHelper;

public class HomeFragment extends Fragment
        implements HomeContract.View,
        AppsQuickAdapter.OnAppClickListener,
        SearchViewHolder.OnSearchClickListener {

    private HomeContract.Presenter mPresenter;

    TasksListener mTasksListener = new TasksListener() {

        @Override
        public void onTasksListClick() {
            showTasksListDialog();
        }

        @Override
        public void onAddTaskClick(Task task) {
            showAddEditTaskDialog(task);
        }

        @Override
        public void onEditTaskClick(Task task) {
            showAddEditTaskDialog(task);
        }

        @Override
        public void onCompleteTaskClick(Task task) {
            mPresenter.completeTask(task);
        }
    };

    private static final int ADD_EDIT_DIALOG_REQUEST_CODE = 1;

    private RecyclerView mHomeRecyclerView;
    private HomeAdapter mHomeAdapter;
    private DragAndDropHelper mDragAndDropHelper;

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
        mDragAndDropHelper.setDragable(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home_fragment, container, false);
        setUpRecycleView(root);
        return root;
    }

    @Override
    public void setPresenter(@NonNull HomeContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private void setUpRecycleView(View root) {
        mHomeRecyclerView = root.findViewById(R.id.home_rv);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mHomeRecyclerView.setLayoutManager(llm);
        mHomeRecyclerView.setHasFixedSize(true);
        mHomeAdapter = new HomeAdapter(getActivity(), mTasksListener, this, this);
        mDragAndDropHelper = new DragAndDropHelper(mHomeAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(mDragAndDropHelper);
        mHomeRecyclerView.setAdapter(mHomeAdapter);
        touchHelper.attachToRecyclerView(mHomeRecyclerView);
    }

    @Override
    public void showWeather(Cursor weatherData) {
        mHomeAdapter.swapWeatherCursor(weatherData);
    }

    @Override
    public void showTasks(Cursor tasksData) {
        mHomeAdapter.swapTasksCursor(tasksData);
    }

    @Override
    public void showQuickApps(Cursor cursor) {
        mHomeAdapter.swapQuickAppsCursor(cursor);
    }

    @Override
    public void showSearch() {
        mHomeAdapter.showSearch();
    }

    @Override
    public void showUndoSnackBar(final Task task) {
        String undo = getActivity().getString(R.string.undo_complete_task);
        String taskCompleted = getActivity().getString(R.string.task_completed);

        Snackbar snackbar = Snackbar
                .make(getActivity().findViewById(android.R.id.content), taskCompleted, Snackbar.LENGTH_LONG)
                .setAction(undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPresenter.saveTask(task);
                    }
                });
        snackbar.show();
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

    private void showTasksListDialog() {
        Intent intent = new Intent(getActivity(), TasksListActivity.class);
        startActivity(intent);
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
    public void onAppClicked(View view, AppModel app) {
        mDragAndDropHelper.setDragable(false);
        PackageManager packageManager = getActivity().getPackageManager();
        if (app != null) {
            Intent intent = packageManager.getLaunchIntentForPackage(app.getPackage());
            if (intent != null) {
                int left = 0, top = 0;
                int width = view.getMeasuredWidth(), height = view.getMeasuredHeight();
                ActivityOptions opts = ActivityOptions.makeClipRevealAnimation(view, left, top, width, height);
                startActivityForResult(intent, 0, opts.toBundle());
            }
        }
    }

    @Override
    public void onAppLongClicked(View view, final AppModel app) {
        mDragAndDropHelper.setDragable(false);

        PopupMenu popup = new PopupMenu(getActivity(), view);
        popup.inflate(R.menu.apps_quick_options_menu);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mDragAndDropHelper.setDragable(true);
                switch (item.getItemId()) {
                    case R.id.app_info:
                        try {
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + app.getPackage()));
                            startActivityForResult(intent, 1);

                        } catch (ActivityNotFoundException e) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                            startActivityForResult(intent, 1);
                        }
                        break;
                    case R.id.app_remove:
                        mPresenter.removeQuickApp(app.getPackage());
                        break;
                    case R.id.app_uninstall:
                        Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                        uninstallIntent.setData(Uri.parse("package:" + app.getPackage()));
                        startActivityForResult(uninstallIntent, 2);
                        break;
                }
                return false;
            }
        });

        popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                mDragAndDropHelper.setDragable(true);
            }
        });
        popup.show();
    }

    @Override
    public void onSearchClicked() {
        ComponentName globalSearchActivity =
                new ComponentName("com.google.android.googlequicksearchbox",
                        "com.google.android.apps.gsa.queryentry.QueryEntryActivity");

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(globalSearchActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

    public interface TasksListener {
        void onTasksListClick();
        void onAddTaskClick(Task task);
        void onEditTaskClick(Task task);
        void onCompleteTaskClick(Task task);
    }
}
