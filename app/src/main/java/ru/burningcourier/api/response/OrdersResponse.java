package ru.burningcourier.api.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import ru.burningcourier.api.model.Order;

public class OrdersResponse {

    @SerializedName("tracking")
    private String tracking;

    @SerializedName("orders")
    private List<Order> orders;
    
    
    public String getTracking() {
        return tracking;
    }
    
    public List<Order> getOrders() {
        return orders;
    }
}
