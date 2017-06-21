package ru.burningcourier.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import ru.burningcourier.api.model.Order;
import ru.burningcourier.R;

public class AppUtils {
    
    private static final long RED_TIME = 15 * 60_000;
    private static final long HOUR = 60 * 60_000;
    public static final long SESSION_TIME = HOUR * 8;
    public static final String DATE_FORMAT = "yyyyMMddHHmmss";
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private static final String LOG_TAG = "AppUtils";
    
    
    public static Date formatDate(String dateString) {
        Date date = null;
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss", Locale.getDefault());
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            Log.d(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }
        return date;
    }
    
    public static String formatTimer(Order order) {
        String timerFormatted = null;
        if (order.isDelivered) {
            timerFormatted = "Заказ доставлен";
        } else {
            SimpleDateFormat formatter;
            if (order.timeLeft > 0) {
                try {
                    if (order.timeLeft < HOUR) {
                        formatter = new SimpleDateFormat("00:mm:ss", Locale.getDefault());
                    } else {
                        formatter = new SimpleDateFormat("hh:mm", Locale.getDefault());
                    }
                    timerFormatted = formatter.format(new Date(order.timeLeft));
                } catch (Error e) {
                    Log.d(LOG_TAG, e.getMessage());
                    e.printStackTrace();
                }
            } else {
                formatter = new SimpleDateFormat("-00:mm:ss", Locale.getDefault());
                timerFormatted = formatter.format(new Date(order.timeLeft));
            }
        }
        return timerFormatted;
    }
    
    public static int processTimerColor(Order order, Context context) {
        boolean isOrderInRedZone = order.timeLeft <= RED_TIME && !order.isDelivered;
        int timerTextColorId = isOrderInRedZone ? R.color.colorRed : R.color.colorGray111;
        return ContextCompat.getColor(context, timerTextColorId);
    }
    
    public static boolean checkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
