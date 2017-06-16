
package ru.burningcourier.api.response;

import com.google.gson.annotations.SerializedName;
import ru.burningcourier.api.data.MenuCategoryDetailsData;

public class MenuCategoryDetailsResponse {

    @SerializedName("data")
    public MenuCategoryDetailsData menuCategoryDetailsData;

    @SerializedName("result")
    public String result;

    @SerializedName("code")
    public int code;
}
