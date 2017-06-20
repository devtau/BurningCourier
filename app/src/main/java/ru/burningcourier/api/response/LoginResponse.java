package ru.burningcourier.api.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("token")
    private String token;

    @SerializedName("name")
    private String name;

    @SerializedName("point")
    private String point;
    
    
    public String getToken() {
        return token;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPoint() {
        return point;
    }
}
