package com.securityapp.security.security.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.util.Date;

/**
 * Device object that is returned from web service
 */

public class Device {

    @SerializedName("deviceIdentifier")
    @Expose
    private long deviceIdentifier;

    @SerializedName("ipAddress")
    @Expose
    private String ipAddress;

    @SerializedName("deviceEnabled")
    @Expose
    private String deviceEnabled;

    @SerializedName("lastContact")
    @Expose
    private long lastContact;

    @SerializedName("deviceName")
    @Expose
    private String deviceName;

    @SerializedName("orientation")
    @Expose
    private String orientation;

    private static String enabledString = "Yes";
    private static String disabledString = "No";

    public long getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public boolean isEnabled() {
        return deviceEnabled.equalsIgnoreCase(enabledString);
    }

    public String getEnabledString(){
        return deviceEnabled;
    }

    public String getEnabledStringFromBoolean(boolean enabled){
        if(enabled){
            return enabledString;
        }
        return disabledString;
    }

    public void setDeviceEnabled(boolean enabled) {
        if(enabled){
            this.deviceEnabled = enabledString;
        }else{
            this.deviceEnabled = disabledString;
        }
    }

    public String getLastContact() {
        return DateFormat.getDateInstance().format(new Date(lastContact * 1000));
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }
}
