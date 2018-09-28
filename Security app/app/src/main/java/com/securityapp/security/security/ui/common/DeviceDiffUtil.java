package com.securityapp.security.security.ui.common;

import android.support.v7.util.DiffUtil;

import com.securityapp.security.security.data.Device;

import java.util.List;
import java.util.Objects;

/**
 * Created by Tyler on 2/23/2018.
 */

public class DeviceDiffUtil extends DiffUtil.Callback {

    private List<Device> oldList;
    private List<Device> newList;

    public DeviceDiffUtil(List<Device> oldList, List<Device> newList){
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
        return (oldList.get(oldItemPosition).getDeviceIdentifier() == (newList.get(newItemPosition).getDeviceIdentifier()));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Device oldItem = oldList.get(oldItemPosition);
        Device newItem = newList.get(newItemPosition);

        return (Objects.equals(oldItem.getOrientation(), newItem.getOrientation()))
                && (Objects.equals(oldItem.getDeviceName(), newItem.getDeviceName()))
                && (Objects.equals(oldItem.getEnabledString(), newItem.getEnabledString()));
    }
}
