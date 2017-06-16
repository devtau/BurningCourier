package ru.burningcourier.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Tag implements Parcelable, Serializable {

	@SerializedName("tag_id")
	private int tagId;

	@SerializedName("title")
	private String title;
	
	private int productId;
	
	
	private Tag() { }
	

	public final static Creator<Tag> CREATOR = new Creator<Tag>() {
		public Tag createFromParcel(Parcel in) {
			Tag instance = new Tag();
			instance.tagId = in.readInt();
			instance.title = in.readString();
			instance.productId = in.readInt();
			return instance;
		}

		public Tag[] newArray(int size) {
			return (new Tag[size]);
		}
	};
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(tagId);
		dest.writeString(title);
		dest.writeInt(productId);
	}

	public int describeContents() {
		return 0;
	}

	@Override
	public int hashCode() {
		return 37 * tagId ^ 3;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Tag) {
			return tagId == ((Tag) obj).tagId;
		} else {
			return false;
		}
	}
	
	
	public int getTagId() {
		return tagId;
	}

	public String getTitle() {
		return title;
	}
	
	public int getProductId() {
		return productId;
	}
	
	public void setProductId(int productId) {
		this.productId = productId;
	}
	
	
	@Override
	public String toString() {
		return "id = " + String.valueOf(tagId)
				+ ", title = " + title
				+ ", productId = " + productId;
	}
}
