package ru.burningcourier.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import ru.burningcourier.BCApplication;
import ru.burningcourier.api.model.Order;

public class TimerService extends Service {
    
    public static final String TIME_TICK = "ru.burningcourier.service.TIME_TICK";
    private static final String LOG_TAG = "TimerService";
    private static final int START_DELAY_MS = 1000;
    private static final int TICK_DELAY_MS = 1000;
    private List<Order> orders;
    private Timer timer;
    
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        orders = new ArrayList<>();
        orders.addAll(BCApplication.orders);
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask () {
            @Override
            public void run() {
                if (orders.size() > 0) {
                    for (Order order : BCApplication.orders) {
                        if (order.isDelivered) {
                            order.timeLeft = 0;
                            orders.remove(order);
                        } else {
                            order.timeLeft = order.date.getTime() - System.currentTimeMillis();
                        }
                    }
                } else {
                    timer.cancel();
                }
            
                Intent intent = new Intent(TIME_TICK);
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                sendBroadcast(intent);
            }
        }, START_DELAY_MS, TICK_DELAY_MS);
        return START_NOT_STICKY;
    }
}
