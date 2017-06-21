package ru.burningcourier.api.response;

import com.google.gson.annotations.SerializedName;

public class ChangeStatusResponse {

    @SerializedName("tracking")
    private int tracking;
    
    public int getTracking() {
        return tracking;
    }
}
