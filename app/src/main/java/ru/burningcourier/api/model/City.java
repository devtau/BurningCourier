package ru.burningcourier.api.model;

import com.google.gson.annotations.SerializedName;

public class City {

	@SerializedName("name")
	private String name;

	@SerializedName("url")
	private String url;
	
	
	public String getName() {
		return name;
	}
	
	public String getUrl() {
		return url;
	}
	
	@Override
	public String toString() {
		return "name = " + name + ", url = " + url;
	}
}
