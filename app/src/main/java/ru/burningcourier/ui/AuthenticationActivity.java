package ru.burningcourier.ui;

import android.content.Context;
import android.content.Intent;
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
import ru.burningcourier.api.HttpClient;
import ru.burningcourier.utils.PreferencesManager;

public class AuthenticationActivity extends SFBaseActivity implements
        ProgressDialogFragment.AuthCancellerListener {
    
    private int requestId = -1;
    private ProgressDialogFragment progress;
    private long session;
    private String loginString;
    
    private EditText loginView;
    private EditText passwordView;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        initUI();
        String login = PreferencesManager.getInstance(this).getLogin();
        session = PreferencesManager.getInstance(this).getSessionTime();
        if (loginView != null) {
            loginView.setText(login);
        }
        HttpClient.setAPIBase(PreferencesManager.getInstance(this).getCurrentCity());
        session = PreferencesManager.getInstance(this).getSessionTime();
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
    public void cancelAuthorization() {
        cancelCommand(requestId);
    }
    
    private void check4Auth() {
        if (PreferencesManager.getInstance(this).getCurrentCity() != -1 && SFApplication.userAuth && System.currentTimeMillis() < session) {
            startActivity(new Intent(this, OrdersListActivity.class));
            finish();
        } else {
            SFApplication.userAuth = false;
        }
    }
    
    private void authorization(int resultCode, Bundle resultData) {
        if (resultCode == AuthorizationCommand.RESPONSE_SUCCESS) {
            saveELogin();
            progress.updateProgressDialogMessage(getString(R.string.receiving_data));
            requestId = getServiceHelper().ordersCommand(HttpClient.buildUpdateUrl(loginView.getText().toString()));
        } else {
            Toast.makeText(this, resultData.getString(AuthorizationCommand.AUTHORIZATION_STATUS_EXTRA), Toast.LENGTH_LONG).show();
            if (progress != null && progress.isAdded()) {
                progress.dismiss();
            }
        }
    }
    
    private void saveELogin() {
        if (loginString.isEmpty()) return;
        PreferencesManager.getInstance(this).setLogin(loginString);
        PreferencesManager.getInstance(this).setSessionTime(System.currentTimeMillis() + AppUtils.SESSION_TIME);
    }
    
    private void initUI() {
        loginView = (EditText) findViewById(R.id.login);
        passwordView = (EditText) findViewById(R.id.password);
        Spinner citiesSpinner = (Spinner) findViewById(R.id.citiesSpinner);
        citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                saveCity(selectedItemPosition);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
        findViewById(R.id.authButton).setOnClickListener(v -> login());
    }
    
    private void saveCity(int cityId) {
        if (cityId == -1) return;
        HttpClient.setAPIBase(cityId);
        PreferencesManager.getInstance(this).setCityId(cityId);
        check4Auth();
    }
    
    private void login() {
        if (TextUtils.isEmpty(loginView.getText()) || TextUtils.isEmpty(passwordView.getText())) {
            Toast.makeText(this, getString(R.string.fill_in_all_fields), Toast.LENGTH_SHORT).show();
        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(passwordView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            
            if (AppUtils.checkConnection(this)) {
                this.loginString = loginView.getText().toString();
                progress = new ProgressDialogFragment();
                progress.setMessage(getString(R.string.authorization));
                progress.show(getSupportFragmentManager(), ProgressDialogFragment.TAG);
                requestId = getServiceHelper().authCommand(HttpClient.buildAuthorizationUrl(loginView.getText().toString(), passwordView.getText().toString()));
            } else {
                Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
