package org.nghru_hpv.ghru.vo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.nghru_hpv.ghru.vo.request.HouseholdRequest;

public class HouseholdBodyData {
    @Expose
    @SerializedName("body")
    private
    HouseholdRequest household;

    public HouseholdRequest getHousehold() {
        return household;
    }
}
