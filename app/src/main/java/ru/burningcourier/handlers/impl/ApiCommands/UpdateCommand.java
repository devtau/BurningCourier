package ru.burningcourier.handlers.impl.ApiCommands;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import org.json.JSONArray;
import org.json.JSONException;
import ru.burningcourier.R;
import ru.burningcourier.handlers.SFBaseCommand;
import ru.burningcourier.Order;
import ru.burningcourier.sfClasses.SFApplication;
import ru.burningcourier.utils.HttpClient;
import ru.burningcourier.utils.AppUtils;

public class UpdateCommand extends SFBaseCommand {
    
    public static final String UPDATE_EXTRA = "Получение данных";
    private String url;
    
    
    public UpdateCommand(String url) {
        this.url = url;
    }
    
    private UpdateCommand(Parcel in) {
        url = in.readString();
    }
    
    @Override
    protected void doExecute(Intent intent, Context context, ResultReceiver callback) {
        Bundle data = new Bundle();
        HttpClient httpClient = new HttpClient(url);
        if (SFApplication.userAuth && httpClient.execute()) {
            try {
                JSONArray jarrOrders = new JSONArray(httpClient.getResponseInfo());
                SFApplication.orders.clear();
                SFApplication.orders.trimToSize();
                for (int i = 0; i < jarrOrders.length(); i++) {
                    Order order = new Order();
                    order.date = AppUtils.formatDate(jarrOrders.getJSONObject(i).getString("date"));
                    order.timer = order.date.getTime() - System.currentTimeMillis();
                    order.orderNum = jarrOrders.getJSONObject(i).getInt("order");
                    order.address = jarrOrders.getJSONObject(i).getString("address");
                    order.phone = jarrOrders.getJSONObject(i).getString("phone");
                    order.delivered = false;
                    SFApplication.orders.add(order);
                }
                if (SFApplication.orders.size() == 0) {
                    //Если данные пустые, то отправляем ошибку
                    data.putString(UPDATE_EXTRA, context.getString(R.string.update_no_data));
                    notifyFailure(data);
                } else {
                    data.putString(UPDATE_EXTRA, context.getString(R.string.update_data));
                    notifySuccess(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                data.putString(UPDATE_EXTRA, context.getString(R.string.update_error));
                notifyFailure(data);
            }
        } else {
            data.putString(UPDATE_EXTRA, context.getString(R.string.update_error));
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
    }
    
    public static final Parcelable.Creator<UpdateCommand> CREATOR = new Parcelable.Creator<UpdateCommand>() {
        public UpdateCommand createFromParcel(Parcel in) {
            return new UpdateCommand(in);
        }
        
        public UpdateCommand[] newArray(int size) {
            return new UpdateCommand[size];
        }
    };
}
