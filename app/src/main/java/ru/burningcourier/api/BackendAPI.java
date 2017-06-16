package ru.burningcourier.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import ru.burningcourier.api.requestBody.LoginBody;
import ru.burningcourier.api.requestBody.PhoneValidationBody;
import ru.burningcourier.api.requestBody.RegistrationBody;
import ru.burningcourier.api.response.ImageResponse;
import ru.burningcourier.api.response.LoginResponse;
import ru.burningcourier.api.response.MenuCategoryDetailsResponse;
import ru.burningcourier.api.response.MenuResponse;
import ru.burningcourier.api.response.RegistrationResponse;

public interface BackendAPI {

	String SW_API_BASE_URL = "http://sw.stage.devcluster.ru";
//	String SW_API_BASE_URL = "http://spb.swprod.spbdev.ru";

	String VALIDATE_PHONE_ENDPOINT = "/api/phone/validate";
	String REGISTER_NEW_USER_ENDPOINT = "/api/user/register";
	String LOGIN_ENDPOINT = "/api/auth/login";
	String MENU_ENDPOINT = "/api/{locale}/{city}/menu/get_list";
	String MENU_CATEGORY_DETAILS_ENDPOINT = "/api/{locale}/{city}/product/get_list";
	String IMAGE_ENDPOINT = "/img/{filename}/{width}x{height}";


	@POST(VALIDATE_PHONE_ENDPOINT)
	Call<Void> validatePhone(@Body PhoneValidationBody phone);

	@POST(REGISTER_NEW_USER_ENDPOINT)
	Call<RegistrationResponse> registerNewUser(@Body RegistrationBody registrationBody);

	@POST(LOGIN_ENDPOINT)
	Call<LoginResponse> login(@Body LoginBody loginBody);

	@GET(MENU_ENDPOINT)
	Call<MenuResponse> getMenu(@Path("locale") String locale, @Path("city") String city);

	@GET(MENU_CATEGORY_DETAILS_ENDPOINT)
	Call<MenuCategoryDetailsResponse> getMenuCategoryDetails(@Path("locale") String locale, @Path("city") String city);

	@GET(IMAGE_ENDPOINT)
	Call<ImageResponse> getImage(@Path("filename") String filename, @Path("width") int width, @Path("height") int height);
}
