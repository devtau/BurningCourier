package ru.burningcourier.api;

import java.util.List;
import ru.burningcourier.api.model.City;
import ru.burningcourier.api.model.Order;

public interface RESTClientView {
	void processCitiesList(List<City> cities);
	void processLoginSuccess(String token);
	void processLoginFail();
	void processOrders(List<Order> menuCategoriesList);
	void processOrderStatusChanged(int tracking);
    void processPhotoUploaded(String photoUrl);
    void processPhotoUrlUploaded();
    void showToast(String msg);
}
