package ru.burningcourier.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserProfile {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    @SerializedName("birth_day")
    private String birthDay;

    @SerializedName("sex")
    private String sex;

    @SerializedName("sms_subscription")
    private boolean smsSubscription;

    @SerializedName("email_subscription")
    private boolean emailSubscription;

    @SerializedName("email_confirmed")
    private String emailConfirmed;

    @SerializedName("phone_confirmed")
    private String phoneConfirmed;

    @SerializedName("vk_id")
    private String vkId;

    @SerializedName("fb_id")
    private String fbId;

    @SerializedName("ok_id")
    private String okId;

    @SerializedName("tw_id")
    private String twId;

    @SerializedName("has_franchise")
    private boolean hasFranchise;

    @SerializedName("phone_subscription")
    private boolean phoneSubscription;

    @SerializedName("favorites")
    private List<String> favorites = null;

    @SerializedName("order_count")
    private int orderCount;


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public String getSex() {
        return sex;
    }

    public boolean isSmsSubscription() {
        return smsSubscription;
    }

    public boolean isEmailSubscription() {
        return emailSubscription;
    }

    public String getEmailConfirmed() {
        return emailConfirmed;
    }

    public String getPhoneConfirmed() {
        return phoneConfirmed;
    }

    public String getVkId() {
        return vkId;
    }

    public String getFbId() {
        return fbId;
    }

    public String getOkId() {
        return okId;
    }

    public String getTwId() {
        return twId;
    }

    public boolean isHasFranchise() {
        return hasFranchise;
    }

    public boolean isPhoneSubscription() {
        return phoneSubscription;
    }

    public List<String> getFavorites() {
        return favorites;
    }

    public int getOrderCount() {
        return orderCount;
    }
}
