
package ru.burningcourier.api.data;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import ru.burningcourier.api.model.MenuCategory;

public class MenuData {

    @SerializedName("menu")
    public ArrayList<MenuCategory> menuCategoriesList = null;
}
