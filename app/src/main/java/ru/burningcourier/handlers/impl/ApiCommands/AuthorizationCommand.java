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

public class AuthorizationCommand extends SFBaseCommand {
    
    public static final String AUTHORIZATION_STATUS_EXTRA = "AUTHORIZATION_STATUS_EXTRA";
    private String url;
    
    
    public AuthorizationCommand(String url) {
        this.url = url;
    }

    private AuthorizationCommand(Parcel in) {
        url = in.readString();
    }

    @Override
    protected void doExecute(Intent intent, Context context, ResultReceiver callback) {
        Bundle data = new Bundle();
        HttpClient client = new HttpClient(url);
        if (client.execute()) {
            int authStatus = client.getAuthStatus();
            if (authStatus == 0) {
                data.putString(AUTHORIZATION_STATUS_EXTRA, context.getString(R.string.auth_no_login));
                notifyFailure(data);
            } else if (authStatus == 1 || authStatus == 2) {
                SFApplication.userAuth = true;
                SFApplication.geoLocation = authStatus == 1;
                data.putString(AUTHORIZATION_STATUS_EXTRA, context.getString(R.string.auth_login));
                notifySuccess(data);
            }
        } else {
            data.putString(AUTHORIZATION_STATUS_EXTRA, context.getString(R.string.auth_error));
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

    public static final Parcelable.Creator<AuthorizationCommand> CREATOR = new Parcelable.Creator<AuthorizationCommand>() {
        public AuthorizationCommand createFromParcel(Parcel in) {
            return new AuthorizationCommand(in);
        }

        public AuthorizationCommand[] newArray(int size) {
            return new AuthorizationCommand[size];
        }
    };
}

