package ru.burningcourier.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import ru.burningcourier.R;
import ru.burningcourier.adapters.OrdersAdapter;
import ru.burningcourier.handlers.impl.ApiCommands.SendCommand;
import ru.burningcourier.handlers.impl.ApiCommands.TimerCommand;
import ru.burningcourier.handlers.impl.ApiCommands.UpdateCommand;
import ru.burningcourier.service.GEOLocationService;
import ru.burningcourier.sfClasses.SFApplication;
import ru.burningcourier.sfClasses.SFBaseActivity;
import ru.burningcourier.ui.fragments.MapFragment;
import ru.burningcourier.ui.fragments.OrderListFragment;
import ru.burningcourier.utils.AppUtils;
import ru.burningcourier.utils.HttpClient;

public class TabHostActivity extends SFBaseActivity implements
        ProgressDialogFragment.AuthCancellerListener,
        OrderListFragment.OrderListListener {
    
    private static final String LOG_TAG = "TabHostActivity";
    private int requestTimerId = -1;
    private int requestId = -1;
    private ProgressDialogFragment progress;
    private TabHost.TabContentFactory tabContentFactory;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_host);
        requestTimerId = getServiceHelper().timerCommand(AppUtils.TIMER_TIME_MINUTES);
        tabContentFactory = s -> findViewById(android.R.id.tabcontent);
        initTabs();
        startGEOSend();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "TabHostActivity onPause");
        if (isFinishing()) {
            stopGEOSend();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "TabHostActivity onResume");
        if (requestId != -1) {
            if (!getServiceHelper().isPending(requestId)) {
                if (progress != null && progress.isAdded()) {
                    progress.dismiss();
                }
            }
        }
    }
    
    @Override
    public void updateOrders() {
        progress = new ProgressDialogFragment();
        progress.setMessage(getString(R.string.receiving_data));
        progress.show(getSupportFragmentManager(), ProgressDialogFragment.TAG);
        requestId = getServiceHelper().ordersCommand(HttpClient.API_DB_URL + HttpClient.UPDATE_URL + SFApplication.CURRENT_LOGIN);
    }
    
    @Override
    public void sendOrder() {
        requestId = getServiceHelper().sendCommand(HttpClient.API_DB_URL + HttpClient.DELIVER_URL, SFApplication.selectedOrder);
    }
    
    @Override
    public void cancelCommand() {
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
        this.requestId = -1;
        if (resultCode == UpdateCommand.RESPONSE_SUCCESS) {
            SFApplication.selectedOrder = -1;
            updateOrderList();
            findViewById(R.id.deliverBtn).setVisibility(View.VISIBLE);
            if (!getServiceHelper().isPending(requestTimerId)) {
                requestTimerId = getServiceHelper().timerCommand(AppUtils.TIMER_TIME_MINUTES);
            }
        } else {
            Toast.makeText(this, resultData.getString(UpdateCommand.UPDATE_EXTRA), Toast.LENGTH_LONG).show();
            findViewById(R.id.deliverBtn).setVisibility(View.INVISIBLE);
            updateOrderList();
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
            updateOrderList();
        }
        Toast.makeText(this, resultData.getString(SendCommand.DELIVER_STATUS_EXTRA), Toast.LENGTH_LONG).show();
    }
    
    //Обработка таймера
    private void getTimer(int resultCode, Bundle resultData) {
        if (resultCode == TimerCommand.RESPONSE_PROGRESS) {
            updateOrderList();
        } else {
            this.requestTimerId = -1;
            stopService(SFApplication.geoIntent);
            Toast.makeText(this, resultData.getString(TimerCommand.TIMER), Toast.LENGTH_LONG).show();
        }
    }
    
    private void replaceFragments(Fragment fragment, boolean shouldAnimate, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        if (shouldAnimate) {
            // 1-появляется/2-уходит
            if (tag.equals(getString(R.string.list_Tab_Tag))) {
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            }
            if (tag.equals(getString(R.string.map_Tab_Tag))) {
                ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }
        ft.replace(android.R.id.tabcontent, fragment, tag);
        ft.commit();
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
    
    private void initTabs() {
        TabHost.TabSpec tabSpec;
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        
        tabSpec = tabHost.newTabSpec(getString(R.string.list_Tab_Tag));
        tabSpec.setIndicator(getString(R.string.list_Tab_Indicator));
        tabSpec.setContent(tabContentFactory);
        tabHost.addTab(tabSpec);
        
        tabSpec = tabHost.newTabSpec(getString(R.string.map_Tab_Tag));
        tabSpec.setIndicator(getString(R.string.map_Tab_Indicator));
        tabSpec.setContent(tabContentFactory);
        tabHost.addTab(tabSpec);
        tabHost.setOnTabChangedListener(tabTag -> {
            if (tabTag.equals(getString(R.string.list_Tab_Tag))) {
                Log.d(LOG_TAG, "Выбрана владка со списком");
                replaceFragments(new OrderListFragment(), true, getString(R.string.list_Tab_Tag));
            } else
            if (tabTag.equals(getString(R.string.map_Tab_Tag))) {
                Log.d(LOG_TAG, "Выбрана владка с картой");
                replaceFragments(new MapFragment(), true, getString(R.string.map_Tab_Tag));
            }
        });
        
        replaceFragments(new OrderListFragment(), true, getString(R.string.list_Tab_Tag));
    }
    
    private void updateOrderList() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.list_Tab_Tag));
        if (fragment == null) return;
        OrdersAdapter ordersAdapter = (OrdersAdapter) ((ListView) findViewById(R.id.orderList)).getAdapter();
        if (ordersAdapter == null) return;
        ordersAdapter.notifyDataSetChanged();
    }
}
