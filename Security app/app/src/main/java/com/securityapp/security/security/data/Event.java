package com.securityapp.security.security.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by ginahasrouni on 2/20/17.
 */

public class Event {

    @SerializedName("eventId")
    @Expose
    public int eventId;

    @SerializedName("nameOfDevice")
    @Expose
    public String nameOfDevice;

    @SerializedName("timeStarted")
    @Expose
    private long timeStarted;

    @SerializedName("timeEnded")
    @Expose
    private long timeEnded;

    public String getDate(){
        return DateFormat.getDateInstance().format(new Date(timeStarted * 1000));
    }

    public String getStartTime(){
        return DateFormat.getTimeInstance().format(new Date(timeStarted * 1000));
    }

    public int getDuration(){
        return (int) new Date((timeEnded - timeStarted)).getTime();
    }
}