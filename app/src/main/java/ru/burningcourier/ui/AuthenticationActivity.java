package ru.burningcourier.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import ru.burningcourier.R;
import ru.burningcourier.api.RESTClient;
import ru.burningcourier.api.RESTClientImpl;
import ru.burningcourier.api.RESTClientView;
import ru.burningcourier.api.model.City;
import ru.burningcourier.api.model.Order;
import ru.burningcourier.service.GeoService;
import ru.burningcourier.sfClasses.SFApplication;
import ru.burningcourier.utils.AppUtils;
import ru.burningcourier.utils.PreferencesManager;

public class AuthenticationActivity extends AppCompatActivity {
    
    private RESTClient restClient;
    private GeoService geoService;
    private boolean mBound = false;
    private ServiceConnection sConn;
    private ProgressDialogFragment progress;
    private Spinner citiesSpinner;
    private EditText loginView;
    private EditText passwordView;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        initUI();
        if (loginView != null) {
            loginView.setText(PreferencesManager.getInstance(this).getLogin());
        }
        check4Auth();
        setTitle(getString(R.string.authorization));
    
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
        restClient.getCitiesList();
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        dismissProgress();
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
    
    private void dismissProgress() {
        if (progress != null && progress.isAdded()) {
            progress.dismiss();
        }
    }
    
    private void check4Auth() {
        long session = PreferencesManager.getInstance(this).getSessionTime();
        String savedCityName = PreferencesManager.getInstance(this).getCurrentCity();
        if (!TextUtils.isEmpty(savedCityName) && SFApplication.orders != null && SFApplication.userAuth && System.currentTimeMillis() < session) {
            startActivity(new Intent(AuthenticationActivity.this, OrdersListActivity.class));
            finish();
        } else {
            SFApplication.userAuth = false;
        }
    }
    
    private void saveLoginAndSessionTime() {
        String login = loginView.getText().toString();
        if (TextUtils.isEmpty(login)) return;
        PreferencesManager.getInstance(this).setLogin(login);
        PreferencesManager.getInstance(this).setSessionTime(System.currentTimeMillis() + AppUtils.SESSION_TIME);
    }
    
    private void initUI() {
        loginView = (EditText) findViewById(R.id.login);
        passwordView = (EditText) findViewById(R.id.password);
        citiesSpinner = (Spinner) findViewById(R.id.citiesSpinner);
        findViewById(R.id.authButton).setOnClickListener(v -> login());
    }
    
    private void login() {
        if (TextUtils.isEmpty(loginView.getText()) || TextUtils.isEmpty(passwordView.getText())) {
            Toast.makeText(this, getString(R.string.fill_in_all_fields), Toast.LENGTH_SHORT).show();
        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(passwordView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            
            if (AppUtils.checkConnection(this)) {
                progress = new ProgressDialogFragment();
                progress.setMessage(getString(R.string.authorization));
                progress.show(getSupportFragmentManager(), ProgressDialogFragment.TAG);

                String savedCityName = PreferencesManager.getInstance(AuthenticationActivity.this).getCurrentCity();
                String cityUrl = City.getUrlByName(savedCityName);
                String deviceId = PreferencesManager.getInstance(this).getDeviceId();
                if (!TextUtils.isEmpty(cityUrl)) {
                    restClient.login(cityUrl, loginView.getText().toString(), passwordView.getText().toString(), deviceId);
                } else {
                    Toast.makeText(this, getString(R.string.choose_city), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    
    
    private class RESTClientViewImpl implements RESTClientView {
        @Override
        public void processCitiesList(List<City> cities) {
            SFApplication.cities = cities;
            String[] citiesArray = new String[cities.size()];
            for (int i = 0; i < citiesArray.length; i++) {
                citiesArray[i] = cities.get(i).getName();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AuthenticationActivity.this, android.R.layout.simple_list_item_1, citiesArray);
            citiesSpinner.setAdapter(adapter);
            citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                    String cityName = SFApplication.cities.get(selectedItemPosition).getName();
                    PreferencesManager.getInstance(AuthenticationActivity.this).setCityName(cityName);
                    check4Auth();
                }
                @Override public void onNothingSelected(AdapterView<?> parent) { }
            });
        }
    
        @Override
        public void processLoginSuccess(String token) {
            SFApplication.userAuth = true;
            SFApplication.token = token;
            saveLoginAndSessionTime();
            if (progress != null && progress.isAdded()) {
                progress.updateProgressDialogMessage(getString(R.string.receiving_data));
            }
            String savedCityName = PreferencesManager.getInstance(AuthenticationActivity.this).getCurrentCity();
            String cityUrl = City.getUrlByName(savedCityName);
            if (!TextUtils.isEmpty(cityUrl)) {
                restClient.getOrders(cityUrl, token, geoService.getGeoList());
            } else {
                showToast("выберите город");
            }
        }
    
        @Override
        public void processLoginFail() {
            dismissProgress();
        }
    
        @Override
        public void processOrders(List<Order> orders) {
            showToast("processOrders orders size = " + orders.size());
            SFApplication.orders = (ArrayList<Order>) orders;
            dismissProgress();
            startActivity(new Intent(AuthenticationActivity.this, OrdersListActivity.class));
            finish();
        }
    
        @Override
        public void showToast(String msg) {
            Toast.makeText(AuthenticationActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
