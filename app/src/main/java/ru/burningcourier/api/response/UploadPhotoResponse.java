package ru.burningcourier.api.response;

import com.google.gson.annotations.SerializedName;

public class UploadPhotoResponse {

    @SerializedName("check_photo")
    private NestedResponse checkPhoto;
    
    
    public String getFilename() {
        return checkPhoto.getFilename();
    }
    
    
    
    private class NestedResponse {
        @SerializedName("status")
        private String status;
    
        @SerializedName("filename")
        private String filename;
        
    
        public String getFilename() {
            return filename;
        }
    }
}
