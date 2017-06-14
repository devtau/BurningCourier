package ru.burningcourier.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import ru.burningcourier.R;
import ru.burningcourier.Order;
import ru.burningcourier.sfClasses.SFApplication;

public class AppUtils {
    
    private static final String LOG_TAG = "AppUtils";
    private static final String TIME_ZONE = "GMT+00";
    
    
    public static final int TIMER_TIME_MINUTES = 1;
    public static final long RED_TIME = 900_000;
    public static final long HOUR = 3_600_000;
    public static final long SESSION_TIME = HOUR * 8;
    
    
    public static Date formatDate(String dateString) {
        Date date = null;
        SimpleDateFormat formatter;
        try {
            formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss", Locale.getDefault());
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            Log.d(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }
        return date;
    }
    
    public static String formatTimer(Order order) {
        String formDate = null;
        SimpleDateFormat format;
        if (!order.delivered) {
            if (order.timer > 0) {
                try {
                    if (order.timer < HOUR) {
                        format = new SimpleDateFormat("00:mm", Locale.getDefault());
                    } else {
                        format = new SimpleDateFormat("hh:mm", Locale.getDefault());
                    }
                    format.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
                    formDate = format.format(new Date(order.timer));
                } catch (Error e) {
                    Log.d(LOG_TAG, e.getMessage());
                    e.printStackTrace();
                }
            } else {
                formDate = "Время истекло";
            }
        } else {
            formDate = "Заказ доставлен";
        }
        return formDate;
    }
    
    public static void genOrders() {
        Order order = new Order();
        order.orderNum = 11114458;
        order.date = formatDate("10.08.2015 15:45:18");
        order.delivered = false;
        order.timer = order.date.getTime() - System.currentTimeMillis();
        SFApplication.orders.add(order);
        
        Log.d(LOG_TAG, "1) Время заказа - " + order.date);
        Log.d(LOG_TAG, "1) Время сейчас - " + new Date(System.currentTimeMillis()));
        Log.d(LOG_TAG, "1) Разница - " + order.timer);
        
        order = new Order();
        order.orderNum = 1114458;
        order.date = formatDate("19.08.2015 16:45:18");
        order.delivered = false;
        order.timer = order.date.getTime() - System.currentTimeMillis();
        SFApplication.orders.add(order);
        
        Log.d(LOG_TAG, "2) Время заказа - " + order.date);
        Log.d(LOG_TAG, "2) Время сейчас - " + new Date(System.currentTimeMillis()));
        Log.d(LOG_TAG, "2) Разница - " + order.timer);
    }
    
    public static boolean checkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
            return false;
        } else {
            return true;
        }
    }
    
    public static void setAPIBase(Context context, int cityId) {
        switch (cityId) {
            case 0:
                HttpClient.API_DB_URL = context.getString(R.string.api_piter);
                break;
            case 1:
                HttpClient.API_DB_URL = context.getString(R.string.api_moskow);
                break;
            case 2:
                HttpClient.API_DB_URL = context.getString(R.string.api_ural);
                break;
            case 3:
                HttpClient.API_DB_URL = context.getString(R.string.api_murman);
                break;
        }
    }
}
