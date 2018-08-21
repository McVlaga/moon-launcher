package io.github.mcvlaga.moonlauncher.home.apps.drawer;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import io.github.mcvlaga.moonlauncher.R;
import io.github.mcvlaga.moonlauncher.home.apps.data.AppModel;
import io.github.mcvlaga.moonlauncher.settings.SettingsActivity;

public class AppsDrawerFragment extends Fragment implements AppsDrawerContract.View,
        AppsDrawerAdapter.OnAppClickListener, View.OnClickListener {

    private static final int APP_UNINSTALL_CODE = 22;
    private static final int APP_INFO_CODE = 33;

    private RecyclerView mAppsDrawerRecyclerView;
    private AppsDrawerAdapter mAppsDrawerAdapter;
    private ProgressBar mAppsDrawerProgressBar;

    private AppsDrawerContract.Presenter mPresenter;

    public AppsDrawerFragment() {

    }

    public static AppsDrawerFragment newInstance() {
        return new AppsDrawerFragment();
    }

    @Override
    public void setPresenter(AppsDrawerContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.apps_drawer_fragment, container, false);

        mAppsDrawerRecyclerView = root.findViewById(R.id.apps_drawer_rv);
        int numberOfColumns = 4;
        mAppsDrawerRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        mAppsDrawerAdapter = new AppsDrawerAdapter();
        mAppsDrawerAdapter.setOnItemClickListener(this);
        mAppsDrawerRecyclerView.setAdapter(mAppsDrawerAdapter);

        mAppsDrawerProgressBar = root.findViewById(R.id.apps_progress_bar);

        ImageButton settingsButton = root.findViewById(R.id.app_settings_button);
        settingsButton.setOnClickListener(this);
        return root;
    }

    @Override
    public void showApps(Cursor cursor) {
        mAppsDrawerAdapter.swapCursor(cursor);
    }

    @Override
    public void onAppClicked(View view, int position) {
        PackageManager packageManager = getActivity().getPackageManager();
        AppModel app = mAppsDrawerAdapter.getApp(position);
        if (app != null) {
            Intent intent = packageManager.getLaunchIntentForPackage(app.getPackage());
            if (intent != null) {
                int left = 0, top = 0;
                int width = view.getMeasuredWidth(), height = view.getMeasuredHeight();
                ActivityOptions opts = ActivityOptions.makeClipRevealAnimation(view, left, top, width, height);
                startActivity(intent, opts.toBundle());
            }
        }
    }

    @Override
    public void onAppLongClicked(View view, final int position) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        popup.inflate(R.menu.apps_drawer_options_menu);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AppModel app = mAppsDrawerAdapter.getApp(position);
                switch (item.getItemId()) {
                    case R.id.app_info:
                        try {
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + app.getPackage()));
                            startActivityForResult(intent, APP_INFO_CODE);

                        } catch (ActivityNotFoundException e) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                            startActivityForResult(intent, APP_INFO_CODE);
                        }
                        break;
                    case R.id.app_hide:
                        mPresenter.addHiddenApp(app.getPackage());
                        break;
                    case R.id.app_add:
                        mPresenter.addQuickApp(app.getPackage());
                        break;
                    case R.id.app_uninstall:
                        Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                        uninstallIntent.setData(Uri.parse("package:" + app.getPackage()));
                        startActivityForResult(uninstallIntent, APP_UNINSTALL_CODE);
                        break;
                }
                return false;
            }
        });
        popup.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == APP_UNINSTALL_CODE) {
            if(resultCode == Activity.RESULT_OK){
                mPresenter.reloadApps();
            }
        }
        if (requestCode == APP_INFO_CODE) {
            mPresenter.reloadApps();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }
}
