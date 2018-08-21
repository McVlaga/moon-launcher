package io.github.mcvlaga.moonlauncher.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import io.github.mcvlaga.moonlauncher.R;
import io.github.mcvlaga.moonlauncher.home.apps.data.AppModel;

public class HiddenAppsDialogFragment extends DialogFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int APPS_LOADER_ID = 300;

    private HiddenAppsAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.hidden_apps_dialog, null);
        mProgressBar = dialogView.findViewById(R.id.hidden_apps_progress_bar);
        mRecyclerView = dialogView.findViewById(R.id.hidden_apps_recycler_view);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setClickable(true);
        mAdapter = new HiddenAppsAdapter();
        mRecyclerView.setAdapter(mAdapter);

        getActivity().getSupportLoaderManager().initLoader(APPS_LOADER_ID, null, this);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Hidden apps");
        dialogBuilder.setPositiveButton(
                getString(R.string.add_edit_task_save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int numOfApps = mAdapter.getItemCount();
                        int numberOfHiddenApps = 0;
                        for (int i = 0; i < numOfApps; i++) {
                            AppModel app = mAdapter.getApp(i);
                            if (mAdapter.getHiddenState(i)) {
                                numberOfHiddenApps++;
                                ((SettingsFragment) getTargetFragment()).addHiddenApp(app.getPackage());
                            } else {
                                ((SettingsFragment) getTargetFragment()).removeHiddenApp(app.getPackage());
                            }
                        }
                        Intent intent = new Intent();
                        intent.putExtra("number", numberOfHiddenApps);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    }
                });
        dialogBuilder.setNegativeButton(getString(R.string.add_edit_task_cancel), null);
        return dialogBuilder.create();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return ((SettingsFragment) getTargetFragment()).createAppsLoader();
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
