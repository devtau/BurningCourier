package ru.burningcourier.sfClasses;

import android.app.Application;
import android.content.Intent;

import java.util.ArrayList;
import ru.burningcourier.Order;

public class SFApplication extends Application {
    
    public static final String PACKAGE = "ru.burningcourier";
    public static ArrayList<Order> orders = new ArrayList<>();
    public static int selectedOrder = -1;
    
    public static String CURRENT_LOGIN = null;
    public static int CURRENT_CITY = -1;
    public static boolean userAuth = false;
    public static boolean geoLocation = false;
    public static Intent geoIntent = null;
    
    
    private SFServiceHelper serviceHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        serviceHelper = new SFServiceHelper(this);
    }

    public SFServiceHelper getServiceHelper() {
        return serviceHelper;
    }

}
