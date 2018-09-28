package com.securityapp.security.security.ui.device;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.securityapp.security.security.R;
import com.securityapp.security.security.adapter.SelectionAdapter;
import com.securityapp.security.security.config.Config;
import com.securityapp.security.security.data.Device;
import com.securityapp.security.security.ui.common.DeviceDiffUtil;
import com.securityapp.security.security.ui.common.EventDiffUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tyler on 1/20/2018.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private DeviceClickListener mClickListener;
    private List<Device> mDevices = new ArrayList<>(0);

    public DeviceAdapter(DeviceClickListener clickListener){
        mClickListener = clickListener;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new DeviceViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        holder.bindDevice(mDevices.get(position));
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    public void replaceData(List<Device> devices){
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DeviceDiffUtil(this.mDevices, devices), false);
        mDevices = devices;
        diffResult.dispatchUpdatesTo(this);
    }

    public List<Device> getAllDeviceByStatus(boolean enabled){
        List<Device> devices = new ArrayList<>();
        for(Device device : mDevices){
            if(device.isEnabled() == enabled){
                devices.add(device);
            }
        }
        return devices;
    }

    public int getDeviceStatusCount(boolean enabled){
        int count = 0;
        for(Device device : mDevices){
            if(device.isEnabled() == enabled){
                count++;
            }
        }
        return count;
    }

    /**
     * View Holder for Device row
     */
    public class DeviceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.device_name)
        TextView deviceName;

        @BindView(R.id.ip_address)
        TextView ipAddress;

        @BindView(R.id.device_orientation)
        TextView deviceOrientation;

        @BindView(R.id.last_contact)
        TextView lastContact;

        @BindView(R.id.txt_device_activated)
        TextView deviceEnabled;

        @BindView(R.id.txt_device_disabled)
        TextView deviceDisabled;

        @BindView(R.id.btn_edit_name)
        Button editDeviceName;

        @BindView(R.id.btn_edit_orientation)
        Button editOrientation;

        @BindView(R.id.btn_enable_device)
        Button enableButton;

        @BindString(R.string.enable)
        String enablePrompt;

        @BindString(R.string.disable)
        String disablePrompt;

        private DeviceClickListener mClickListener;

        public DeviceViewHolder(View itemView, DeviceClickListener clickListener) {
            super(itemView);
            mClickListener = clickListener;
            ButterKnife.bind(this, itemView);
        }

        public void bindDevice(Device device){
            deviceName.setText(device.getDeviceName());
            ipAddress.setText(device.getIpAddress());
            deviceOrientation.setText(device.getOrientation());
            lastContact.setText(device.getLastContact());

            if(device.isEnabled()){
                deviceEnabled.setVisibility(View.VISIBLE);
                deviceDisabled.setVisibility(View.GONE);
                enableButton.setText(disablePrompt);
            }else{
                deviceDisabled.setVisibility(View.VISIBLE);
                deviceEnabled.setVisibility(View.GONE);
                enableButton.setText(enablePrompt);
            }

            enableButton.setOnClickListener(view -> mClickListener.onEnableButtonClicked(device));

            editDeviceName.setOnClickListener(view -> mClickListener.onEditNameClicked(device));

            editOrientation.setOnClickListener(view -> mClickListener.onEditOrientationClicked(device));
        }
    }
}