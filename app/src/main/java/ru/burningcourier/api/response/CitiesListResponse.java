package ru.burningcourier.api.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import ru.burningcourier.api.model.City;

public class CitiesListResponse {

    @SerializedName("cities")
    private List<City> cities;
    
    
    public List<City> getCities() {
        return cities;
    }
}
