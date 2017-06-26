package ru.burningcourier.api.model;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.annotations.SerializedName;
import ru.burningcourier.BCApplication;
import ru.burningcourier.utils.PreferencesManager;

public class City {

	private static final String LOG_TAG = "City";
	
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
	
	public Statuses getStatuses() {
		return statuses;
	}
	
	public Long getCallCenterPhone() {
		return callCenterPhone;
	}
	
	
	@Nullable
	public static City getCityByName(Context context) {
		String savedCityName = PreferencesManager.getInstance(context).getCurrentCity();
		for (City city : BCApplication.cities) {
			if (city.getName().equals(savedCityName)) {
				if (TextUtils.isEmpty(city.getUrl())
						|| city.getStatuses() == null || (city.getStatuses().getBampsiStatuses() == null && city.getStatuses().getSakuraStatuses() == null)
						|| city.getCallCenterPhone() == null || city.getCallCenterPhone() == 0) {
					String errorMsg = "непредвиденное состояние. у сохраненного города не заполнены url, statuses или callCenterPhone";
					Log.e(LOG_TAG, errorMsg);
					Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
					return null;
				} else {
					return city;
				}
			}
		}
		String errorMsg = "непредвиденное состояние. нет сохраненного города с таким именем";
		Log.e(LOG_TAG, errorMsg);
		Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
		return null;
	}
	
	
	@Override
	public String toString() {
		return "name = " + name
				+ ", url = " + url
				+ ", statuses = " + statuses
				+ ", callCenterPhone = " + callCenterPhone;
	}
}
