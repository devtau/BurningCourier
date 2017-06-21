package ru.burningcourier.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import ru.burningcourier.api.requestBody.ChangeStatusRequestBody;
import ru.burningcourier.api.requestBody.LoginRequestBody;
import ru.burningcourier.api.requestBody.OrdersRequestBody;
import ru.burningcourier.api.response.ChangeStatusResponse;
import ru.burningcourier.api.response.CitiesListResponse;
import ru.burningcourier.api.response.LoginResponse;
import ru.burningcourier.api.response.OrdersResponse;

interface BackendAPI {
	
//	адрес сервера до 01.06.17: 93.92.194.154
	String BC_API_BASE_URL = "http://91.190.82.49/";
	String SUFFIX = "/sakura_mobile/hs/courier_test";

	String CITIES_LIST_ENDPOINT = SUFFIX + "/config";
	String LOGIN_ENDPOINT = SUFFIX + "/auth";
	String ORDERS_ENDPOINT = SUFFIX + "/orders";
	String CHANGE_STATUS_ENDPOINT = SUFFIX + "/changestatus";
	
	
	@GET(CITIES_LIST_ENDPOINT)
	Call<CitiesListResponse> getCitiesList();
	
	@POST(LOGIN_ENDPOINT)
	Call<LoginResponse> login(@Body LoginRequestBody requestBody);
	
	@POST(ORDERS_ENDPOINT)
	Call<OrdersResponse> getOrders(@Header("Authorization") String token, @Body OrdersRequestBody requestBody);
	
	@POST(CHANGE_STATUS_ENDPOINT)
	Call<ChangeStatusResponse> changeStatus(@Header("Authorization") String token, @Body ChangeStatusRequestBody requestBody);
}
