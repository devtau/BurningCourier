package ru.burningcourier.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;
import ru.burningcourier.sfClasses.SFApplication;
import ru.burningcourier.utils.HttpClient;

public class GEOLocationService extends Service implements LocationListener {
    
    private static final String LOG_TAG = "GEOLocationService";
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; //meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 2; //minutes
    private static final long TIME_BW_SEND_UPDATES = 1000 * 60 * 3; //minutes
    
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private double latitude;
    private double longitude;
    private LocationManager locationManager;
    private Location location;
    
    
    public GEOLocationService() { }
    
    
    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                getLocation();
            }
        };
        timer.schedule(timerTask, 1000, TIME_BW_SEND_UPDATES);
    }
    
    
    private void getLocation() {
        try {
            boolean canGetLocation;
            if (!isGPSEnabled && !isNetworkEnabled) {
                canGetLocation = false;
            } else {
                canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    Log.d(LOG_TAG, "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            setLocationParms();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    Log.d(LOG_TAG, "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            setLocationParms();
                        }
                    }
                }
            }
            if (canGetLocation) {
                sendGeo();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void sendGeo() {
        int geoStatus;
        String url = String.format(HttpClient.SEND_GEO_TEMPLATE, HttpClient.API_DB_URL + HttpClient.SEND_GEO_URL, SFApplication.CURRENT_LOGIN, latitude, longitude);
        url = url.replaceAll(" ", "");
        
        HttpClient basicAuthBCClient = new HttpClient(url);
        basicAuthBCClient.execute();
        geoStatus = basicAuthBCClient.getGeoStatus();
        
        if (SFApplication.userAuth) {
            Log.d(LOG_TAG, "Пользователь auth");
            if (geoStatus == 0) {
                Log.d(LOG_TAG, basicAuthBCClient.getResponseInfo());
            }
            if (geoStatus == 1) {
                Log.d(LOG_TAG, basicAuthBCClient.getResponseInfo());
            }
        }
    }
    
    private void setLocationParms() {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }
    @Override public void onStatusChanged(String s, int i, Bundle bundle) { }
    @Override public void onProviderEnabled(String s) { }
    @Override public void onProviderDisabled(String s) { }
}
