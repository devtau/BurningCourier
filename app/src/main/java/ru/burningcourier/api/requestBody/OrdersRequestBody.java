package ru.burningcourier.api.requestBody;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import ru.burningcourier.api.model.Geo;

public class OrdersRequestBody {
    
    @SerializedName("geo")
    private List<Geo> geo;
    
    public OrdersRequestBody(List<Geo> geo) {
        this.geo = geo;
    }
    
    public OrdersRequestBody() {
        geo = new ArrayList<>();
        geo.add(new Geo("20140924173211", 11.11, 22.22, 220));
    }
}
