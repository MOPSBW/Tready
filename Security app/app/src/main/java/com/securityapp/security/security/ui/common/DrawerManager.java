package com.securityapp.security.security.ui.common;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import com.securityapp.security.security.R;

import javax.inject.Inject;

/**
 * Helper class for DrawerLayout operations
 */

public class DrawerManager {

    private NavigationController mNavigationController;
    private DrawerLayout mDrawerLayout;

    @Inject
    public DrawerManager(NavigationController navigationController){
        mNavigationController = navigationController;
    }

    public void buildDrawerLayout(DrawerLayout drawerLayout, NavigationView navigationView){
        mDrawerLayout = drawerLayout;
        navigationView.setNavigationItemSelectedListener(item -> {
            onDrawerItemSelected(item);
            return true;
        });
    }

    private void onDrawerItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.nav_devices:
                mNavigationController.navigateToDevices();
                break;
            case R.id.nav_settings:
                mNavigationController.navigateToSettings();
                break;
            case R.id.nav_contribute:
                mNavigationController.navigateToBrowserView("https://bitbucket.org/bkrupp/skunkworks", mDrawerLayout.getContext());
                break;
            case R.id.nav_help:
                Toast.makeText(mDrawerLayout.getContext(), "Open link to documentation when completed", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_feedback:
                Toast.makeText(mDrawerLayout.getContext(), "Open link to Google Play store feedback when completed", Toast.LENGTH_SHORT).show();
                break;
        }
        closeDrawer();
    }

    public void openDrawer(){
        mDrawerLayout.openDrawer(GravityCompat.START, true);
    }

    public void closeDrawer(){
        mDrawerLayout.closeDrawer(Gravity.START,true);
    }

    public boolean isDrawerOpen(){
        return mDrawerLayout.isDrawerOpen(Gravity.START);
    }

    public void enableDrawer(boolean isEnabled){
        if(!isEnabled){
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }else{
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }
}
