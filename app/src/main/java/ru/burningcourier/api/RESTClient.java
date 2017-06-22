package ru.burningcourier.api;

import java.util.List;
import ru.burningcourier.api.model.Geo;

public interface RESTClient {

	void getCitiesList();
	void login(String cityUrl, String login, String password, String deviceId);
	void getOrders(String cityUrl, String token, List<Geo> geos);
	void changeStatus(String cityUrl, String token, String orderId, int newStatusId, List<Geo> geos);
	void callClient(String cityUrl, String token, String orderId);
	void uploadPhoto(String cityUrl, String token, String orderId, String photoUrl, int checkSumm);
}
