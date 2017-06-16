package ru.burningcourier.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import java.util.Locale;
import ru.burningcourier.Order;
import ru.burningcourier.R;
import ru.burningcourier.sfClasses.SFApplication;

public class OrderActivity extends AppCompatActivity {
    
    private static final String ORDER_EXTRA = "ORDER_EXTRA";
    private final static String LOG_TAG = "OrderActivity";
    private Order order;
    private Toolbar toolbar;
    
    
    public static void startActivity(Context context, Order order) {
        Intent intent = new Intent(context, OrderActivity.class);
        intent.putExtra(ORDER_EXTRA, order);
        context.startActivity(intent);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        order = (Order) getIntent().getSerializableExtra(ORDER_EXTRA);
        toolbar = (Toolbar) findViewById(R.id.toolbarOrder);
        String addressPlusNote = order.address;
        if (!TextUtils.isEmpty(order.note)) {
            addressPlusNote += ", " + order.note;
        }
        ((TextView) findViewById(R.id.orderAddress)).setText(addressPlusNote);
        initToolbar();
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
    
    //TODO: это нам еще пригодится
    private void doSomeLegacyShit(int position) {
        SFApplication.orders.trimToSize();
        while (position >= SFApplication.orders.size()) {
            position -= 1;
        }
        if (SFApplication.selectedOrder != position) {
            if (SFApplication.selectedOrder != -1) {
                SFApplication.orders.get(SFApplication.selectedOrder).selected = false;
            }
            SFApplication.orders.get(position).selected = true;
            SFApplication.selectedOrder = position;
        } else {
            SFApplication.orders.get(SFApplication.selectedOrder).selected = false;
            SFApplication.selectedOrder = -1;
        }
    }
}
