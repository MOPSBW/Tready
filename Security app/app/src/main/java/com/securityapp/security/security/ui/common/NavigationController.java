package com.securityapp.security.security.ui.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.securityapp.security.security.R;
import com.securityapp.security.security.ui.device.DeviceFragment;
import com.securityapp.security.security.ui.event.EventFragment;
import com.securityapp.security.security.ui.eventVideo.EventVideoFragment;
import com.securityapp.security.security.ui.main.MainActivity;
import com.securityapp.security.security.ui.settings.SettingsFragment;

import javax.inject.Inject;

/**
 * Helper class for navigation
 */

public class NavigationController {
    private final int containerId;
    private final FragmentManager fragmentManager;

    @Inject
    public NavigationController(MainActivity mainActivity){
        this.containerId = R.id.flContent;
        this.fragmentManager = mainActivity.getSupportFragmentManager();
    }

    public void setBackstackListener(FragmentManager.OnBackStackChangedListener backstackListener){
        fragmentManager.addOnBackStackChangedListener(backstackListener);
    }

    public void navigateToEvents(){
        EventFragment eventFragment = EventFragment.newInstance();
        displayFragment(eventFragment);
    }

    public void navigateToEventVideo(int eventId){
        EventVideoFragment eventVideoFragment = EventVideoFragment.newInstance(eventId);
        displayFragment(eventVideoFragment);
    }

    public void navigateToDevices(){
        DeviceFragment deviceFragment = new DeviceFragment();
        displayFragment(deviceFragment);
    }

    public void navigateToSettings(){
        SettingsFragment settingsFragment = new SettingsFragment();
        displayFragment(settingsFragment);
    }

    public void navigateToBrowserView(String url, Context context){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    private void displayFragment(Fragment fragment){
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void navigateBack(){
        fragmentManager.popBackStackImmediate();
    }

    public boolean isNavigationFragmentVisible(){
        return fragmentManager.getBackStackEntryCount() <= 1;
    }
}