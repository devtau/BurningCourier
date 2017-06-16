package ru.burningcourier.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class MenuImage implements Parcelable, Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("object_id")
    private int objectId;

    @SerializedName("filename")
    private String filename;
    //TODO: убрать после того, как появивится хранилище и иконки на серве
    private int localResourceId;

    @SerializedName("original_name")
    private String originalName;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("deleted_at")
    private String deletedAt;

    @SerializedName("section")
    private String section;


    private MenuImage(Parcel in) {
        id = in.readInt();
        objectId = in.readInt();
        filename = in.readString();
        originalName = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        deletedAt = in.readString();
        section = in.readString();
    }

    MenuImage(int localResourceId) {
        this.localResourceId = localResourceId;
    }

    public final static Creator<MenuImage> CREATOR = new Creator<MenuImage>() {
        public MenuImage createFromParcel(Parcel in) {
            return new MenuImage(in);
        }

        public MenuImage[] newArray(int size) {
            return (new MenuImage[size]);
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(objectId);
        dest.writeString(filename);
        dest.writeString(originalName);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(deletedAt);
        dest.writeString(section);
    }

    public int describeContents() {
        return 0;
    }

    public int getId() {
        return id;
    }

    public int getObjectId() {
        return objectId;
    }

    public String getFilename() {
        return filename;
    }

    public int getLocalResourceId() {
        return localResourceId;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public String getSection() {
        return section;
    }

    
    @Override
    public String toString() {
        return "id = " + String.valueOf(id) +
                ", objectId = " + String.valueOf(objectId) +
                ", filename = " + filename +
                ", localResourceId = " + String.valueOf(localResourceId) +
                ", originalName = " + originalName +
                ", createdAt = " + createdAt +
                ", updatedAt = " + updatedAt +
                ", deletedAt = " + deletedAt +
                ", section = " + section;
    }
}
