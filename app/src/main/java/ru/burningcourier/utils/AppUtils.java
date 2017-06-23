package ru.burningcourier.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ru.burningcourier.api.BackendAPI;
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
    
    public static File compressFile(File photoFile) {
        int fileSizeKb = Math.round(photoFile.length() / 1024);
        int targetFileSizeKb = 1024;
        if (fileSizeKb < targetFileSizeKb) return photoFile;
        
        int compressRate = Math.round(targetFileSizeKb * 100 / fileSizeKb);
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        File photoFileCompressed = new File(photoFile.getParentFile(), photoFile.getName() + "-compressed.jpg");
        FileOutputStream out = null;
        try {
            photoFileCompressed.createNewFile();
            out = new FileOutputStream(photoFileCompressed.getAbsolutePath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressRate, out);
            photoFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d(LOG_TAG, "photoFile of " + fileSizeKb + "Kb compressed to " + Math.round(photoFileCompressed.length() / 1024) + "Kb");
        return photoFileCompressed;
    }
    
    public static Uri buildUriFromPath(String validPath, Context context) {
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(context, "ru.burningcourier.fileprovider", new File(validPath));
        } else {
            fileUri = Uri.fromFile(new File(validPath));
        }
        Log.d(LOG_TAG, "buildUriFromPath fileUri is: " + fileUri);
        return fileUri;
    }
}
