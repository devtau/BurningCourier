package ru.burningcourier.api;

import android.support.annotation.NonNull;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.burningcourier.api.model.Geo;
import ru.burningcourier.api.requestBody.CallClientRequestBody;
import ru.burningcourier.api.requestBody.ChangeStatusRequestBody;
import ru.burningcourier.api.requestBody.LoginRequestBody;
import ru.burningcourier.api.requestBody.OrdersRequestBody;
import ru.burningcourier.api.requestBody.UploadPhotoRequestBody;
import ru.burningcourier.api.response.ChangeStatusResponse;
import ru.burningcourier.api.response.CitiesListResponse;
import ru.burningcourier.api.response.LoginResponse;
import ru.burningcourier.api.response.OrdersResponse;
import ru.burningcourier.utils.AppUtils;
/**
 * library authors page: http://square.github.io/retrofit/
 */
public class RESTClientImpl implements RESTClient {

	private static final String LOG_TAG = "RESTClient";
	private static final int TIMEOUT_CONNECT = 10;
	private static final int TIMEOUT_READ = 60;
	private static final int TIMEOUT_WRITE = 120;
	private static OkHttpClient httpClient;
	private RESTClientView view;


	public RESTClientImpl(RESTClientView view) {
		this.view = view;
	}
	
	
	@Override
	public void getCitiesList() {
		Call<CitiesListResponse> call = getBackendAPIClient(BackendAPI.BC_API_BASE_URL).getCitiesList();
		Callback<CitiesListResponse> callback = new Callback<CitiesListResponse>() {
			@Override
			public void onResponse (Call<CitiesListResponse> call, Response<CitiesListResponse> response){
				if (response.isSuccessful()) {
					Log.d(LOG_TAG, "retrofit getCitiesList response isSuccessful");
					view.processCitiesList(response.body().getCities());
				} else {
					handleError(response.code(), response.errorBody());
				}
			}
			
			@Override
			public void onFailure (Call <CitiesListResponse> call, Throwable t){
				Log.e(LOG_TAG, "retrofit failure: " + t.getLocalizedMessage());
				handleFailure(t.getLocalizedMessage());
			}
		};
		call.enqueue(callback);
	}
	
	@Override
	public void login(String cityUrl, String login, String password, String deviceId) {
		Call<LoginResponse> call = getBackendAPIClient(cityUrl).login(new LoginRequestBody(login, password, deviceId));
		Callback<LoginResponse> callback = new Callback<LoginResponse>() {
			@Override
			public void onResponse (Call<LoginResponse> call, Response<LoginResponse> response){
				if (response.isSuccessful()) {
					Log.d(LOG_TAG, "retrofit login response isSuccessful");
					view.processLoginSuccess(response.body().getToken());
				} else {
					view.processLoginFail();
					handleError(response.code(), response.errorBody());
				}
			}
			
			@Override
			public void onFailure (Call <LoginResponse> call, Throwable t){
				Log.e(LOG_TAG, "retrofit failure: " + t.getLocalizedMessage());
				view.processLoginFail();
				handleFailure(t.getLocalizedMessage());
			}
		};
		call.enqueue(callback);
	}
	
	@Override
	public void getOrders(String cityUrl, String token, List<Geo> geos) {
		Call<OrdersResponse> call = getBackendAPIClient(cityUrl).getOrders(token, new OrdersRequestBody(Geo.getMockGeos()));
		Callback<OrdersResponse> callback = new Callback<OrdersResponse>() {
			@Override
			public void onResponse (Call<OrdersResponse> call, Response<OrdersResponse> response){
				if (response.isSuccessful()) {
					Log.d(LOG_TAG, "retrofit getOrders response isSuccessful");
					view.processOrders(response.body().getOrders());
				} else {
					handleError(response.code(), response.errorBody());
				}
			}
			
			@Override
			public void onFailure (Call <OrdersResponse> call, Throwable t){
				Log.e(LOG_TAG, "retrofit failure: " + t.getLocalizedMessage());
				handleFailure(t.getLocalizedMessage());
			}
		};
		call.enqueue(callback);
	}
	
	@Override
	public void changeStatus(String cityUrl, String token, String orderId, int newStatusId, List<Geo> geos) {
		Call<ChangeStatusResponse> call = getBackendAPIClient(cityUrl).changeStatus(token, new ChangeStatusRequestBody(orderId, newStatusId, Geo.getMockGeos()));
		Callback<ChangeStatusResponse> callback = new Callback<ChangeStatusResponse>() {
			@Override
			public void onResponse (Call<ChangeStatusResponse> call, Response<ChangeStatusResponse> response){
				if (response.isSuccessful()) {
					Log.d(LOG_TAG, "retrofit changeStatus response isSuccessful");
					view.processOrderStatusChanged(response.body().getTracking());
				} else {
					handleError(response.code(), response.errorBody());
				}
			}
			
			@Override
			public void onFailure (Call <ChangeStatusResponse> call, Throwable t){
				Log.e(LOG_TAG, "retrofit failure: " + t.getLocalizedMessage());
				handleFailure(t.getLocalizedMessage());
			}
		};
		call.enqueue(callback);
	}
	
	@Override
	public void callClient(String cityUrl, String token, String orderId) {
		Call<Void> call = getBackendAPIClient(cityUrl).callClient(token, new CallClientRequestBody(orderId));
		Callback<Void> callback = new Callback<Void>() {
			@Override
			public void onResponse (Call<Void> call, Response<Void> response){
				if (response.isSuccessful()) {
					Log.d(LOG_TAG, "retrofit callClient response isSuccessful");
				} else {
					handleError(response.code(), response.errorBody());
				}
			}
			
			@Override
			public void onFailure (Call <Void> call, Throwable t){
				Log.e(LOG_TAG, "retrofit failure: " + t.getLocalizedMessage());
				handleFailure(t.getLocalizedMessage());
			}
		};
		call.enqueue(callback);
	}
	
	@Override
	public void uploadPhoto(String cityUrl, String token, String orderId, String photoUrl, int checkSumm) {
		Call<Void> call = getBackendAPIClient(cityUrl).uploadPhoto(token, new UploadPhotoRequestBody(orderId, photoUrl, checkSumm));
		Callback<Void> callback = new Callback<Void>() {
			@Override
			public void onResponse (Call<Void> call, Response<Void> response){
				if (response.isSuccessful()) {
					Log.d(LOG_TAG, "retrofit uploadPhoto response isSuccessful");
				} else {
					handleError(response.code(), response.errorBody());
				}
			}
			
			@Override
			public void onFailure (Call <Void> call, Throwable t){
				Log.e(LOG_TAG, "retrofit failure: " + t.getLocalizedMessage());
				handleFailure(t.getLocalizedMessage());
			}
		};
		call.enqueue(callback);
	}


	private BackendAPI getBackendAPIClient(String baseUrl) {
		Gson gson = new GsonBuilder().setDateFormat(AppUtils.DATE_FORMAT).create();
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(baseUrl)
				.addConverterFactory(GsonConverterFactory.create(gson))
				.client(getClient())
				.build();
		return retrofit.create(BackendAPI.class);
	}

	@NonNull
	private static OkHttpClient getClient() {
		OkHttpClient client = httpClient;
		if (client == null) {
			synchronized (RESTClientImpl.class) {
				client = httpClient;
				if (client == null) {
					client = httpClient = buildClient();
				}
			}
		}
		return client;
	}

	@NonNull
	private static OkHttpClient buildClient() {
		return new OkHttpClient.Builder()
				.connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
				.readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
				.writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS)
				.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
//				.addInterceptor(chain -> {
//					Request newRequest  = chain.request().newBuilder()
//							.addHeader("Authorization", "Bearer " + "some long token")
//							.build();
//					return chain.proceed(newRequest);
//				})
				.build();
	}

	private void handleError(int errorCode, ResponseBody errorBody) {
		String logMsg = "Код ошибки: " + String.valueOf(errorCode) + " ";
		try {
			String errorSubCodeString = new JSONObject(errorBody.string()).getString("error");
			logMsg += errorSubCodeString;
			int errorSubCode = Integer.parseInt(errorSubCodeString);
			switch (errorSubCode) {
				case ErrorConstants.TOKEN_CREATION_FAILURE:
					logMsg += "\nошибка создания токена";
					break;
				case ErrorConstants.TOKEN_EXPIRED:
					logMsg += "\nсрок жизни токена истек";
					break;
				case ErrorConstants.NO_SUCH_USER:
					logMsg += "\nсотрудник с таким телефоном не найден в базе";
					break;
				case ErrorConstants.WRONG_PASSWORD:
					logMsg += "\nневерный пароль";
					break;
				case ErrorConstants.ALREADY_LOGGED_IN:
					logMsg += "\nповторная авторизация с другого устройства в срок жизни токена";
					break;
			}
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		Log.e(LOG_TAG, logMsg);
		view.showToast(logMsg);

		try {
			Log.e(LOG_TAG, errorBody.string());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleFailure(String failureMessage) {
		view.showToast(failureMessage);
	}
}
