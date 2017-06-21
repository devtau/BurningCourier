package ru.burningcourier;

import android.app.Application;
import java.util.ArrayList;
import java.util.List;
import ru.burningcourier.api.model.Order;
import ru.burningcourier.api.model.City;
import ru.burningcourier.utils.PreferencesManager;

public class BCApplication extends Application {
    
    public static List<City> cities;
    public static String token;
    public static ArrayList<Order> orders = new ArrayList<>();
    public static boolean userAuth = false;
    public static boolean geoLocation = false;
    
    @Override
    public void onCreate() {
        super.onCreate();
        //TODO: отключить перед релизом
        PreferencesManager.getInstance(this).setLogin("1111");
        orders = Order.getMockOrders();
    }
}
