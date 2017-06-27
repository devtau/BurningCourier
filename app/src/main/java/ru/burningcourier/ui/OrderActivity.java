package ru.burningcourier.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import ru.burningcourier.BCApplication;
import ru.burningcourier.adapters.StatusesAdapter;
import ru.burningcourier.api.BackendAPI;
import ru.burningcourier.api.model.Order;
import ru.burningcourier.R;
import ru.burningcourier.api.RESTClient;
import ru.burningcourier.api.RESTClientImpl;
import ru.burningcourier.api.RESTClientView;
import ru.burningcourier.api.model.City;
import ru.burningcourier.api.model.Status;
import ru.burningcourier.service.TimerService;
import ru.burningcourier.utils.AppUtils;

public class OrderActivity extends GeoListenerActivity implements ConfirmationDialog.ConfirmationDialogListener {
    
    private static final String ORDER_EXTRA = "ORDER_EXTRA";
    private final static String LOG_TAG = "OrderActivity";
    private static final int TAKE_PHOTO_REQUEST = 9732;
    private Order order;
    private RESTClient restClient;
    private BroadcastReceiver timeTickReceiver;
    private Toolbar toolbar;
    private TextView orderTimer;
    private List<Status> statuses;
    private File photoFile;
    private int checkSumValue;
    
    
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
        initStatuses();
        initUI();
        restClient = new RESTClientImpl(new RESTClientViewImpl());
        
        timeTickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateTimer();
            }
        };
        startService(new Intent(OrderActivity.this, TimerService.class));
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
                openMap();
                return true;
            case R.id.action_call:
                ConfirmationDialog.showDialog(order.orderId, ConfirmationDialog.ConfirmationType.CALL, getSupportFragmentManager());
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
        registerReceiver(timeTickReceiver, new IntentFilter(TimerService.TIME_TICK));
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK) {
            File compressedFile = AppUtils.compressFile(photoFile);
            Uri compressedFileUri = AppUtils.buildUriFromPath(compressedFile.getAbsolutePath(), this);
            restClient.uploadPhoto(BackendAPI.PHOTO_API_BASE_URL, order.orderId, getContentResolver(), compressedFileUri, compressedFile);
            Toast.makeText(this, R.string.wait_for_server_response, Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void onConfirmed() {
        City savedCity = City.getCityByName(this);
        if (savedCity != null) {
            restClient.changeStatus(savedCity.getUrl(), BCApplication.token, order.orderId, order.nextStatus, geoService.getGeoList());
        }
    }
    
    @Override
    public void onCallCenterCallClicked() {
        City savedCity = City.getCityByName(this);
        if (savedCity != null) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:+" + savedCity.getCallCenterPhone()));
            startActivity(intent);
        }
    }
    
    @Override
    public void onClientCallClicked() {
        Toast.makeText(this, "ждите звонок", Toast.LENGTH_SHORT).show();
        City savedCity = City.getCityByName(this);
        if (savedCity != null) {
            restClient.callClient(savedCity.getUrl(), BCApplication.token, order.orderId);
        }
    }
    
    @Override
    public void takePhoto(int checkSumValue) {
        this.checkSumValue = checkSumValue;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, "ru.burningcourier.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST);
            }
        }
    }
    
    @NonNull
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile("JPEG_" + timeStamp + "_", "", storageDir);
    }
    
    
    private void initUI() {
        View nextStatusContainer = findViewById(R.id.nextStatusContainer);
        if (order.type.equals(Order.TYPE_BAMPSI)) {
            initStatusesList();
            nextStatusContainer.setVisibility(View.GONE);
        } else {
            nextStatusContainer.setOnClickListener(v -> {
                if (!order.isDelivered) {
                    ConfirmationDialog.showDialog(order.orderId, ConfirmationDialog.ConfirmationType.STATUS, getSupportFragmentManager());
                }
            });
        }
        
        String addressPlusNote = order.address;
        if (!TextUtils.isEmpty(order.note)) {
            addressPlusNote += ", " + order.note;
        }
        ((TextView) findViewById(R.id.orderAddress)).setText(addressPlusNote);
        ((TextView) findViewById(R.id.toolbarOrderTitle)).setText(String.valueOf(order.orderId));
        toolbar = (Toolbar) findViewById(R.id.toolbarOrder);
        orderTimer = (TextView) findViewById(R.id.orderTimer);
        findViewById(R.id.onlinePayment).setVisibility(order.isCash ? View.GONE : View.VISIBLE);
        updateTimer();
        initToolbar();
    }
    
    private void updateTimer() {
        BCApplication.orders.stream()
                .filter(storedOrder -> storedOrder.orderId.equals(this.order.orderId))
                .forEach(storedOrder -> this.order = storedOrder);
        orderTimer.setText(AppUtils.formatTimer(order));
        orderTimer.setTextColor(AppUtils.processTimerColor(order, this));
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
    
    private void initStatuses() {
        City savedCity = City.getCityByName(this);
        if (savedCity != null) {
            switch (order.type) {
                case Order.TYPE_BAMPSI:
                    statuses = savedCity.getStatuses().getBampsiStatuses();
                    break;
                case Order.TYPE_SAKURA:
                    statuses = savedCity.getStatuses().getSakuraStatuses();
                    break;
            }
        }
    }
    
    private void initStatusesList() {
        RecyclerView statusesList = (RecyclerView) findViewById(R.id.statusesList);
        statusesList.setAdapter(new StatusesAdapter(statuses, order.nextStatus, order.isCash, status -> {
            boolean isCheckRequired = status.getId() == StatusesAdapter.STATUS_DELIVERED && !order.isCash;
            ConfirmationDialog.ConfirmationType type = isCheckRequired ?
                    ConfirmationDialog.ConfirmationType.PHOTO :
                    ConfirmationDialog.ConfirmationType.STATUS;
            ConfirmationDialog.showDialog(order.orderId, type, getSupportFragmentManager());
        }));
        statusesList.setLayoutManager(new LinearLayoutManager(this));
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
            BCApplication.orders = (ArrayList<Order>) orders;
            finish();
        }
    
        @Override
        public void processOrderStatusChanged(int tracking) {
            City savedCity = City.getCityByName(OrderActivity.this);
            if (savedCity != null) {
                restClient.getOrders(savedCity.getUrl(), BCApplication.token, geoService.getGeoList());
            }
        }
    
        @Override
        public void processPhotoUploaded(String photoNameOnServer) {
            City savedCity = City.getCityByName(OrderActivity.this);
            if (savedCity != null) {
                String fullPhotoUrl = BackendAPI.PHOTO_API_BASE_URL + BackendAPI.VIEW_PHOTO_ENDPOINT + photoNameOnServer;
                restClient.uploadPhotoUrl(savedCity.getUrl(), BCApplication.token, order.orderId, fullPhotoUrl, checkSumValue);
            }
        }
    
        @Override
        public void processPhotoUrlUploaded() {
            finish();
        }
    
        @Override
        public void showToast(String msg) {
            Toast.makeText(OrderActivity.this, msg, Toast.LENGTH_LONG).show();
        }
    }
}
