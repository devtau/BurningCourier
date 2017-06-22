package ru.burningcourier.api.requestBody;

import com.google.gson.annotations.SerializedName;

public class CallClientRequestBody {
    
    @SerializedName("order_id")
    private String orderId;
    
    
    public CallClientRequestBody(String orderId) {
        this.orderId = orderId;
    }
}
