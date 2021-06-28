package org.southasia.ghrufollowup_sab.vo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.southasia.ghrufollowup_sab.vo.request.IntakeResponse

class IntakeData {

    @Expose
    @SerializedName("body")
    private val intake: IntakeResponse? = null

    fun getIntake(): IntakeResponse? {
        return intake
    }
}