package ru.burningcourier.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import java.util.Locale;
import ru.burningcourier.api.model.Order;
import ru.burningcourier.R;
import ru.burningcourier.api.RESTClient;
import ru.burningcourier.api.RESTClientImpl;
import ru.burningcourier.api.RESTClientView;
import ru.burningcourier.api.model.City;
import ru.burningcourier.utils.AppUtils;
import ru.burningcourier.utils.PreferencesManager;

public class OrderActivity extends AppCompatActivity {
    
    private static final String ORDER_EXTRA = "ORDER_EXTRA";
    private final static String LOG_TAG = "OrderActivity";
    private Order order;
    private RESTClient restClient;
    private Toolbar toolbar;
    private TextView orderTimer;
    private TextView nextStatus;
    
    
    public static void startActivity(Context context, Order order) {
        Intent intent = new Intent(context, OrderActivity.class);
        intent.putExtra(ORDER_EXTRA, order);
        context.startActivity(intent);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        order = getIntent().getParcelableExtra(ORDER_EXTRA);
        initUI();
        restClient = new RESTClientImpl(new RESTClientViewImpl());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_map:
//                MapsActivity.startActivity(this, order.addressLat, order.addressLon, "цель");
                openMap();
                return true;
            case R.id.action_call:
                Toast.makeText(this, "скоро это будет открывать диалог выбора типа звонка", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void initUI() {
        nextStatus = (TextView) findViewById(R.id.nextStatus);
        nextStatus.setOnClickListener(v -> {
            if (!order.delivered) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.adb_title)
                        .setMessage(R.string.adb_msg)
                        .setPositiveButton(R.string.adb_yes, (dialog, which) -> {
                            String login = PreferencesManager.getInstance(this).getLogin();
//                            restClient.changeOrderStatus();
                        })
                        .setNegativeButton(R.string.adb_no, null)
                        .show();
            }
        });
        String addressPlusNote = order.address;
        if (!TextUtils.isEmpty(order.note)) {
            addressPlusNote += ", " + order.note;
        }
        ((TextView) findViewById(R.id.orderAddress)).setText(addressPlusNote);
        ((TextView) findViewById(R.id.toolbarOrderTitle)).setText(String.valueOf(order.orderId));
        toolbar = (Toolbar) findViewById(R.id.toolbarOrder);
        orderTimer = (TextView) findViewById(R.id.orderTimer);
        orderTimer.setText(AppUtils.formatTimer(order));
        orderTimer.setTextColor(AppUtils.processTimerColor(order, this));
        initToolbar();
    }
    
    private void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
        } else {
            Log.e(LOG_TAG, "unexpected state. actionBar is null");
        }
    }
    
    private void openMap() {
        String uriFormatter = getString(R.string.yandexmaps_uri_formatter);
        String uriString = String.format(Locale.US, uriFormatter, order.addressLon, order.addressLat, 15);
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities.size() > 0;
        if (isIntentSafe) {
            startActivity(intent);
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=ru.yandex.yandexmaps"));
            startActivity(intent);
        }
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
        }
    
//        @Override
//        public void processOrderStatusChanged() {
//            restClient.getOrders(SFApplication.cities.get(0).getUrl(), SFApplication.token);
//        }
    
        @Override
        public void showToast(String msg) {
            Toast.makeText(OrderActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
