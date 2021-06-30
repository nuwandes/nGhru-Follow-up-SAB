package org.southasia.ghrufollowup_sab.repository

import androidx.lifecycle.LiveData
import org.southasia.ghrufollowup_sab.AppExecutors
import org.southasia.ghrufollowup_sab.api.ApiResponse
import org.southasia.ghrufollowup_sab.api.NghruService
import org.southasia.ghrufollowup_sab.vo.BloodTestRequest
import org.southasia.ghrufollowup_sab.vo.*
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
