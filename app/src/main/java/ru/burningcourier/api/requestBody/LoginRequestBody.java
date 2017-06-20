package ru.burningcourier.api.requestBody;

import com.google.gson.annotations.SerializedName;

public class LoginRequestBody {
    
    @SerializedName("login")
    private String login;
    
    @SerializedName("pwd")
    private String password;
    
    @SerializedName("device_id")
    private String deviceId;
    
    
    public LoginRequestBody(String login, String password, String deviceId) {
        this.login = login;
        this.password = password;
        this.deviceId = deviceId;
    }
}
