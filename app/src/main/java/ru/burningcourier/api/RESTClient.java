package ru.burningcourier.api;

public interface RESTClient {

	void validatePhone(String phone);
	void registerNewUser(String phone, String password, int smsValidationCode, String name, String birthday,
                         String sex, String email, String socialProvider, String socialId);
	void login(String login, String password);
	void getMenu(String locale, String city);
	void getMenuCategoryDetails(String locale, String city, int menuId);
	void getImage(String filename, int width, int height);
}
