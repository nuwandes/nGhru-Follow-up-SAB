package org.nghru_ins.ghru.vo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.nghru_ins.ghru.vo.request.IntakeResponse

class IntakeData {

    @Expose
    @SerializedName("body")
    private val intake: IntakeResponse? = null

    fun getIntake(): IntakeResponse? {
        return intake
    }
}