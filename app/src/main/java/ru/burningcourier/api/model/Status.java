package ru.burningcourier.api.model;

import com.google.gson.annotations.SerializedName;

public class Status {

	@SerializedName("id")
	private String id;

	@SerializedName("text_id")
	private String textId;
	
	
	@Override
	public String toString() {
		return "id = " + id + ", textId = " + textId;
	}
}
