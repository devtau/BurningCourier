package ru.burningcourier.utils;

import android.util.Base64;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class HttpClient {
    
    private static String API_DB_URL = null;
    
    private static final String API_PITER = "http://91.190.82.49/sakura/hs/courier/";
    private static final String API_MOSCOW = "http://91.190.82.49:8083/wok/hs/courier/";
    private static final String API_URAL = "http://91.190.82.49:8084/wokekb/hs/courier/";
    private static final String API_MURMAN = "http://91.190.82.49:8089/sw_mur/hs/courier/";
    private static final String API_TEST = "http://93.92.194.154/test_sak/hs/courier/";
//    адрес сервера до 01.06.17: 93.92.194.154
    
    //Адреса операций API
    private static final String GET_CITIES_LIST_URL = "some_static_base_url/config/";
    private static final String AUTHORIZATION_URL = "ident/";
//    private static final String AUTHORIZATION_URL = "auth/";
    private static final String UPDATE_URL = "update/";
    private static final String DELIVER_URL = "deliver/";
    private static final String SEND_GEO_URL = "geo/";
    
    //Шаблоны url отправки заказа
    public static final String DELIVER_TEMPLATE = "%1s%2s/%3s";
    private static final String SEND_GEO_TEMPLATE = "%s%s/%f/%f";
    
    private static final String LOG_TAG = "HttpClient";
    private static final String LOGIN = "mobil1";
    private static final String PASSWORD = "vUFwLd4akOoE80E";
    private String url;
    private StringBuffer responseInfo;
    
    
    public HttpClient(String url) {
        this.url = url;
        Log.d(LOG_TAG, "Получены данные для аутентификации. url = " + this.url);
    }

    public boolean execute() {
        Log.d(LOG_TAG, "Начало аутентификации");
        responseInfo = new StringBuffer("");

        try {
            URL urlLoc = new URL(url);
    
            HttpURLConnection urlConnection = (HttpURLConnection) urlLoc.openConnection();
            urlConnection.setRequestProperty("Authorization", "Basic " + encodeLoginPassword());
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
    
            int statusCode = urlConnection.getResponseCode();
            Log.d(LOG_TAG, "statusCode: " + statusCode);

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    responseInfo.append(line);
                }
                reader.close();

                if (responseInfo != null) {
                    Log.d(LOG_TAG, "Данные получены: " + responseInfo);
                    return true;
                } else {
                    Log.e(LOG_TAG, "Нет данных");
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Произошло исключение во время авторизации: " + e.getMessage() + "\n" + e);
        }
        return false;
    }

    private String encodeLoginPassword() {
        String authParam = LOGIN + ":" + PASSWORD;
        return Base64.encodeToString(authParam.getBytes(), Base64.NO_WRAP);
    }

    public int getAuthStatus() {
        try {
            JSONObject jsonAuth = new JSONObject(responseInfo.toString());
            return jsonAuth.getInt("ident");
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Ошибка при получении json данных: " + e.getMessage() + "\n" + e);
        }
        return 0;
    }

    public int getDeliveryStatus() {
        try {
            JSONObject jsonAuth = new JSONObject(responseInfo.toString());
            return jsonAuth.getInt("delivered");
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Ошибка при получении json данных: " + e.getMessage() + "\n" + e);
        }
        return 0;
    }

    public int getGeoStatus() {
        try {
            JSONObject jsonAuth = new JSONObject(responseInfo.toString());
            return jsonAuth.getInt("state");
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Ошибка при получении json данных: " + e.getMessage() + "\n" + e);
        }
        return 0;
    }

    public String getResponseInfo() {
        return responseInfo.toString();
    }
    
    public static void setAPIBase(int cityId) {
        switch (cityId) {
            case 0:
                API_DB_URL = API_PITER;
                break;
            case 1:
                API_DB_URL = API_MOSCOW;
                break;
            case 2:
                API_DB_URL = API_URAL;
                break;
            case 3:
                API_DB_URL = API_MURMAN;
                break;
        }
    }
    
    public static String buildAuthorizationUrl(String login, String password) {
        return API_DB_URL + AUTHORIZATION_URL + login + "/" + password;
    }
    
    public static String buildUpdateUrl(String login) {
        return API_DB_URL + UPDATE_URL + login;
    }
    
    public static String buildDeliverUrl() {
        return API_DB_URL + DELIVER_URL;
    }
    
    public static String buildSendGeoUrl(double latitude, double longitude) {
        String uriString = String.format(Locale.US, SEND_GEO_TEMPLATE, API_DB_URL, SEND_GEO_URL, latitude, longitude);
        uriString = uriString.replaceAll(" ", "");
        return uriString;
    }
}
