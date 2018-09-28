package com.securityapp.security.security.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.securityapp.security.security.R;
import com.securityapp.security.security.ui.login.LoginActivity;

/**
 * BaseFragment handles logic for setting up actionbar and communicates with BaseActivity
 * when a session is expired
 */

public class BaseFragment extends Fragment {

    /**
     * Set up actionbar for current fragment
     * @param isNavigationShown true for navigation menu, false for back button
     * @param title toolbar title
     */
    protected void setupActionBar(boolean isNavigationShown, String title){
        if(getActivity() != null && getActivity() instanceof BaseActivity){
            ActionBar actionBar = ((BaseActivity) getActivity()).getSupportActionBar();
            actionBar.setTitle(title);
            if(isNavigationShown){
                actionBar.setHomeAsUpIndicator(R.drawable.ic_nav_menu_icon);
            }else{
                actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            }
        }
    }

    /**
     * Show or hide the toolbar
     * @param isVisible true to show, false to hide
     */
    protected void showActionBar(boolean isVisible){
        if(getActivity() != null && getActivity() instanceof BaseActivity){
            ActionBar actionBar = ((BaseActivity) getActivity()).getSupportActionBar();
            if(isVisible){
                actionBar.show();
            }else{
                actionBar.hide();
            }
        }
    }

    /**
     * Session expired, need to reauthenticate user before resuming
     */
    protected void sessionExpired(){
        if(getActivity() != null && getActivity() instanceof BaseActivity){
            ((BaseActivity)getActivity()).reauthenticationRequired();
        }else{
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }
}
