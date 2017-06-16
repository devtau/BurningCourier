package ru.burningcourier;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import ru.burningcourier.utils.AppUtils;

public class Order implements Serializable {
    
    private static final String LOG_TAG = "Order";
    
    public int orderNum;
    public Date date;
    public String address;
    public String phone;
    
    public double addressLat;
    public double addressLon;
    public String paymentType;
    public String note;
    public String type;
    public String currStatus;
    public String nextStatus;
    
    public boolean delivered;
    public boolean selected;
    public long timer;
    
    
    //JSON fields
    private static final String ORDER = "order";
    private static final String DATE = "date";
    private static final String ADDRESS = "address";
    private static final String PHONE = "phone";
    private static final String LAT = "lat";
    private static final String LON = "lon";
    private static final String PAYMENT_TYPE = "payment_type";
    private static final String NOTE = "note";
    private static final String TYPE = "type";
    private static final String CURR_STATUS = "curr_status";
    private static final String NEXT_STATUS = "next_status";
    
    
    public Order(JSONObject jsonObject) throws JSONException {
        //[{"order":8819818,"date":"13.06.2017 16:48:20","address":"Санкт-Петербург,Маршала Блюхера,47,53","phone":"89626920375"}]
        orderNum = jsonObject.getInt(Order.ORDER);
        date = AppUtils.formatDate(jsonObject.getString(Order.DATE));
        address = jsonObject.getString(Order.ADDRESS);
        phone = jsonObject.getString(Order.PHONE);
        
        addressLat = jsonObject.getDouble(Order.LAT);
        addressLon = jsonObject.getDouble(Order.LON);
        paymentType = jsonObject.getString(Order.PAYMENT_TYPE);
        note = jsonObject.getString(Order.NOTE);
        type = jsonObject.getString(Order.TYPE);
        currStatus = jsonObject.getString(Order.CURR_STATUS);
        nextStatus = jsonObject.getString(Order.NEXT_STATUS);
        
        delivered = false;
        timer = date.getTime() - System.currentTimeMillis();
        Log.d(LOG_TAG, "created new Order: " + this);
    }
    
    //используется только для мока
    private Order(int orderNum, Date date, String address, String note, boolean delivered, double lat, double lon) {
        this.orderNum = orderNum;
        this.date = date;
        this.address = address;
        this.note = note;
        this.delivered = delivered;
        timer = date.getTime() - System.currentTimeMillis();
        this.addressLat = lat;
        this.addressLon = lon;
        Log.d(LOG_TAG, "created new Order: " + this);
    }
    
    @Override
    public String toString() {
        return "orderNum = " + orderNum
                + ", date = " + date
                + ", address = " + address
                + ", phone = " + phone
                + ", addressLat = " + addressLat
                + ", addressLon = " + addressLon
                + ", paymentType = " + paymentType
                + ", note = " + note
                + ", type = " + type
                + ", currStatus = " + currStatus
                + ", nextStatus = " + nextStatus
                + ", delivered = " + delivered
                + ", selected = " + selected
                + ", timer = " + timer;
    }
    
    public static ArrayList<Order> getMockOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order(85231, AppUtils.formatDate("16.06.2017 10:10:00"), "Санкт-Петербург, Шаумяна, 27-12",
                "встретить у подъезда, домофон 125", false, 59.931772, 30.415403));
        orders.add(new Order(85232, AppUtils.formatDate("16.06.2017 11:30:00"), "Санкт-Петербург, Энергетиков, 9-1-20",
                "", false, 59.937870, 30.435815));
        orders.add(new Order(85233, AppUtils.formatDate("16.06.2017 13:45:18"), "Санкт-Петербург, Среднеохтинский, 3-1-78",
                "не звонить. все равно не открою", false, 59.945026, 30.414169));
        orders.add(new Order(85234, AppUtils.formatDate("16.06.2017 19:15:18"), "Санкт-Петербург, Среднеохтинский, 3-1-78",
                "попрыгать на месте перед собакой", false, 59.945026, 30.414169));
        orders.add(new Order(85235, AppUtils.formatDate("16.06.2017 19:45:18"), "Санкт-Петербург, Среднеохтинский, 3-1-78",
                "кидать камни в окно пока не открою", false, 59.945026, 30.414169));
        return orders;
    }
}
