package ru.burningcourier.api.model;

import com.google.gson.annotations.SerializedName;

public class Status {

	@SerializedName("id")
	private int id;

	@SerializedName("text_id")
	private String textId;
	
	
	public int getId() {
		return id;
	}
	
	public String getTextId() {
		return textId;
	}
	
	
	@Override
	public String toString() {
		return "id = " + id + ", textId = " + textId;
	}
}
