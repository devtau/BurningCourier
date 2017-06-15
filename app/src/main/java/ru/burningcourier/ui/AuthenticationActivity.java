package ru.burningcourier.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import ru.burningcourier.R;
import ru.burningcourier.handlers.impl.ApiCommands.AuthorizationCommand;
import ru.burningcourier.handlers.impl.ApiCommands.UpdateCommand;
import ru.burningcourier.sfClasses.SFApplication;
import ru.burningcourier.sfClasses.SFBaseActivity;
import ru.burningcourier.utils.AppUtils;
import ru.burningcourier.utils.HttpClient;

public class AuthenticationActivity extends SFBaseActivity implements
        ProgressDialogFragment.AuthCancellerListener {
    
    //Ключи для хранимых данных
    public static final String PREF_LOGIN = "pref-login";
    public static final String PREF_SESSION_TIME = "pref-session-time";
    public static final String PREF_CITY = "pref-city";
    
    private int requestId = -1;
    private ProgressDialogFragment progress;
    private long session;
    private String loginString;
    
    private EditText login;
    private EditText password;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        initUI();
        lookForELogin();
        look4City();
        check4Auth();
        setTitle(getString(R.string.authorization));
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (requestId != -1 && !getServiceHelper().isPending(requestId) && progress != null && progress.isAdded()) {
            progress.dismiss();
        }
    }
    
    @Override
    public void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle resultData) {
        super.onServiceCallback(requestId, requestIntent, resultCode, resultData);
        //Обработка авторизации
        if (getServiceHelper().check(requestIntent, AuthorizationCommand.class)) {
            authorization(resultCode, resultData);
        }
        //Обработка данных списка заказов
        if (getServiceHelper().check(requestIntent, UpdateCommand.class)) {
            this.requestId = -1;
            if (progress != null && progress.isAdded()) {
                progress.dismiss();
            }
            Intent intent = new Intent(this, OrdersListActivity.class);
            startActivity(intent);
            Toast.makeText(this, resultData.getString(UpdateCommand.UPDATE_EXTRA), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    @Override
    public void cancelCommand() {
        cancelCommand(requestId);
    }
    
    private void check4Auth() {
        if (SFApplication.CURRENT_CITY != -1 && SFApplication.userAuth && System.currentTimeMillis() < session) {
            startActivity(new Intent(this, OrdersListActivity.class));
            finish();
        } else {
            SFApplication.userAuth = false;
        }
    }
    
    private void look4City() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SFApplication.CURRENT_CITY = preferences.getInt(PREF_CITY, -1);
        AppUtils.setAPIBase(this, SFApplication.CURRENT_CITY);
        session = preferences.getLong(PREF_SESSION_TIME, 0);
    }
    
    private void authorization(int resultCode, Bundle resultData) {
        if (resultCode == AuthorizationCommand.RESPONSE_SUCCESS) {
            saveELogin();
            progress.updateProgressDialogMessage(getString(R.string.receiving_data));
            requestId = getServiceHelper().ordersCommand(HttpClient.API_DB_URL + HttpClient.UPDATE_URL + SFApplication.CURRENT_LOGIN);
        } else {
            Toast.makeText(this, resultData.getString(AuthorizationCommand.AUTHORIZATION_STATUS_EXTRA), Toast.LENGTH_LONG).show();
            if (progress != null && progress.isAdded()) {
                progress.dismiss();
            }
        }
    }
    
    private void saveELogin() {
        if (loginString.isEmpty()) return;
        SFApplication.CURRENT_LOGIN = loginString;
        SharedPreferences.Editor preferencesEditor = getPreferences(MODE_PRIVATE).edit();
        preferencesEditor.putString(PREF_LOGIN, SFApplication.CURRENT_LOGIN);
        preferencesEditor.putLong(PREF_SESSION_TIME, System.currentTimeMillis() + AppUtils.SESSION_TIME);
        preferencesEditor.apply();
    }
    
    private void initUI() {
        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);
        Spinner citiesSpinner = (Spinner) findViewById(R.id.citiesSpinner);
        citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                saveCity(selectedItemPosition);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
        findViewById(R.id.authButton).setOnClickListener(v -> login());;
    }
    
    private void lookForELogin() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SFApplication.CURRENT_LOGIN = preferences.getString(PREF_LOGIN, null);
        session = preferences.getLong(PREF_SESSION_TIME, 0);
        if (SFApplication.CURRENT_LOGIN != null) {
            login.setText(SFApplication.CURRENT_LOGIN);
        }
    }
    
    private void saveCity(int cityId) {
        AppUtils.setAPIBase(this, cityId);
        if (cityId == -1) return;
        SFApplication.CURRENT_CITY = cityId;
        SharedPreferences.Editor preferencesEditor = getPreferences(MODE_PRIVATE).edit();
        preferencesEditor.putInt(PREF_CITY, SFApplication.CURRENT_CITY);
        preferencesEditor.apply();
        check4Auth();
    }
    
    private void login() {
        if (TextUtils.isEmpty(login.getText()) || TextUtils.isEmpty(password.getText())) {
            Toast.makeText(this, getString(R.string.fill_in_all_fields), Toast.LENGTH_SHORT).show();
        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(password.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            
            if (AppUtils.checkConnection(this)) {
                doAuth(login.getText().toString(), password.getText().toString());
            } else {
                Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void doAuth(String login, String password) {
        this.loginString = login;
        progress = new ProgressDialogFragment();
        progress.setMessage(getString(R.string.authorization));
        progress.show(getSupportFragmentManager(), ProgressDialogFragment.TAG);
        requestId = getServiceHelper().authCommand(HttpClient.API_DB_URL + HttpClient.AUTHORIZATION_URL + login + "/" + password);
    }
}
