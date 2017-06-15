package ru.burningcourier.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import ru.burningcourier.R;
import ru.burningcourier.handlers.impl.ApiCommands.AuthorizationCommand;
import ru.burningcourier.handlers.impl.ApiCommands.UpdateCommand;
import ru.burningcourier.sfClasses.SFApplication;
import ru.burningcourier.sfClasses.SFBaseActivity;
import ru.burningcourier.ui.fragments.AuthenticationFragment;
import ru.burningcourier.ui.fragments.CitiesListFragment;
import ru.burningcourier.utils.AppUtils;
import ru.burningcourier.utils.HttpClient;

public class AuthenticationActivity extends SFBaseActivity implements AuthInteractionListener {
    
    //Ключи для хранимых данных
    public static final String PREF_LOGIN = "pref-login";
    public static final String PREF_SESSION_TIME = "pref-session-time";
    public static final String PREF_CITY = "pref-city";
    
    private int requestId = -1;
    private ProgressDialogFragment progress;
    private long session;
    private SharedPreferences currentLogin;
    private SharedPreferences.Editor editorCurrentLogin;
    private String login;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        look4City();
        check4Auth();
    }
    
    @Override
    public void lookForELogin(TextView login) {
        currentLogin = getPreferences(MODE_PRIVATE);
        SFApplication.CURRENT_LOGIN = currentLogin.getString(PREF_LOGIN, null);
        session = currentLogin.getLong(PREF_SESSION_TIME, 0);
        if (SFApplication.CURRENT_LOGIN != null) {
            login.setText(SFApplication.CURRENT_LOGIN);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (requestId != -1 && !getServiceHelper().isPending(requestId) && progress != null && progress.isAdded()) {
            progress.dismiss();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_city:
                replaceFragment(CitiesListFragment.newInstance(), getString(R.string.fragment_city));
                return true;
        }
        return super.onOptionsItemSelected(item);
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
    public void doAuth(String login, String password) {
        this.login = login;
        progress = new ProgressDialogFragment();
        progress.setMessage(getString(R.string.authorization));
        progress.show(getSupportFragmentManager(), ProgressDialogFragment.TAG);
        requestId = getServiceHelper().authCommand(HttpClient.API_DB_URL + HttpClient.AUTHORIZATION_URL + login + "/" + password);
    }
    
    @Override
    public void saveCity(int city) {
        if (city == -1) return;
        SFApplication.CURRENT_CITY = city;
        currentLogin = getPreferences(MODE_PRIVATE);
        editorCurrentLogin = currentLogin.edit();
        editorCurrentLogin.putInt(PREF_CITY, SFApplication.CURRENT_CITY);
        editorCurrentLogin.apply();
        check4Auth();
    }
    
    @Override
    public void cancelCommand() {
        cancelCommand(requestId);
    }
    
    
    private void replaceFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .commit();
    }
    
    private void check4Auth() {
        if (SFApplication.CURRENT_CITY != -1 && SFApplication.userAuth && System.currentTimeMillis() < session) {
            Intent intent = new Intent(this, OrdersListActivity.class);
            startActivity(intent);
            finish();
        } else {
            SFApplication.userAuth = false;
        }
        if (SFApplication.CURRENT_CITY != -1) {
            setTitle(getString(R.string.authorization));
            replaceFragment(AuthenticationFragment.newInstance(), getString(R.string.fragment_city));
        } else {
            replaceFragment(CitiesListFragment.newInstance(), getString(R.string.fragment_city));
        }
    }
    
    private void look4City() {
        currentLogin = getPreferences(MODE_PRIVATE);
        SFApplication.CURRENT_CITY = currentLogin.getInt(PREF_CITY, -1);
        AppUtils.setAPIBase(this, SFApplication.CURRENT_CITY);
        session = currentLogin.getLong(PREF_SESSION_TIME, 0);
    }
    
    private void saveELogin() {
        if (!login.isEmpty()) {
            SFApplication.CURRENT_LOGIN = login;
            currentLogin = getPreferences(MODE_PRIVATE);
            editorCurrentLogin = currentLogin.edit();
            editorCurrentLogin.putString(PREF_LOGIN, SFApplication.CURRENT_LOGIN);
            editorCurrentLogin.putLong(PREF_SESSION_TIME, System.currentTimeMillis() + AppUtils.SESSION_TIME);
            editorCurrentLogin.apply();
        }
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
}
