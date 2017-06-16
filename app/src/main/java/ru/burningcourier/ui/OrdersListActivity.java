package ru.burningcourier.ui;

import android.content.Intent;
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
import ru.burningcourier.R;
import ru.burningcourier.OrdersAdapter;
import ru.burningcourier.handlers.impl.ApiCommands.SendCommand;
import ru.burningcourier.handlers.impl.ApiCommands.TimerCommand;
import ru.burningcourier.handlers.impl.ApiCommands.UpdateCommand;
import ru.burningcourier.service.GEOLocationService;
import ru.burningcourier.sfClasses.SFApplication;
import ru.burningcourier.sfClasses.SFBaseActivity;
import ru.burningcourier.utils.AppUtils;
import ru.burningcourier.api.HttpClient;
import ru.burningcourier.utils.PreferencesManager;

public class OrdersListActivity extends SFBaseActivity implements
        ProgressDialogFragment.AuthCancellerListener {
    
    private static final String LOG_TAG = "OrdersListActivity";
    private int requestTimerId = -1;
    private int requestId = -1;
    
    private ProgressDialogFragment progress;
    private OrdersAdapter adapter;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);
        initOrdersList();
        initToolbar();
        requestTimerId = getServiceHelper().timerCommand(AppUtils.TIMER_TIME_MINUTES);
        startGEOSend();
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
                String login = PreferencesManager.getInstance(this).getLogin();
                requestId = getServiceHelper().ordersCommand(HttpClient.buildUpdateUrl(login));
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
    
    @Override
    public void cancelAuthorization() {
        cancelCommand(requestId);
    }
    
    @Override
    public void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle resultData) {
        super.onServiceCallback(requestId, requestIntent, resultCode, resultData);
        if (getServiceHelper().check(requestIntent, UpdateCommand.class)) {
            getOrders(resultCode, resultData);
        }
        if (getServiceHelper().check(requestIntent, SendCommand.class)) {
            getSendOrder(resultCode, resultData);
        }
        if (getServiceHelper().check(requestIntent, TimerCommand.class)) {
            getTimer(resultCode, resultData);
        }
    }
    
    //Обработка данных списка заказов
    private void getOrders(int resultCode, Bundle resultData) {
        requestId = -1;
        if (resultCode == UpdateCommand.RESPONSE_SUCCESS) {
            SFApplication.selectedOrder = -1;
            adapter.notifyDataSetChanged();
            if (!getServiceHelper().isPending(requestTimerId)) {
                requestTimerId = getServiceHelper().timerCommand(AppUtils.TIMER_TIME_MINUTES);
            }
        } else {
            Toast.makeText(this, resultData.getString(UpdateCommand.UPDATE_EXTRA), Toast.LENGTH_LONG).show();
            adapter.notifyDataSetChanged();
        }
        if (progress != null && progress.isAdded()) {
            progress.dismiss();
            progress = null;
        }
    }
    
    //Обработка отправленного заказа
    private void getSendOrder(int resultCode, Bundle resultData) {
        requestId = -1;
        if (resultCode == SendCommand.RESPONSE_SUCCESS) {
            adapter.notifyDataSetChanged();
        }
        Toast.makeText(this, resultData.getString(SendCommand.DELIVER_STATUS_EXTRA), Toast.LENGTH_LONG).show();
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
            SFApplication.geoIntent = new Intent(this, GEOLocationService.class);
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
}
