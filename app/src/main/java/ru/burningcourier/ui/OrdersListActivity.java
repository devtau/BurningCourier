package ru.burningcourier.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
import ru.burningcourier.utils.HttpClient;

public class OrdersListActivity extends SFBaseActivity implements
        ProgressDialogFragment.AuthCancellerListener {
    
    private static final String LOG_TAG = "OrdersListActivity";
    private int requestTimerId = -1;
    private int requestId = -1;
    
    private ProgressDialogFragment progress;
    private OrdersAdapter adapter;
    private View deliverBtn;
    private RecyclerView ordersList;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);
        initUI();
        requestTimerId = getServiceHelper().timerCommand(AppUtils.TIMER_TIME_MINUTES);
        startGEOSend();
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
        if (SFApplication.orders.size() == 0) {
            deliverBtn.setVisibility(View.INVISIBLE);
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
    
    private void initUI() {
        findViewById(R.id.updateBtn).setOnClickListener(v -> updateOrders());
        ordersList = (RecyclerView) findViewById(R.id.ordersList);
        deliverBtn = findViewById(R.id.deliverBtn);
        deliverBtn.setOnClickListener(v -> {
            if ((SFApplication.selectedOrder != -1) && !SFApplication.orders.get(SFApplication.selectedOrder).delivered) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.adb_title)
                        .setMessage(R.string.adb_msg)
                        .setPositiveButton(R.string.adb_yes, (dialog, which) -> requestId = getServiceHelper()
                                .sendCommand(HttpClient.API_DB_URL + HttpClient.DELIVER_URL, SFApplication.selectedOrder))
                        .setNegativeButton(R.string.adb_no, null)
                        .show();
            }
        });
        initOrdersList();
    }
    
    //Обработка данных списка заказов
    private void getOrders(int resultCode, Bundle resultData) {
        this.requestId = -1;
        if (resultCode == UpdateCommand.RESPONSE_SUCCESS) {
            SFApplication.selectedOrder = -1;
            adapter.notifyDataSetChanged();
            deliverBtn.setVisibility(View.VISIBLE);
            if (!getServiceHelper().isPending(requestTimerId)) {
                requestTimerId = getServiceHelper().timerCommand(AppUtils.TIMER_TIME_MINUTES);
            }
        } else {
            Toast.makeText(this, resultData.getString(UpdateCommand.UPDATE_EXTRA), Toast.LENGTH_LONG).show();
            deliverBtn.setVisibility(View.INVISIBLE);
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
        adapter = new OrdersAdapter(SFApplication.orders, order -> {
            Log.d(LOG_TAG, "list item clicked. order = " + order);
            MapsActivity.startActivity(OrdersListActivity.this, order.addressLat, order.addressLon, "цель");
            
            //TODO: это нам еще пригодится
//            SFApplication.orders.trimToSize();
//            while (position >= SFApplication.orders.size()) {
//                position -= 1;
//            }
//            if (SFApplication.selectedOrder != position) {
//                if (SFApplication.selectedOrder != -1) {
//                    SFApplication.orders.get(SFApplication.selectedOrder).selected = false;
//                }
//                SFApplication.orders.get(position).selected = true;
//                SFApplication.selectedOrder = position;
//            } else {
//                SFApplication.orders.get(SFApplication.selectedOrder).selected = false;
//                SFApplication.selectedOrder = -1;
//            }
//            adapter.notifyDataSetChanged();
        });
        ordersList.setAdapter(adapter);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }
    
    private void updateOrders() {
        progress = new ProgressDialogFragment();
        progress.setMessage(getString(R.string.receiving_data));
        progress.show(getSupportFragmentManager(), ProgressDialogFragment.TAG);
        requestId = getServiceHelper().ordersCommand(HttpClient.API_DB_URL + HttpClient.UPDATE_URL + SFApplication.CURRENT_LOGIN);
    }
}
