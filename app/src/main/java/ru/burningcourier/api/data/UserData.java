
package ru.burningcourier.api.data;

import com.google.gson.annotations.SerializedName;
import ru.burningcourier.api.model.UserProfile;

public class UserData {

    @SerializedName("user")
    private UserProfile userProfile;

    @SerializedName("token")
    private String token;


    public UserProfile getUserProfile() {
        return userProfile;
    }

    public String getToken() {
        return token;
    }
}
