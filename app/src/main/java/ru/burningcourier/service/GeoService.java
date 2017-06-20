package ru.burningcourier.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.burningcourier.api.model.Geo;
import ru.burningcourier.utils.AppUtils;

public class GeoService extends Service implements LocationListener {
    
    private static final String LOG_TAG = "GeoService";
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; //meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 2; //minutes
    private final IBinder mBinder = new LocalBinder();
    private List<Location> locations = new ArrayList<>();
    private List<Geo> geo = new ArrayList<>();
    
    
    public List<Location> getLocations() {
        return locations;
    }
    
    public List<Geo> getGeoList() {
        return geo;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    @Override
    public void onLocationChanged(Location location) {
        if (location == null) return;
        Log.d(LOG_TAG, "onLocationChanged. lat = " + location.getLatitude() + " lon = " + location.getLongitude() + " geo.size() = " + geo.size());
        locations.add(location);
        String now = AppUtils.DATE_FORMATTER.format(new Date(System.currentTimeMillis()));
        geo.add(new Geo(now, location.getLatitude(), location.getLongitude(), 220));
    }
    @Override public void onStatusChanged(String s, int i, Bundle bundle) { }
    @Override public void onProviderEnabled(String s) { }
    @Override public void onProviderDisabled(String s) { }
    
    
    
    public class LocalBinder extends Binder {
        public GeoService getService() {
            return GeoService.this;
        }
    }
}
