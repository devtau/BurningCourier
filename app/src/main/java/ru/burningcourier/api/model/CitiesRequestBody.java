package ru.burningcourier.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CitiesRequestBody {

	private List<City> cities;

	@SerializedName("statuses")
	private String statuses;

	@SerializedName("call_center_phone")
	private String callCenterPhone;
	
//	@Override
//	public String toString() {
//		return "name = " + name + ", url = " + url;
//	}
}
