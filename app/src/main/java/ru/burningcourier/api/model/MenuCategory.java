package ru.burningcourier.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class MenuCategory implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("position")
    private int position;

    @SerializedName("type")
    private String type;

    @SerializedName("visible")
    private boolean visible;

    @SerializedName("text_id")
    private String textId;

    @SerializedName("images")
    private List<MenuImage> images = null;


    //TODO: убрать после того, как появивится хранилище и иконки на серве
    public MenuCategory(int menuCategoryId, int localResourceId, String title) {
        id = menuCategoryId;
        images = new ArrayList<>();
        images.add(new MenuImage(localResourceId));
        this.title = title;
    }

    public MenuCategory(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        position = in.readInt();
        type = in.readString();
        visible = in.readInt() == 1;
        textId = in.readString();
        in.readList(images, (MenuImage.class.getClassLoader()));
    }

    public final static Creator<MenuCategory> CREATOR = new Creator<MenuCategory>() {
        public MenuCategory createFromParcel(Parcel in) {
            return new MenuCategory(in);
        }

        public MenuCategory[] newArray(int size) {
            return (new MenuCategory[size]);
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(position);
        dest.writeString(type);
        dest.writeInt(visible ? 1 : 0);
        dest.writeString(textId);
        dest.writeList(images);
    }

    public int describeContents() {
        return 0;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }

    public boolean isVisible() {
        return visible;
    }

    public String getTextId() {
        return textId;
    }

    public List<MenuImage> getImages() {
        return images;
    }


    @Override
    public String toString() {
        return "id = " + String.valueOf(id) +
                ", title = " + title +
                ", position = " + String.valueOf(position) +
                ", type = " + type +
                ", visible = " + String.valueOf(visible) +
                ", textId = " + textId +
                ", images size = " + ((images == null) ? "null" : String.valueOf(images.size()));
    }
}
