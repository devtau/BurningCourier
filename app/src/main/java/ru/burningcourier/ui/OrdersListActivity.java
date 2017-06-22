package ru.burningcourier.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import ru.burningcourier.BCApplication;
import ru.burningcourier.R;
import ru.burningcourier.adapters.OrdersAdapter;
import ru.burningcourier.api.RESTClient;
import ru.burningcourier.api.RESTClientImpl;
import ru.burningcourier.api.RESTClientView;
import ru.burningcourier.api.model.City;
import ru.burningcourier.api.model.Order;
import ru.burningcourier.service.GeoService;
import ru.burningcourier.service.TimerService;

public class OrdersListActivity extends GeoListenerActivity {
    
    private static final String LOG_TAG = "OrdersListActivity";
    private ProgressDialogFragment progress;
    private RecyclerView ordersList;
    private OrdersAdapter adapter;
    private RESTClient restClient;
    private BroadcastReceiver timeTickReceiver;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);
        initOrdersList();
        initToolbar();
        startGEOSend();
        restClient = new RESTClientImpl(new RESTClientViewImpl());
        
        timeTickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                adapter.notifyDataSetChanged();
            }
        };
        startService(new Intent(OrdersListActivity.this, TimerService.class));
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
                
                City savedCity = City.getCityByName(this);
                if (savedCity != null) {
                    restClient.getOrders(savedCity.getUrl(), BCApplication.token, geoService.getGeoList());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(timeTickReceiver);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        ProgressDialogFragment.dismissProgress(progress);
        adapter = new OrdersAdapter(BCApplication.orders, order -> OrderActivity.startActivity(this, order));
        ordersList.setAdapter(adapter);
        registerReceiver(timeTickReceiver, new IntentFilter(TimerService.TIME_TICK));
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
    
    private void startGEOSend() {
        if (BCApplication.geoLocation) {
            startService(new Intent(this, GeoService.class));
        }
    }
    
    private void initOrdersList() {
        ordersList = (RecyclerView) findViewById(R.id.ordersList);
        adapter = new OrdersAdapter(BCApplication.orders, order -> OrderActivity.startActivity(this, order));
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
            //TODO: вернуть
            BCApplication.orders = (ArrayList<Order>) orders;
            adapter = new OrdersAdapter(BCApplication.orders, order -> OrderActivity.startActivity(OrdersListActivity.this, order));
            ordersList.setAdapter(adapter);
            ProgressDialogFragment.dismissProgress(progress);
        }
    
        @Override
        public void processOrderStatusChanged(int tracking) {
            City savedCity = City.getCityByName(OrdersListActivity.this);
            if (savedCity != null) {
                restClient.getOrders(savedCity.getUrl(), BCApplication.token, geoService.getGeoList());
            }
        }
        
        @Override
        public void showToast(String msg) {
            Toast.makeText(OrdersListActivity.this, msg, Toast.LENGTH_LONG).show();
        }
    }
}
