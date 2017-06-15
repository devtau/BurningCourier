package ru.burningcourier.handlers.impl.ApiCommands;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import ru.burningcourier.R;
import ru.burningcourier.handlers.SFBaseCommand;
import ru.burningcourier.Order;
import ru.burningcourier.sfClasses.SFApplication;

public class TimerCommand extends SFBaseCommand {
    
    private static final String LOG_TAG = "TimerCommand";
    public static final String TIMER = "Таймер";
    private int time;
    private List<Order> orders;

    public TimerCommand(int time) {
        this.time = time;
    }

    private TimerCommand(Parcel in) {
        time = in.readInt();
    }

    @Override
    protected void doExecute(Intent intent, Context context, ResultReceiver callback) {
        Bundle data = new Bundle();
        orders = new ArrayList<>();
        orders.addAll(SFApplication.orders);
        synchronOrders();

        while (orders.size() > 0) {
            try {
                TimeUnit.SECONDS.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
                data.putString(TIMER, context.getString(R.string.timer_error));
                notifyFailure(data);
            }
            data.putString(TIMER, context.getString(R.string.timer_progress));
            sendProgress(0);
            synchronOrders();
        }
        context.stopService(SFApplication.geoIntent);
        data.putString(TIMER, context.getString(R.string.timer_end));
        notifySuccess(data);
        Log.d(LOG_TAG, context.getString(R.string.timer_end));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(time);
    }

    public static final Parcelable.Creator<TimerCommand> CREATOR = new Parcelable.Creator<TimerCommand>() {
        public TimerCommand createFromParcel(Parcel in) {
            return new TimerCommand(in);
        }

        public TimerCommand[] newArray(int size) {
            return new TimerCommand[size];
        }
    };

    private void synchronOrders() {
        for (Order order : SFApplication.orders) {
            long timer = order.date.getTime() - System.currentTimeMillis();

            if (!order.delivered &&  timer > 0) {
                order.timer = timer;
            } else {
                order.timer = 0;
                orders.remove(order);
            }
        }
    }
}
