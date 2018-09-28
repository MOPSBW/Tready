package com.securityapp.security.security.utils;

import android.content.SharedPreferences;

import java.util.HashSet;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Manages the shared preferences for the application. All preferences require a Key.
 */

@Singleton
public class SharedPreferencesManager {

    private final SharedPreferences mPrefs;

    public enum Key {
        USERNAME_STR,
        PASSWORD_STR,
        USERKEY_STR,
        FIRST_TIME_LAUNCH,
        ENDPOINT,
    }

    @Inject
    public SharedPreferencesManager(SharedPreferences sharedPreferences) {
        mPrefs = sharedPreferences;
    }

    public void put(Key key, String val) {
        mPrefs.edit().putString(key.name(), val).apply();
    }

    public void put(Key key, int val) {
        mPrefs.edit().putInt(key.name(), val).apply();
    }

    public void put(Key key, boolean val) {
        mPrefs.edit().putBoolean(key.name(), val).apply();
    }

    public void put(Key key, float val) {
        mPrefs.edit().putFloat(key.name(), val).apply();
    }

    public String getString(Key key) {
        return mPrefs.getString(key.name(), null);
    }

    public void put(String key, HashSet<String> set){
        mPrefs.edit().putStringSet(key, set).apply();
    }

    public HashSet<String> getStringSet(String key){
        return (HashSet<String>) mPrefs.getStringSet(key, new HashSet<String>(0));
    }

    public int getInt(Key key, int defaultValue) {
        return mPrefs.getInt(key.name(), defaultValue);
    }

    public boolean getBoolean(Key key, boolean defaultValue) {
        return mPrefs.getBoolean(key.name(), defaultValue);
    }

    public boolean isFirstTimeLaunch(){
        return getBoolean(Key.FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstLaunch){
        mPrefs.edit().putBoolean(Key.FIRST_TIME_LAUNCH.name(), isFirstLaunch).apply();
    }

    /**
     * Deletes keys passed as args.
     * @param keys list of keys to delete
     */
    public void remove(Key... keys) {
        for (Key key : keys) {
            mPrefs.edit().remove(key.name()).apply();
        }
    }
}
