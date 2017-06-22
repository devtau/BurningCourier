package ru.burningcourier.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import ru.burningcourier.api.model.Order;
import ru.burningcourier.R;

public class AppUtils {
    
    private static final long RED_TIME = 15 * 60_000;
    private static final long HOUR = 60 * 60_000;
    private static final long DAY = HOUR * 24;
    public static final long SESSION_TIME = DAY;
    public static final String DATE_FORMAT = "yyyyMMddHHmmss";
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private static final String LOG_TAG = "AppUtils";
    
    
    public static Date formatDate(String dateString) {
        Date date = null;
        try {
            date = DATE_FORMATTER.parse(dateString);
        } catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return date;
    }
    
    public static String formatTimer(Order order) {
        int timeZoneOffset = TimeZone.getDefault().getOffset(System.currentTimeMillis());
        if (order.isDelivered) {
            return "Заказ доставлен";
        }
    
        if (order.timeLeft < -DAY) {
            return String.valueOf((int) (order.timeLeft / HOUR)) + " ч";
        }
    
        if (order.timeLeft < 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("-H:mm:ss", Locale.getDefault());
            return formatter.format(new Date(order.timeLeft * (-1) - timeZoneOffset));
        }
        
        if (order.timeLeft > 0) {
            boolean lessThanHourLeft = order.timeLeft < HOUR;
            SimpleDateFormat formatter = new SimpleDateFormat(lessThanHourLeft ? "mm:ss" : "H:mm:ss", Locale.getDefault());
            return formatter.format(new Date(order.timeLeft));
        }
        return "";
    }
    
    public static int processTimerColor(Order order, Context context) {
        boolean isOrderInRedZone = order.timeLeft <= RED_TIME && !order.isDelivered;
        int timerTextColorId = isOrderInRedZone ? R.color.colorRed : R.color.colorGray74;
        return ContextCompat.getColor(context, timerTextColorId);
    }
    
    public static boolean checkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        if (!isConnected) {
            Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
