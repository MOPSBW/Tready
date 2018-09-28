package com.securityapp.security.security.ui.device;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.securityapp.security.security.R;
import com.securityapp.security.security.data.Device;
import com.securityapp.security.security.di.Injectable;
import com.securityapp.security.security.service.request.DeviceNameRequest;
import com.securityapp.security.security.service.request.DeviceOrientationRequest;
import com.securityapp.security.security.service.request.DeviceStatusRequest;
import com.securityapp.security.security.ui.BaseFragment;
import com.securityapp.security.security.utils.AlertDialogUtils;
import com.securityapp.security.security.utils.SnackbarMessage;
import com.securityapp.security.security.utils.SnackbarUtils;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tyler on 1/20/2018.
 */

public class DeviceFragment extends BaseFragment implements DeviceClickListener, Injectable{

    @BindString(R.string.selectOrientation) String selectOrientationTitle;
    @BindString(R.string.important) String orientationImportantTitle;
    @BindString(R.string.orientationResetMsg) String orientationResetMsg;
    @BindString(R.string.allDevicesEnabledMsg) String allEnabledMsg;
    @BindString(R.string.allDevicesDisabledMsg) String allDisabledMsg;
    @BindString(R.string.deviceNameEmptyErrorMsg) String deviceNameEmptyErrorMsg;
    @BindString(R.string.newDeviceNameSameAsExistingMsg) String newDeviceNameSameAsExistingMsg;
    @BindString(R.string.orientationAlreadyApplied) String orientationAlreadyAppliedMsg;
    @BindString(R.string.changeDeviceName) String changeDeviceNameMsg;

    @BindView(R.id.device_recycler_view) RecyclerView recyclerView;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private DeviceViewModel mDeviceViewModel;
    private DeviceAdapter mDeviceAdapter;

    public DeviceFragment(){
        //requires empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        ButterKnife.bind(this, view);
        super.setupActionBar(false, "My Devices");
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDeviceViewModel = ViewModelProviders.of(this,viewModelFactory).get(DeviceViewModel.class);

        setupRecyclerViewAdapter();

        setupSnackbar();

        mDeviceViewModel.reauthRequired().observe(this, aBoolean -> sessionExpired());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.device_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_refresh_devices:
                mDeviceViewModel.loadDevices();
                return true;
            case R.id.menu_enable_all:
                if(mDeviceAdapter.getDeviceStatusCount(false) == 0){
                    Toast.makeText(getContext(), allEnabledMsg, Toast.LENGTH_SHORT).show();
                }else{
                    setAllActiveStatus(true);
                }
                return true;
            case R.id.menu_disable_all:
                if(mDeviceAdapter.getDeviceStatusCount(true) == 0){
                    Toast.makeText(getContext(), allDisabledMsg, Toast.LENGTH_SHORT).show();
                }else{
                    setAllActiveStatus(false);
                }
                return true;
            default:
                return false;
        }
    }

    private void setupSnackbar(){
        mDeviceViewModel.getSnackbarMessage().observe(this, new SnackbarMessage.SnackbarObserver() {
            @Override
            public void onNewMessage(int snackbarMessageResourceId) {
                SnackbarUtils.showSnackbar(getView(), getString(snackbarMessageResourceId));
            }
        });
    }

    /**
     * Sets all devices to active/inactive when the user selects 'enable all' or 'disable all'
     * @param activate boolean to enable or disable all devices
     */
    private void setAllActiveStatus(boolean activate){
        List<Device> devices = mDeviceAdapter.getAllDeviceByStatus(!activate);

        for(Device device : devices){
            setupConfigurationRequest(device, device.getEnabledStringFromBoolean(activate));
        }
    }

    /**
     * Sets up configuration change request and send to EventViewModel
     * @param device Device to change configuration
     * @param activate boolean to enable/disable
     */
    private void setupConfigurationRequest(Device device, String activate){
        DeviceStatusRequest statusRequest = new DeviceStatusRequest(device.getDeviceIdentifier(), activate);
        mDeviceViewModel.setDeviceConfiguration(statusRequest);
    }

    private void setupRecyclerViewAdapter(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        mDeviceAdapter = new DeviceAdapter(this);
        recyclerView.setAdapter(mDeviceAdapter);

        mDeviceViewModel.getDevices().observe(this, devices -> {
                mDeviceAdapter.replaceData(devices);
        });
    }

    @Override
    public void onEnableButtonClicked(Device device) {
        DeviceStatusRequest statusRequest = new DeviceStatusRequest(device.getDeviceIdentifier(), device.getEnabledStringFromBoolean(!device.isEnabled()));
        mDeviceViewModel.setDeviceConfiguration(statusRequest);
    }

    @Override
    public void onEditNameClicked(Device device) {
        EditText input = new EditText(getContext());

        AlertDialogUtils.showInputDialog(getActivity(), device.getDeviceName(), changeDeviceNameMsg ,input, (dialogInterface, i) -> {
            if(i == DialogInterface.BUTTON_POSITIVE){
                String newName = input.getText().toString().trim();
                if(newName.length() == 0){
                    Toast.makeText(getContext(), deviceNameEmptyErrorMsg, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Objects.equals(newName, device.getDeviceName())){
                    Toast.makeText(getContext(), newDeviceNameSameAsExistingMsg, Toast.LENGTH_SHORT).show();
                    return;
                }
                setupDeviceNameRequest(device.getDeviceIdentifier(), newName);
            }
            //Do nothing for cancel button pressed, closes automatically
        });
    }

    private void setupDeviceNameRequest(long deviceIdentifier, String newName){
        DeviceNameRequest nameRequest = new DeviceNameRequest(deviceIdentifier, newName);
        mDeviceViewModel.setDeviceConfiguration(nameRequest);
    }

    private void setupDeviceOrientationRequest(Device device, String orientation){
        DeviceOrientationRequest orientationRequest = new DeviceOrientationRequest(device.getDeviceIdentifier(), orientation);
        mDeviceViewModel.setDeviceConfiguration(orientationRequest);
    }

    @Override
    public void onEditOrientationClicked(Device device) {
        String[] choices = getContext().getResources().getStringArray(R.array.deviceOrientationChoices);
        int initialSelection = getOrientationStartingIndex(choices, device.getOrientation());

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(selectOrientationTitle)
                .setSingleChoiceItems(R.array.deviceOrientationChoices, initialSelection, (dialogInterface, selected) -> {
                    //store user selection if it is a new selection
                    if(!Objects.equals(choices[selected], device.getOrientation())){
                        //Warn user that a manual reboot is required for changes to take place
                        AlertDialogUtils.showAlertDialog(getContext(), orientationImportantTitle, orientationResetMsg, (dialog1, which) -> {
                            if(which == DialogInterface.BUTTON_POSITIVE){
                                setupDeviceOrientationRequest(device, choices[selected]);
                            }
                        });
                    }else{
                        Toast.makeText(getContext(), orientationAlreadyAppliedMsg, Toast.LENGTH_SHORT).show();
                    }
                    dialogInterface.dismiss();
                })
                .show();
    }

    /**
     * Matches current device orientation with index in list to correctly
     * @param choices CharSequence of orientations
     * @param deviceOrientation devices current orientation
     * @return index that matches the orientation in orientationLabels
     */
    private int getOrientationStartingIndex(String[] choices,String deviceOrientation){
        for(int i = 0; i < choices.length; i++){
            if(deviceOrientation.equals(choices[i])){
                return i;
            }
        }
        //should never get here, but if it does just return 0
        return 0;
    }
}