package ru.burningcourier.handlers.impl.ApiCommands;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import ru.burningcourier.Order;
import ru.burningcourier.R;
import ru.burningcourier.handlers.SFBaseCommand;
import ru.burningcourier.sfClasses.SFApplication;
import ru.burningcourier.utils.HttpClient;

public class SendCommand extends SFBaseCommand {
    
    public static final String DELIVER_STATUS_EXTRA = "DELIVER_STATUS_EXTRA";
    private String url;
    private Order order;
    
    public SendCommand(String url, Order order, String login) {
        this.order = order;
        this.url = String.format(HttpClient.DELIVER_TEMPLATE, url, login, order.orderNum);
    }

    private SendCommand(Parcel in) {
        url = in.readString();
        order = in.readParcelable(Order.class.getClassLoader());
    }

    @Override
    protected void doExecute(Intent intent, Context context, ResultReceiver callback) {
        int deliveryStatus;
        boolean resultStatus;
        Bundle data = new Bundle();
        HttpClient httpClient = new HttpClient(url);
        resultStatus = httpClient.execute();

        if (SFApplication.userAuth && resultStatus) {
            deliveryStatus = httpClient.getDeliveryStatus();
            if (deliveryStatus == 0) {
                data.putString(DELIVER_STATUS_EXTRA, context.getString(R.string.order_not_delivered));
                notifyFailure(data);
            }
            if (deliveryStatus == 1) {
                order.delivered = true;
                data.putString(DELIVER_STATUS_EXTRA, context.getString(R.string.order_delivered));
                notifySuccess(data);
            }
        } else {
            data.putString(DELIVER_STATUS_EXTRA, context.getString(R.string.auth_error));
            notifyFailure(data);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(url);
        parcel.writeParcelable(order, i);
    }

    public static final Parcelable.Creator<SendCommand> CREATOR = new Parcelable.Creator<SendCommand>() {
        public SendCommand createFromParcel(Parcel in) {
            return new SendCommand(in);
        }

        public SendCommand[] newArray(int size) {
            return new SendCommand[size];
        }
    };
}
