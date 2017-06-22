package ru.burningcourier.api.requestBody;

import com.google.gson.annotations.SerializedName;

public class UploadPhotoRequestBody {
    
    @SerializedName("order_id")
    private String orderId;
    
    @SerializedName("url")
    private String photoUrl;
    
    @SerializedName("check_summ")
    private int checkSumm;
    
    
    public UploadPhotoRequestBody(String orderId, String photoUrl, int checkSumm) {
        this.orderId = orderId;
        this.photoUrl = photoUrl;
        this.checkSumm = checkSumm;
    }
}
