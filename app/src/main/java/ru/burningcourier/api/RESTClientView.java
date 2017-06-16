package ru.burningcourier.api;

import android.content.res.Resources;
import java.util.ArrayList;
import java.util.List;
import ru.burningcourier.api.model.MenuCategory;
import ru.burningcourier.api.model.Product;

public interface RESTClientView {
	void processNewMenu(List<MenuCategory> menuCategoriesList);
	void processNewProductsList(ArrayList<Product> menuCategoriesList, int categoryId);
	void showToast(String msg);
	Resources getResources();
}
