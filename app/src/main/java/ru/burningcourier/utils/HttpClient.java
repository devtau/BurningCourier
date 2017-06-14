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

public class HttpClient {
    
    public static String API_DB_URL = null;
    // private static final String API_DB_URL = "http://93.92.194.154/test_sak/hs/";
    
    //Адреса операций API
    public static final String AUTHORIZATION_URL = "courier/ident/";
    public static final String UPDATE_URL = "courier/update/";
    public static final String DELIVER_URL = "courier/deliver/";
    public static final String SEND_GEO_URL = "courier/geo/";
    
    //Шаблоны uri отправки заказа
    public static final String DELIVER_TEMPLATE = "%1s%2s/%3s";
    public static final String SEND_GEO_TEMPLATE = "%1s%2s/%3s/%4s";
    public static final String SEND_GEO_SPEED_TEMPLATE = "%1s%2s/%3s/%4s/%5s";
    
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
        int i = Integer.valueOf("004762141");
        return responseInfo.toString();
    }
}
