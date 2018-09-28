package com.securityapp.security.security.ui.device;

import com.securityapp.security.security.data.Device;

/**
 * Created by Tyler on 2/4/2018.
 */

public interface DeviceClickListener {

    void onEnableButtonClicked(Device device);

    void onEditNameClicked(Device device);

    void onEditOrientationClicked(Device device);
}
