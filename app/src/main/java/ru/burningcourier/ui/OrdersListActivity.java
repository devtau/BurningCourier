package ru.burningcourier.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import ru.burningcourier.R;
import ru.burningcourier.OrdersAdapter;
import ru.burningcourier.api.RESTClient;
import ru.burningcourier.api.RESTClientImpl;
import ru.burningcourier.api.RESTClientView;
import ru.burningcourier.api.model.City;
import ru.burningcourier.api.model.Order;
import ru.burningcourier.handlers.TimerCommand;
import ru.burningcourier.service.GeoService;
import ru.burningcourier.sfClasses.SFApplication;
import ru.burningcourier.sfClasses.SFBaseActivity;
import ru.burningcourier.utils.AppUtils;
import ru.burningcourier.utils.PreferencesManager;

public class OrdersListActivity extends SFBaseActivity {
    
    private static final String LOG_TAG = "OrdersListActivity";
    private int requestTimerId = -1;
    private int requestId = -1;
    
    private ProgressDialogFragment progress;
    private OrdersAdapter adapter;
    private RESTClient restClient;
    private GeoService geoService;
    private boolean mBound = false;
    private ServiceConnection sConn;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);
        initOrdersList();
        initToolbar();
        requestTimerId = getServiceHelper().timerCommand(AppUtils.TIMER_TIME_MINUTES);
        startGEOSend();
    
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
        restClient = new RESTClientImpl(new RESTClientViewImpl());
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.orders_list_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit:
                System.exit(0);
                return true;
            case R.id.action_update:
                progress = new ProgressDialogFragment();
                progress.setMessage(getString(R.string.receiving_data));
                progress.show(getSupportFragmentManager(), ProgressDialogFragment.TAG);
                
                String savedCityName = PreferencesManager.getInstance(OrdersListActivity.this).getCurrentCity();
                String cityUrl = City.getUrlByName(savedCityName);
                if (!TextUtils.isEmpty(cityUrl)) {
                    restClient.getOrders(cityUrl, SFApplication.token, geoService.getGeoList());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarOrdersList);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        } else {
            Log.e(LOG_TAG, "unexpected state. actionBar is null");
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
        if (isFinishing()) {
            stopGEOSend();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        if (requestId != -1 && !getServiceHelper().isPending(requestId) && progress != null && progress.isAdded()) {
            progress.dismiss();
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
    
    //Обработка таймера
    private void getTimer(int resultCode, Bundle resultData) {
        if (resultCode == TimerCommand.RESPONSE_PROGRESS) {
            adapter.notifyDataSetChanged();
        } else {
            this.requestTimerId = -1;
            stopService(SFApplication.geoIntent);
            Toast.makeText(this, resultData.getString(TimerCommand.TIMER), Toast.LENGTH_LONG).show();
        }
    }
    
    private void startGEOSend() {
        if (SFApplication.geoLocation) {
            SFApplication.geoIntent = new Intent(this, GeoService.class);
            startService(SFApplication.geoIntent);
        }
    }
    
    private void stopGEOSend() {
        try {
            stopService(SFApplication.geoIntent);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "Сервис геолокации еще не запущен");
        }
    }
    
    private void initOrdersList() {
        RecyclerView ordersList = (RecyclerView) findViewById(R.id.ordersList);
        adapter = new OrdersAdapter(SFApplication.orders, order -> OrderActivity.startActivity(this, order));
        ordersList.setAdapter(adapter);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
        ordersList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }
    
    
    private class RESTClientViewImpl implements RESTClientView {
        @Override
        public void processCitiesList(List<City> cities) {/*NOP*/}
        
        @Override
        public void processLoginSuccess(String token) {/*NOP*/}
        
        @Override
        public void processLoginFail() {/*NOP*/}
        
        @Override
        public void processOrders(List<Order> orders) {
            showToast("processOrders orders size = " + orders.size());
            SFApplication.orders = (ArrayList<Order>) orders;
            adapter.notifyDataSetChanged();
            if (!getServiceHelper().isPending(requestTimerId)) {
                requestTimerId = getServiceHelper().timerCommand(AppUtils.TIMER_TIME_MINUTES);
            }
            if (progress != null && progress.isAdded()) {
                progress.dismiss();
            }
        }

//            @Override
//            public void processOrderStatusChanged() {
//                restClient.getOrders(SFApplication.cities.get(0).getUrl(), SFApplication.token);
//            }
        
        @Override
        public void showToast(String msg) {
            Toast.makeText(OrdersListActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
