package ru.burningcourier.api;

import android.support.annotation.NonNull;
import android.util.Log;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.burningcourier.api.requestBody.LoginBody;
import ru.burningcourier.api.requestBody.PhoneValidationBody;
import ru.burningcourier.api.requestBody.RegistrationBody;
import ru.burningcourier.api.response.ImageResponse;
import ru.burningcourier.api.response.LoginResponse;
import ru.burningcourier.api.response.MenuCategoryDetailsResponse;
import ru.burningcourier.api.response.MenuResponse;
import ru.burningcourier.api.response.RegistrationResponse;
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
	public void validatePhone(String phone) {
		Call<Void> call = getBackendAPIClient().validatePhone(new PhoneValidationBody(phone));
		Callback<Void> callback = new Callback<Void>() {
			@Override
			public void onResponse (Call<Void> call, Response<Void> response){
				if (response.isSuccessful()) {
					Log.d(LOG_TAG, "retrofit response isSuccessful");
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
	public void registerNewUser(String phone, String password, int smsValidationCode, String name, String birthday,
								String sex, String email, String socialProvider, String socialId) {
		RegistrationBody body = new RegistrationBody(phone, password, smsValidationCode, name, birthday, sex, email, socialProvider, socialId);
		Call<RegistrationResponse> call = getBackendAPIClient().registerNewUser(body);
		Callback<RegistrationResponse> callback = new Callback<RegistrationResponse>() {
			@Override
			public void onResponse (Call<RegistrationResponse> call, Response<RegistrationResponse> response){
				if (response.isSuccessful()) {
					Log.d(LOG_TAG, "retrofit response isSuccessful");
				} else {
					handleError(response.code(), response.errorBody());
				}
			}

			@Override
			public void onFailure (Call <RegistrationResponse> call, Throwable t){
				Log.e(LOG_TAG, "retrofit failure: " + t.getLocalizedMessage());
				handleFailure(t.getLocalizedMessage());
			}
		};
		call.enqueue(callback);
	}

	@Override
	public void login(String login, String password) {
		Call<LoginResponse> call = getBackendAPIClient().login(new LoginBody(login, password));
		Callback<LoginResponse> callback = new Callback<LoginResponse>() {
			@Override
			public void onResponse (Call<LoginResponse> call, Response<LoginResponse> response){
				if (response.isSuccessful()) {
					Log.d(LOG_TAG, "retrofit response isSuccessful");
				} else {
					handleError(response.code(), response.errorBody());
				}
			}

			@Override
			public void onFailure (Call <LoginResponse> call, Throwable t){
				Log.e(LOG_TAG, "retrofit failure: " + t.getLocalizedMessage());
				handleFailure(t.getLocalizedMessage());
			}
		};
		call.enqueue(callback);
	}

	@Override
	public void getMenu(String locale, String city) {
		Call<MenuResponse> call = getBackendAPIClient().getMenu(locale, city);
		Callback<MenuResponse> callback = new Callback<MenuResponse>() {
			@Override
			public void onResponse (Call<MenuResponse> call, Response<MenuResponse> response){
				if (response.isSuccessful()) {
					Log.d(LOG_TAG, "getMenu retrofit response isSuccessful");
					view.processNewMenu(response.body().menuData.menuCategoriesList);
				} else {
					handleError(response.code(), response.errorBody());
				}
			}

			@Override
			public void onFailure (Call <MenuResponse> call, Throwable t){
				Log.e(LOG_TAG, "retrofit failure: " + t.getLocalizedMessage());
				handleFailure(t.getLocalizedMessage());
			}
		};
		call.enqueue(callback);
	}

	@Override
	public void getMenuCategoryDetails(String locale, String city, int menuId) {
		Call<MenuCategoryDetailsResponse> call = getBackendAPIClient().getMenuCategoryDetails(locale, city);
		Callback<MenuCategoryDetailsResponse> callback = new Callback<MenuCategoryDetailsResponse>() {
			@Override
			public void onResponse (Call<MenuCategoryDetailsResponse> call, Response<MenuCategoryDetailsResponse> response){
				if (response.isSuccessful()) {
					Log.d(LOG_TAG, "getMenu retrofit response isSuccessful");
					view.processNewProductsList(response.body().menuCategoryDetailsData.product, menuId);
				} else {
					handleError(response.code(), response.errorBody());
				}
			}

			@Override
			public void onFailure (Call <MenuCategoryDetailsResponse> call, Throwable t){
				Log.e(LOG_TAG, "retrofit failure: " + t.getLocalizedMessage());
				handleFailure(t.getLocalizedMessage());
			}
		};
		call.enqueue(callback);
	}

	@Override
	public void getImage(String filename, int width, int height) {
		Call<ImageResponse> call = getBackendAPIClient().getImage(filename, width, height);
		Callback<ImageResponse> callback = new Callback<ImageResponse>() {
			@Override
			public void onResponse (Call<ImageResponse> call, Response<ImageResponse> response){
				if (response.isSuccessful()) {
					Log.d(LOG_TAG, "getImage retrofit response isSuccessful");
//					view.processNewMenu(response.body().menuData.menuCategoriesList);
				} else {
					handleError(response.code(), response.errorBody());
				}
			}

			@Override
			public void onFailure (Call <ImageResponse> call, Throwable t){
				Log.e(LOG_TAG, "retrofit failure: " + t.getLocalizedMessage());
				handleFailure(t.getLocalizedMessage());
			}
		};
		call.enqueue(callback);
	}


	private BackendAPI getBackendAPIClient() {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(BackendAPI.SW_API_BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
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
		String logMsg = "retrofit response is not successful. errorCode: " + String.valueOf(errorCode);
		switch (errorCode) {
			case ErrorConstants.BAD_REQUEST:
				logMsg += " Check request Json";
				break;
			case ErrorConstants.NOT_FOUND:
				logMsg += " No such user";
				break;
			case ErrorConstants.PHONE_CODE_INVALID:
				logMsg += " Invalid sms confirmation code";
				break;
			default:
				logMsg += " Check ENDPOINT and method parameters if any";
				break;
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
