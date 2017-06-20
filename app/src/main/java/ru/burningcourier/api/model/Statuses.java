package ru.burningcourier.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Statuses {
    
    @SerializedName("bampsi")
    private List<Status> bampsiStatuses;
    
    @SerializedName("sakura")
    private List<Status> sakuraStatuses;
    
    @Override
    public String toString() {
        return "bampsiStatuses.size() = " + (bampsiStatuses == null ? "null" : bampsiStatuses.size())
                + ", sakuraStatuses.size() = " + (sakuraStatuses == null ? "null" : sakuraStatuses.size());
    }
}
