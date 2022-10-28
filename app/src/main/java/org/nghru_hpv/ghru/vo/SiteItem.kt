package org.nghru_hpv.ghru.vo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SiteItem {
    @Expose
    @SerializedName("data")
    var data: Array<String>? = null
}