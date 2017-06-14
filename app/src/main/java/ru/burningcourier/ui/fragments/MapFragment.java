package ru.burningcourier.ui.fragments;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import java.util.List;
import ru.burningcourier.R;

public class MapFragment extends Fragment implements LocationListener{
    
    private static final String LOG_TAG = "MapFragment";
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private Location location;
    private List<String> matchingProviders;
    
    public MapFragment() { }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        matchingProviders = locationManager.getAllProviders();
        return inflater.inflate(R.layout.fragment_map, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "onActivityCreated");
        setUpMapIfNeeded();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 60, 10, this);
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }
    
    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        googleMap.clear();
        setUpMap();
    }
    
    @Override
    public void onStatusChanged(String _status, int i, Bundle bundle) {
        Log.d(LOG_TAG, "Status - " + _status);
    }
    
    @Override public void onProviderEnabled(String _provider) { }
    @Override public void onProviderDisabled(String _provider) { }
    
    
    private void setUpMapIfNeeded() {
        if (googleMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapViewTab);
            mapFragment.getMapAsync(map -> {
                this.googleMap = map;
                googleMap.clear();
                if (googleMap != null) {
                    googleMap.setMyLocationEnabled(true);
                    setUpMap();
                }
            });
        }
    }
    
    private void setUpMap() {
        LatLng latLng;
        CameraUpdate center;
        CameraUpdate zoom;
        
        for (String provider : matchingProviders) {
            Location localLoc = locationManager.getLastKnownLocation(provider);
            if (localLoc != null) {
                location = localLoc;
            }
        }
        
        if (location != null) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            center = CameraUpdateFactory.newLatLng(latLng);
            zoom = CameraUpdateFactory.zoomTo(14);
            googleMap.moveCamera(center);
            googleMap.moveCamera(zoom);
            googleMap.animateCamera(zoom);
        }
    }
}
