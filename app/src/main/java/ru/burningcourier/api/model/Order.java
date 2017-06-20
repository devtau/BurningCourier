package ru.burningcourier.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import ru.burningcourier.utils.AppUtils;

public class Order implements Parcelable {
    
    private static final String LOG_TAG = "Order";
    
    
    @SerializedName("id")
    public String orderId;
    
    @SerializedName("date")
    public Date date;
    
    @SerializedName("address")
    public String address;
    
    @SerializedName("lat")
    public double addressLat;
    
    @SerializedName("lon")
    public double addressLon;
    
    @SerializedName("payment_type")
    public String paymentType;
    
    @SerializedName("note")
    public String note;
    
    @SerializedName("type")
    public String type;
    
    @SerializedName("curr_status")
    public String currStatus;
    
    @SerializedName("next_status")
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
        orderId = jsonObject.getString(Order.ORDER);
        date = AppUtils.formatDate(jsonObject.getString(Order.DATE));
        address = jsonObject.getString(Order.ADDRESS);
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
    private Order(String orderId, Date date, String address, String note, boolean delivered, double lat, double lon) {
        this.orderId = orderId;
        this.date = date;
        this.address = address;
        this.note = note;
        this.delivered = delivered;
        timer = date.getTime() - System.currentTimeMillis();
        this.addressLat = lat;
        this.addressLon = lon;
        Log.d(LOG_TAG, "created new Order: " + this);
    }
    
    private Order(Parcel in) {
        orderId = in.readString();
        date = (Date) in.readSerializable();
        address = in.readString();
        addressLat = in.readDouble();
        addressLon = in.readDouble();
        paymentType = in.readString();
        note = in.readString();
        type = in.readString();
        currStatus = in.readString();
        nextStatus = in.readString();
        delivered = in.readInt() == 1;
        selected = in.readInt() == 1;
        timer = in.readLong();
    }
    
    
    @Override
    public String toString() {
        return "orderId = " + orderId
                + ", date = " + date
                + ", address = " + address
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
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderId);
        dest.writeSerializable(date);
        dest.writeString(address);
        dest.writeDouble(addressLat);
        dest.writeDouble(addressLon);
        dest.writeString(paymentType);
        dest.writeString(note);
        dest.writeString(type);
        dest.writeString(currStatus);
        dest.writeString(nextStatus);
        dest.writeInt(delivered ? 1 : 0);
        dest.writeInt(selected ? 1 : 0);
        dest.writeLong(timer);
    }
    
    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
    
    public static ArrayList<Order> getMockOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order("SW-977-666-245-064", AppUtils.formatDate("20.06.2017 10:10:00"), "Санкт-Петербург, Шаумяна, 27-12",
                "встретить у подъезда, домофон 125", false, 59.931772, 30.415403));
        orders.add(new Order("SW-977-666-245-065", AppUtils.formatDate("20.06.2017 11:30:00"), "Санкт-Петербург, Энергетиков, 9-1-20",
                "", false, 59.937870, 30.435815));
        orders.add(new Order("SW-977-666-245-066", AppUtils.formatDate("20.06.2017 13:45:18"), "Санкт-Петербург, Среднеохтинский, 3-1-78",
                "не звонить. все равно не открою", false, 59.945026, 30.414169));
        orders.add(new Order("SW-977-666-245-067", AppUtils.formatDate("20.06.2017 19:15:18"), "Санкт-Петербург, Среднеохтинский, 3-1-78",
                "попрыгать на месте перед собакой", false, 59.945026, 30.414169));
        orders.add(new Order("SW-977-666-245-068", AppUtils.formatDate("20.06.2017 19:45:18"), "Санкт-Петербург, Среднеохтинский, 3-1-78",
                "кидать камни в окно пока не открою", false, 59.945026, 30.414169));
        return orders;
    }
}
