package ru.burningcourier.handlers.impl.ApiCommands;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import ru.burningcourier.R;
import ru.burningcourier.handlers.SFBaseCommand;
import ru.burningcourier.sfClasses.SFApplication;
import ru.burningcourier.utils.HttpClient;

public class SendCommand extends SFBaseCommand {
    
    public static final String DELIVER_STATUS_EXTRA = "DELIVER_STATUS_EXTRA";
    private String url;
    private int orderPos;
    private HttpClient basicAuthBCClient;

    public SendCommand(String _url, int _orderPos) {
        orderPos = _orderPos;
        url = String.format(HttpClient.DELIVER_TEMPLATE, _url, SFApplication.CURRENT_LOGIN, SFApplication.orders.get(orderPos).orderNum);
    }

    private SendCommand(Parcel in) {
        url = in.readString();
        orderPos = in.readInt();
    }

    @Override
    protected void doExecute(Intent intent, Context context, ResultReceiver callback) {
        int deliveryStatus;
        boolean resultStatus;
        Bundle data = new Bundle();
        basicAuthBCClient = new HttpClient(url);
        resultStatus = basicAuthBCClient.execute();

        if (SFApplication.userAuth && resultStatus) {
            deliveryStatus = basicAuthBCClient.getDeliveryStatus();
            if (deliveryStatus == 0) {
                data.putString(DELIVER_STATUS_EXTRA, context.getString(R.string.order_not_delivered));
                notifyFailure(data);
            }
            if (deliveryStatus == 1) {
                SFApplication.orders.get(orderPos).delivered = true;
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
        parcel.writeInt(orderPos);
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
