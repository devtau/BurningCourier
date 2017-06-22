package ru.burningcourier.api.requestBody;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import ru.burningcourier.api.model.Geo;

public class ChangeStatusRequestBody {
    
    @SerializedName("order_id")
    private String orderId;
    
    @SerializedName("status_id")
    private int newStatusId;
    
    @SerializedName("geo")
    private List<Geo> geo;
    
    
    public ChangeStatusRequestBody(String orderId, int newStatusId, List<Geo> geo) {
        this.orderId = orderId;
        this.newStatusId = newStatusId;
        this.geo = geo;
    }
}
