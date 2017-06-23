package ru.burningcourier.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import ru.burningcourier.api.requestBody.CallClientRequestBody;
import ru.burningcourier.api.requestBody.ChangeStatusRequestBody;
import ru.burningcourier.api.requestBody.LoginRequestBody;
import ru.burningcourier.api.requestBody.OrdersRequestBody;
import ru.burningcourier.api.requestBody.UploadPhotoUrlRequestBody;
import ru.burningcourier.api.response.ChangeStatusResponse;
import ru.burningcourier.api.response.CitiesListResponse;
import ru.burningcourier.api.response.LoginResponse;
import ru.burningcourier.api.response.OrdersResponse;
import ru.burningcourier.api.response.UploadPhotoResponse;

public interface BackendAPI {
	
//	адрес сервера до 01.06.17: 93.92.194.154
	String BC_API_BASE_URL = "http://91.190.82.49:8096/";
	String PHOTO_API_BASE_URL = "http://courier.stage.devcluster.ru";
	String SUFFIX = "/sakura_mobile/hs/courier_test";

	String CITIES_LIST_ENDPOINT = SUFFIX + "/config";
	String LOGIN_ENDPOINT = SUFFIX + "/auth";
	String ORDERS_ENDPOINT = SUFFIX + "/orders";
	String CHANGE_STATUS_ENDPOINT = SUFFIX + "/changestatus";
	String CALL_CLIENT_ENDPOINT = SUFFIX + "/call";
    String UPLOAD_PHOTO_ENDPOINT = "/courier";
    String VIEW_PHOTO_ENDPOINT = "/files/";
    String UPLOAD_PHOTO_URL_ENDPOINT = SUFFIX + "/uploadphoto";
	
	
	@GET(CITIES_LIST_ENDPOINT)
	Call<CitiesListResponse> getCitiesList();
	
	@POST(LOGIN_ENDPOINT)
	Call<LoginResponse> login(@Body LoginRequestBody requestBody);
	
	@POST(ORDERS_ENDPOINT)
	Call<OrdersResponse> getOrders(@Header("Authorization") String token, @Body OrdersRequestBody requestBody);
	
	@POST(CHANGE_STATUS_ENDPOINT)
	Call<ChangeStatusResponse> changeStatus(@Header("Authorization") String token, @Body ChangeStatusRequestBody requestBody);
	
	@POST(CALL_CLIENT_ENDPOINT)
	Call<Void> callClient(@Header("Authorization") String token, @Body CallClientRequestBody requestBody);
    
    @Multipart
	@POST(UPLOAD_PHOTO_ENDPOINT)
	Call<UploadPhotoResponse> uploadPhoto(@Part("description") RequestBody description, @Part MultipartBody.Part file);
	
	@POST(UPLOAD_PHOTO_URL_ENDPOINT)
	Call<Void> uploadPhotoUrl(@Header("Authorization") String token, @Body UploadPhotoUrlRequestBody requestBody);
}
