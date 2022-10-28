package org.nghru_pk.ghru.vo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.nghru_pk.ghru.vo.request.HouseholdRequest;

public class HouseholdBodyData {
    @Expose
    @SerializedName("body")
    private
    HouseholdRequest household;

    public HouseholdRequest getHousehold() {
        return household;
    }
}
