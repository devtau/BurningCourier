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
import ru.burningcourier.api.HttpClient;

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
                JSONArray jsonArray = new JSONArray(httpClient.getResponseInfo());
                //TODO: отключить перед релизом
//                SFApplication.orders.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Order order = new Order(jsonArray.getJSONObject(i));
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
