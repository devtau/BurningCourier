package ru.burningcourier;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable {
    public int orderNum;
    public Date date;
    public String address;
    public String phone;
    public String note;
    public boolean delivered;
    public boolean selected;
    public long timer;
}
