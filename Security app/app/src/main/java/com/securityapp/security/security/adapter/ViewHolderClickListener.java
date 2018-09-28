package com.securityapp.security.security.adapter;

import android.view.View;

/**
 * Created by Tyler on 12/21/2017.
 */

public interface ViewHolderClickListener {
    void onItemClick(int position);
    boolean onItemLongClick(int position);
    void onRowOptionsClicked(int position, View view);
}
