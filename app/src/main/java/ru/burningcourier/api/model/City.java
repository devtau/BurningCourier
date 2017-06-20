package ru.burningcourier.api.model;

import com.google.gson.annotations.SerializedName;

import ru.burningcourier.sfClasses.SFApplication;

public class City {

	@SerializedName("name")
	private String name;

	@SerializedName("url")
	private String url;
	
	@SerializedName("statuses")
	private Statuses statuses;

	@SerializedName("call_center_phone")
	private Long callCenterPhone;
	
	
	public String getName() {
		return name;
	}
	
	public String getUrl() {
		return url;
	}
	
	public static String getUrlByName(String name) {
		for (City city : SFApplication.cities) {
			if (city.getName().equals(name)) {
				return city.getUrl();
			}
		}
		return "";
	}
	
	
	@Override
	public String toString() {
		return "name = " + name
				+ ", url = " + url
				+ ", statuses = " + statuses
				+ ", callCenterPhone = " + callCenterPhone;
	}
}
