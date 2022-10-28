package org.nghru_hpv.ghru.repository

import androidx.lifecycle.LiveData
import org.nghru_hpv.ghru.AppExecutors
import org.nghru_hpv.ghru.api.ApiResponse
import org.nghru_hpv.ghru.api.NghruServiceLocal
import org.nghru_hpv.ghru.vo.Devices
import org.nghru_hpv.ghru.vo.Resource
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class DeviceRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruServiceLocal: NghruServiceLocal
) {

    fun getDevices(
    ): LiveData<Resource<Devices>> {
        return object : NetworkOnlyBoundResource<Devices>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<Devices>> {
                return nghruServiceLocal.getDevices();
            }
        }.asLiveData()
    }

}
