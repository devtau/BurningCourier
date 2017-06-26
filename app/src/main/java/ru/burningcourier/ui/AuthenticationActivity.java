package ru.burningcourier.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import ru.burningcourier.BCApplication;
import ru.burningcourier.R;
import ru.burningcourier.api.RESTClient;
import ru.burningcourier.api.RESTClientImpl;
import ru.burningcourier.api.RESTClientView;
import ru.burningcourier.api.model.City;
import ru.burningcourier.api.model.Order;
import ru.burningcourier.utils.AppUtils;
import ru.burningcourier.utils.PreferencesManager;

public class AuthenticationActivity extends GeoListenerActivity {
    
    private RESTClient restClient;
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
        restClient = new RESTClientImpl(new RESTClientViewImpl());
        restClient.getCitiesList();
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        ProgressDialogFragment.dismissProgress(progress);
    }
    
    private void check4Auth() {
        long session = PreferencesManager.getInstance(this).getSessionTime();
        String savedCityName = PreferencesManager.getInstance(this).getCurrentCity();
        if (!TextUtils.isEmpty(savedCityName) && BCApplication.orders != null && BCApplication.userAuth && System.currentTimeMillis() < session) {
            startActivity(new Intent(AuthenticationActivity.this, OrdersListActivity.class));
            finish();
        } else {
            BCApplication.userAuth = false;
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
        if (!AppUtils.checkConnection(this) || BCApplication.cities == null || BCApplication.cities.size() == 0) return;
        if (TextUtils.isEmpty(loginView.getText()) || TextUtils.isEmpty(passwordView.getText())) {
            Toast.makeText(this, getString(R.string.fill_in_all_fields), Toast.LENGTH_SHORT).show();
        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(passwordView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    
            progress = new ProgressDialogFragment();
            progress.setMessage(getString(R.string.authorization));
            progress.show(getSupportFragmentManager(), ProgressDialogFragment.TAG);
    
            String deviceId = PreferencesManager.getInstance(this).getDeviceId();
            City savedCity = City.getCityByName(this);
            if (savedCity != null) {
                restClient.login(savedCity.getUrl(), loginView.getText().toString(), passwordView.getText().toString(), deviceId);
            } else {
                Toast.makeText(this, getString(R.string.choose_city), Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    
    
    private class RESTClientViewImpl implements RESTClientView {
        @Override
        public void processCitiesList(List<City> cities) {
            BCApplication.cities = cities;
            String[] citiesArray = new String[cities.size()];
            for (int i = 0; i < citiesArray.length; i++) {
                citiesArray[i] = cities.get(i).getName();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AuthenticationActivity.this, android.R.layout.simple_list_item_1, citiesArray);
            citiesSpinner.setAdapter(adapter);
            citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                    String cityName = BCApplication.cities.get(selectedItemPosition).getName();
                    PreferencesManager.getInstance(AuthenticationActivity.this).setCityName(cityName);
                    check4Auth();
                }
                @Override public void onNothingSelected(AdapterView<?> parent) { }
            });
        }
    
        @Override
        public void processLoginSuccess(String token) {
            BCApplication.userAuth = true;
            BCApplication.token = token;
            saveLoginAndSessionTime();
            if (progress != null && progress.isAdded()) {
                progress.updateProgressDialogMessage(getString(R.string.receiving_data));
            }
            City savedCity = City.getCityByName(AuthenticationActivity.this);
            if (savedCity != null) {
                restClient.getOrders(savedCity.getUrl(), token, geoService.getGeoList());
            } else {
                showToast("выберите город");
            }
        }
    
        @Override
        public void processLoginFail() {
            ProgressDialogFragment.dismissProgress(progress);
        }
    
        @Override
        public void processOrders(List<Order> orders) {
            BCApplication.orders = (ArrayList<Order>) orders;
            ProgressDialogFragment.dismissProgress(progress);
            startActivity(new Intent(AuthenticationActivity.this, OrdersListActivity.class));
            finish();
        }
    
        @Override
        public void processOrderStatusChanged(int tracking) {
            City savedCity = City.getCityByName(AuthenticationActivity.this);
            if (savedCity != null) {
                restClient.getOrders(savedCity.getUrl(), BCApplication.token, geoService.getGeoList());
            }
        }
    
        @Override
        public void processPhotoUploaded(String photoUrl) {/*NOP*/}
    
        @Override
        public void processPhotoUrlUploaded() {/*NOP*/}
    
        @Override
        public void showToast(String msg) {
            Toast.makeText(AuthenticationActivity.this, msg, Toast.LENGTH_LONG).show();
        }
    }
}
