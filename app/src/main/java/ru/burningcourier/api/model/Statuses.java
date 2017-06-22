package ru.burningcourier.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Statuses {
    
    @SerializedName("bampsi")
    private List<Status> bampsiStatuses;
    
    @SerializedName("sakura")
    private List<Status> sakuraStatuses;
    
    
    public List<Status> getBampsiStatuses() {
        return bampsiStatuses;
    }
    
    public List<Status> getSakuraStatuses() {
        return sakuraStatuses;
    }
    
    
    @Override
    public String toString() {
        return "bampsiStatuses.size() = " + (bampsiStatuses == null ? "null" : bampsiStatuses.size())
                + ", sakuraStatuses.size() = " + (sakuraStatuses == null ? "null" : sakuraStatuses.size());
    }
}
