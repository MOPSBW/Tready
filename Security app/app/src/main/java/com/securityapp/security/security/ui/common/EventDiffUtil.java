package com.securityapp.security.security.ui.common;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.securityapp.security.security.data.Event;

import java.util.List;

/**
 * Created by Tyler on 1/22/2018.
 */

public class EventDiffUtil extends DiffUtil.Callback {
    private List<Event> oldList;
    private List<Event> newList;

    public EventDiffUtil(List<Event> oldList, List<Event> newList){
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).eventId == newList.get(newItemPosition).eventId;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).nameOfDevice.equals(newList.get(newItemPosition).nameOfDevice);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}