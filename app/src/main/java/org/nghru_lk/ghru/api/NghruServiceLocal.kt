package org.nghru_lk.ghru.api

import androidx.lifecycle.LiveData
import org.nghru_lk.ghru.vo.Devices
import retrofit2.http.GET
import retrofit2.http.Headers


/**
 * REST API access points
 */
interface NghruServiceLocal {

    @Headers(
        "Accept: text/plain",
        "Content-type:application/json"
    )

    @GET("devices")
    fun getDevices(): LiveData<ApiResponse<Devices>>

}
