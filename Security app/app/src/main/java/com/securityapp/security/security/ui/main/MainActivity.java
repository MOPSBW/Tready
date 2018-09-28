package com.securityapp.security.security.ui.main;

import android.Manifest;
import android.app.DownloadManager;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.securityapp.security.security.R;
import com.securityapp.security.security.config.Config;
import com.securityapp.security.security.ui.BaseActivity;
import com.securityapp.security.security.ui.common.DrawerManager;
import com.securityapp.security.security.ui.common.NavigationController;
import com.securityapp.security.security.utils.AlertDialogUtils;
import com.securityapp.security.security.utils.FileDownloader;

import java.net.CookieManager;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * MainActivity is the activity host for all fragments in the application after the LoginActivity.
 */
public class MainActivity extends BaseActivity implements HasSupportFragmentInjector, OnSaveEventVideoListener, FragmentManager.OnBackStackChangedListener{

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Inject
    NavigationController navigationController;

    @Inject
    DrawerManager drawerManager;

    @Inject
    CookieManager cookieManager;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nvView)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindString(R.string.events_title) String activityTitle;
    @BindString(R.string.signOutMsg) String signOutMsg;
    @BindString(R.string.confirmationMessage) String confirmationMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        ButterKnife.bind(this);

        setupToolbar();

        navigationController.setBackstackListener(this::onBackStackChanged);

        drawerManager.buildDrawerLayout(mDrawerLayout,navigationView);

        if(savedInstanceState == null){
            navigationController.navigateToEvents();
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    /**
     * Setups the supportActionBar and enables home button
     */
    private void setupToolbar(){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case android.R.id.home:
                if(navigationController.isNavigationFragmentVisible()){
                    drawerManager.openDrawer();
                }else{
                    navigationController.navigateBack();
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerManager.isDrawerOpen()){
            drawerManager.closeDrawer();
        }else{
            if(getSupportFragmentManager().getBackStackEntryCount() == 1){
                showExitAlertDialog();
            }else{
                navigationController.navigateBack();
            }
        }
    }

    /**
     * Prompt user before closing application
     */
    private void showExitAlertDialog(){
        AlertDialogUtils.showAlertDialog(this, "Exit app", confirmationMsg, (dialog, which) -> {
            if(which == DialogInterface.BUTTON_POSITIVE){
                finish();
            }else{
                dialog.dismiss();
            }
        });
    }

    @Override
    public void saveEventVideoSelected(int eventId) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Config.MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
        }else{
            FileDownloader fileDownloader = new FileDownloader((DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE), cookieManager);
            fileDownloader.downloadFile("event" + eventId, String.format(Config.EVENT_VIDEO_DOWNLOAD_URL, eventId));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Config.MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permissions granted
                } else {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        //showStoragePermissionRationale();
                    }else{
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "No storage permissions", Snackbar.LENGTH_LONG);
                        snackbar.setAction("Go to settings", view -> {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        });
                        snackbar.show();
                    }
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackStackChanged() {
        boolean isRoot = navigationController.isNavigationFragmentVisible();
        drawerManager.enableDrawer(isRoot);
    }
}