package ru.burningcourier.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Product implements Parcelable, Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("menu_id")
    private int menuId;

    @SerializedName("title")
    private String title;

    @SerializedName("weight")
    private int weight;

    @SerializedName("type")
    private String type;

    @SerializedName("is_delivery")
    private boolean isDelivery;

    @SerializedName("tags")
    private List<Tag> tags = null;

    @SerializedName("composition")
    private List<Composition> composition = null;

    @SerializedName("toppings")
    private List<Integer> toppings = null;

    @SerializedName("images")
    private List<MenuImage> images = null;

    @SerializedName("price")
    private int price;

    @SerializedName("price_old")
    private int priceOld;

    @SerializedName("is_action")
    private boolean isAction;
    
    private int countInCart;
    
    
    private Product() {}


    public final static Creator<Product> CREATOR = new Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            Product instance = new Product();
            instance.id = in.readInt();
            instance.menuId = in.readInt();
            instance.title = in.readString();
            instance.weight = in.readInt();
            instance.type = in.readString();
            instance.isDelivery = in.readInt() == 1;
            if (instance.tags != null) {
                in.readList(instance.tags, (Tag.class.getClassLoader()));
            }
            if (instance.composition != null) {
                in.readList(instance.composition, (Composition.class.getClassLoader()));
            }
            if (instance.toppings != null) {
                in.readList(instance.toppings, (Integer.class.getClassLoader()));
            }
            if (instance.images != null) {
                in.readList(instance.images, (MenuImage.class.getClassLoader()));
            }
            instance.price = in.readInt();
            instance.priceOld = in.readInt();
            instance.isAction = in.readInt() == 1;
            instance.countInCart = in.readInt();
            return instance;
        }

        public Product[] newArray(int size) {
            return (new Product[size]);
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(menuId);
        dest.writeString(title);
        dest.writeInt(weight);
        dest.writeString(type);
        dest.writeInt(isDelivery ? 1 : 0);
        dest.writeList(tags);
        dest.writeList(composition);
        dest.writeList(toppings);
        dest.writeList(images);
        dest.writeInt(price);
        dest.writeInt(priceOld);
        dest.writeInt(isAction ? 1 : 0);
        dest.writeInt(countInCart);
    }

    public int describeContents() {
        return 0;
    }


    public int getId() {
        return id;
    }

    public int getMenuId() {
        return menuId;
    }

    public String getTitle() {
        return title;
    }

    public int getWeight() {
        return weight;
    }

    public String getType() {
        return type;
    }

    public boolean isDelivery() {
        return isDelivery;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public List<Composition> getComposition() {
        return composition;
    }

    public List<Integer> getToppings() {
        return toppings;
    }

    public List<MenuImage> getImages() {
        return images;
    }

    public int getPrice() {
        return price;
    }

    public int getPriceOld() {
        return priceOld;
    }

    public boolean isAction() {
        return isAction;
    }

    public boolean isVegetarian() {
        if (tags == null || tags.size() == 0) return false;
        for (Tag nextTag : tags) {
            if (nextTag.getTagId() == 6) {
                return true;
            }
        }
        return false;
    }

    public boolean isSpicy() {
        if (tags == null || tags.size() == 0) return false;
        for (Tag nextTag : tags) {
            if (nextTag.getTagId() == 1) {
                return true;
            }
        }
        return false;
    }
    
    public int getCountInCart() {
        return countInCart;
    }
    
    public void changeCountInCart(int count) {
        countInCart += count;
    }
    
    public void setCountInCart(int countInCart) {
        this.countInCart = countInCart;
    }
    
    
    @Override
    public String toString() {
        return "id = " + String.valueOf(id) +
                ", menuId = " + menuId +
                ", title = " + title +
                ", weight = " + weight +
                ", type = " + type +
                ", isDelivery = " + isDelivery +
                ", tags = " + ((tags == null) ? "null" : String.valueOf(tags.size())) +
                ", composition = " + ((composition == null) ? "null" : String.valueOf(composition.size())) +
                ", toppings = " + ((toppings == null) ? "null" : String.valueOf(toppings.size())) +
                ", images = " + ((images == null) ? "null" : String.valueOf(images.size())) +
                ", price = " + price +
                ", priceOld = " + priceOld +
                ", isAction = " + isAction +
                ", countInCart = " + countInCart;
    }
}
