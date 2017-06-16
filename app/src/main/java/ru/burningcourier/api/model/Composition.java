package ru.burningcourier.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

class Composition implements Parcelable, Serializable {

	@SerializedName("composition_id")
	private int compositionId;

	@SerializedName("title")
	private String title;


	public final static Creator<Composition> CREATOR = new Creator<Composition>() {
		public Composition createFromParcel(Parcel in) {
			Composition instance = new Composition();
			instance.compositionId = in.readInt();
			instance.title = in.readString();
			return instance;
		}

		public Composition[] newArray(int size) {
			return (new Composition[size]);
		}
	};

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(compositionId);
		dest.writeString(title);
	}

	public int describeContents() {
		return 0;
	}

	public int getCompositionId() {
		return compositionId;
	}

	public String getTitle() {
		return title;
	}
}
