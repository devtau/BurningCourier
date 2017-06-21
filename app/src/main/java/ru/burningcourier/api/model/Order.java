package ru.burningcourier.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
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
    
    @SerializedName("isCash")
    public boolean isCash;
    
    @SerializedName("note")
    public String note;
    
    @SerializedName("type")
    public String type;
    
    @SerializedName("curr_status")
    public int currStatus;
    
    @SerializedName("next_status")
    public int nextStatus;
    
    public boolean isDelivered;
    public boolean selected;
    public long timeLeft;
    
    
    //JSON fields
    private static final String ORDER = "order";
    private static final String DATE = "date";
    private static final String ADDRESS = "address";
    private static final String LAT = "lat";
    private static final String LON = "lon";
    private static final String PAYMENT_TYPE = "payment_type";
    private static final String NOTE = "note";
    private static final String TYPE = "type";
    private static final String CURR_STATUS = "curr_status";
    private static final String NEXT_STATUS = "next_status";
    
    
    public Order(JSONObject jsonObject) throws JSONException {
        //[{"order":8819818,"date":"13.06.2017 16:48:20","address":"Санкт-Петербург,Маршала Блюхера,47,53","phone":"89626920375"}]
        orderId = jsonObject.getString(ORDER);
        date = AppUtils.formatDate(jsonObject.getString(DATE));
        address = jsonObject.getString(ADDRESS);
        addressLat = jsonObject.getDouble(LAT);
        addressLon = jsonObject.getDouble(LON);
        isCash = jsonObject.getInt(PAYMENT_TYPE) == 1;
        note = jsonObject.getString(NOTE);
        type = jsonObject.getString(TYPE);
        currStatus = jsonObject.getInt(CURR_STATUS);
        nextStatus = jsonObject.getInt(NEXT_STATUS);
        
        isDelivered = false;
        timeLeft = date.getTime() - System.currentTimeMillis();
        Log.d(LOG_TAG, "created new Order: " + this);
    }
    
    //используется только для мока
    private Order(String orderId, Date date, String address, String note, boolean isDelivered, double lat, double lon) {
        this.orderId = orderId;
        this.date = date;
        this.address = address;
        this.note = note;
        this.isDelivered = isDelivered;
        timeLeft = date.getTime() - System.currentTimeMillis();
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
        isCash = in.readInt() == 1;
        note = in.readString();
        type = in.readString();
        currStatus = in.readInt();
        nextStatus = in.readInt();
        isDelivered = in.readInt() == 1;
        selected = in.readInt() == 1;
        timeLeft = in.readLong();
    }
    
    
    @Override
    public String toString() {
        return "orderId = " + orderId
                + ", date = " + date
                + ", address = " + address
                + ", addressLat = " + addressLat
                + ", addressLon = " + addressLon
                + ", isCash = " + isCash
                + ", note = " + note
                + ", type = " + type
                + ", currStatus = " + currStatus
                + ", nextStatus = " + nextStatus
                + ", isDelivered = " + isDelivered
                + ", selected = " + selected
                + ", timeLeft = " + timeLeft;
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
        dest.writeInt(isCash ? 1 : 0);
        dest.writeString(note);
        dest.writeString(type);
        dest.writeInt(currStatus);
        dest.writeInt(nextStatus);
        dest.writeInt(isDelivered ? 1 : 0);
        dest.writeInt(selected ? 1 : 0);
        dest.writeLong(timeLeft);
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
        String date = "20170621";
        orders.add(new Order("SW-977-666-245-064", AppUtils.formatDate(date + "060000"), "Санкт-Петербург, Шаумяна, 27-12",
                "встретить у подъезда, домофон 125", false, 59.931772, 30.415403));
        orders.add(new Order("SW-977-666-245-065", AppUtils.formatDate(date + "080000"), "Санкт-Петербург, Энергетиков, 9-1-20",
                "", false, 59.937870, 30.435815));
        orders.add(new Order("SW-977-666-245-066", AppUtils.formatDate(date + "194300"), "Санкт-Петербург, Среднеохтинский, 3-1-78",
                "не звонить. все равно не открою", false, 59.945026, 30.414169));
        orders.add(new Order("SW-977-666-245-067", AppUtils.formatDate(date + "204000"), "Санкт-Петербург, Среднеохтинский, 3-1-78",
                "попрыгать на месте перед собакой", false, 59.945026, 30.414169));
        orders.add(new Order("SW-977-666-245-068", AppUtils.formatDate(date + "235000"), "Санкт-Петербург, Среднеохтинский, 3-1-78",
                "кидать камни в окно пока не открою", false, 59.945026, 30.414169));
        return orders;
    }
}
