package ru.burningcourier.sfClasses;

import android.app.Application;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;
import ru.burningcourier.api.model.Order;
import ru.burningcourier.api.model.City;
import ru.burningcourier.utils.PreferencesManager;

public class SFApplication extends Application {
    
    public static final String PACKAGE = "ru.burningcourier";
    public static List<City> cities;
    public static String token;
    public static ArrayList<Order> orders = new ArrayList<>();
    public static int selectedOrder = -1;
    public static boolean userAuth = false;
    public static boolean geoLocation = false;
    public static Intent geoIntent = null;
    
    
    private SFServiceHelper serviceHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        serviceHelper = new SFServiceHelper(this);
        //TODO: отключить перед релизом
        PreferencesManager.getInstance(this).setLogin("1111");
        orders = Order.getMockOrders();
    }

    public SFServiceHelper getServiceHelper() {
        return serviceHelper;
    }
}
