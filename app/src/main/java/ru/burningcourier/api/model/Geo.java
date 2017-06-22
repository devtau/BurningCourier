package ru.burningcourier.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

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
	
	
	public static List<Geo> getMockGeos() {
		List<Geo> geos = new ArrayList<>();
		geos.add(new Geo("20140924173211", 11.11, 22.22, 220));
		geos.add(new Geo("20140924173212", 11.12, 22.23, 224));
		return geos;
	}
	
	@Override
	public String toString() {
		return "date = " + date
				+ ", lat = " + lat
				+ ", lon = " + lon
				+ ", spd = " + spd;
	}
}
