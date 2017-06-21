package ru.burningcourier.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import ru.burningcourier.service.GeoService;

public abstract class GeoListenerActivity extends AppCompatActivity {
    
    GeoService geoService;
    private boolean mBound = false;
    private ServiceConnection sConn;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                GeoService.LocalBinder binder = (GeoService.LocalBinder) service;
                geoService = binder.getService();
                mBound = true;
            }
            
            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mBound = false;
            }
        };
    }
    
    
    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, GeoService.class), sConn, Context.BIND_AUTO_CREATE);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(sConn);
            mBound = false;
        }
    }
}
