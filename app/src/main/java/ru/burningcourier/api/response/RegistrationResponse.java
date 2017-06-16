
package ru.burningcourier.api.response;

import com.google.gson.annotations.SerializedName;

public class RegistrationResponse {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("token")
    private String token;//eqjPIyTMPVX6qc3Vp75K6799xhTQJZo1NZMBb7jzPYQ0Ri8CaTjizduu8f47AfuZ


    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }
}
