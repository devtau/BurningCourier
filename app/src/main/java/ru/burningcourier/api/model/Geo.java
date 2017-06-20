package ru.burningcourier.api.model;

import com.google.gson.annotations.SerializedName;

public class Geo {

	@SerializedName("date")
	private String date;

	@SerializedName("lat")
	private double lat;
	
	@SerializedName("lon")
	private double lon;

	@SerializedName("spd")
	private double spd;
	
	
	public Geo(String date, double lat, double lon, double spd) {
		this.date = date;
		this.lat = lat;
		this.lon = lon;
		this.spd = spd;
	}
	
	
	@Override
	public String toString() {
		return "date = " + date
				+ ", lat = " + lat
				+ ", lon = " + lon
				+ ", spd = " + spd;
	}
}
