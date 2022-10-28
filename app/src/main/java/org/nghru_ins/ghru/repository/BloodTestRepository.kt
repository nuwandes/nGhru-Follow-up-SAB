package org.nghru_ins.ghru.repository

import androidx.lifecycle.LiveData
import org.nghru_ins.ghru.AppExecutors
import org.nghru_ins.ghru.api.ApiResponse
import org.nghru_ins.ghru.api.NghruService
import org.nghru_ins.ghru.vo.BloodTestRequest
import org.nghru_ins.ghru.vo.*
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class BloodTestRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService
) : Serializable {

    fun syncBloodTest(
        chkRequest: BloodTestRequest,
        screening_id: String

    ): LiveData<Resource<ResourceData<Message>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.addBloodTest(screening_id, chkRequest)
            }
        }.asLiveData()
    }
}