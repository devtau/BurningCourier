
package ru.burningcourier.api.data;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import ru.burningcourier.api.model.Product;

public class MenuCategoryDetailsData {

    @SerializedName("product")
    public ArrayList<Product> product = null;
}
