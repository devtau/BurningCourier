
package ru.burningcourier.api.response;

import com.google.gson.annotations.SerializedName;
import ru.burningcourier.api.data.MenuData;

public class MenuResponse {

    @SerializedName("data")
    public MenuData menuData;

    @SerializedName("result")
    public String result;

    @SerializedName("code")
    public int code;
}
