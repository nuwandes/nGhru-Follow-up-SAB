package org.nghru_pk.ghru.repository

import androidx.lifecycle.LiveData
import org.nghru_pk.ghru.AppExecutors
import org.nghru_pk.ghru.api.ApiResponse
import org.nghru_pk.ghru.api.NghruService
import org.nghru_pk.ghru.vo.*
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class CovidRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService
) : Serializable {


    fun updateCovid(
        cogRequest: CovidRequestNew,
        screening_id: String
    ): LiveData<Resource<ResourceData<Message>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.updateCovid(cogRequest,screening_id)
            }
        }.asLiveData()
    }

    fun syncCovidNew(
        cogRequest: CovidRequest,
        screening_id: String

    ): LiveData<Resource<ResourceData<Message>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.addCovidSync(screening_id, cogRequest)
            }
        }.asLiveData()
    }

}
