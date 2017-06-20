package ru.burningcourier.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import java.util.UUID;

public class PreferencesManager {

    private static final String PREFS_NAME = "burningcourier_prefs";
    //Ключи для хранимых данных
    private static final String DEVICE_ID = "device_id";
    private static final String PREF_LOGIN = "pref-login";
    private static final String PREF_SESSION_TIME = "pref-session-time";
    private static final String PREF_CITY = "pref-city";

    
    private SharedPreferences preferences;
    private static PreferencesManager self;
    
    
    synchronized public static PreferencesManager getInstance(Context context) {
        synchronized (PreferencesManager.class) {
            if (self == null)
                self = new PreferencesManager(context);
        }
        return self;
    }
    
    private PreferencesManager(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public void clear() {
        preferences.edit().clear().apply();
    }


    //setters
    public void setSessionTime(long sessionTime) {
        Editor editor = preferences.edit();
        editor.putLong(PREF_SESSION_TIME, sessionTime);
        editor.apply();
    }
    
    public void setLogin(String login) {
        Editor editor = preferences.edit();
        editor.putString(PREF_LOGIN, login);
        editor.apply();
    }
    
    public void setCityName(String cityName) {
        Editor editor = preferences.edit();
        editor.putString(PREF_CITY, cityName);
        editor.apply();
    }
    
    
    //getters
    public String getDeviceId() {
        String deviceIdStored = preferences.getString(DEVICE_ID, "");
        if (TextUtils.isEmpty(deviceIdStored)) {
            deviceIdStored = UUID.randomUUID().toString();
            Editor editor = preferences.edit();
            editor.putString(DEVICE_ID, deviceIdStored);
            editor.apply();
        }
        return deviceIdStored;
    }
    
    public String getCurrentCity() {
        return preferences.getString(PREF_CITY, "");
    }
    
    public long getSessionTime() {
        return preferences.getLong(PREF_SESSION_TIME, 0);
    }
    
    public String getLogin() {
        return preferences.getString(PREF_LOGIN, null);
    }
}
