
package ru.burningcourier.api.response;

import com.google.gson.annotations.SerializedName;
import ru.burningcourier.api.data.UserData;

public class ImageResponse {

    @SerializedName("data")
    private UserData userData;

    @SerializedName("result")
    private String result;

    @SerializedName("code")
    private Integer code;

    public UserData getUserData() {
        return userData;
    }

    public String getResult() {
        return result;
    }

    public Integer getCode() {
        return code;
    }
}
