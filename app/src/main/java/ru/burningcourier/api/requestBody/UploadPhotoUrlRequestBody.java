package ru.burningcourier.api.requestBody;

import com.google.gson.annotations.SerializedName;

public class UploadPhotoUrlRequestBody {
    
    @SerializedName("order_id")
    private String orderId;
    
    @SerializedName("url")
    private String photoUrl;
    
    @SerializedName("check_summ")
    private int checkSumm;
    
    
    public UploadPhotoUrlRequestBody(String orderId, String photoUrl, int checkSumm) {
        this.orderId = orderId;
        this.photoUrl = photoUrl;
        this.checkSumm = checkSumm;
    }
}
