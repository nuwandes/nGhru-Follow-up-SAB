package org.nghru_bd.ghru.repository

import androidx.lifecycle.LiveData
import org.nghru_bd.ghru.AppExecutors
import org.nghru_bd.ghru.api.ApiResponse
import org.nghru_bd.ghru.api.NghruService
import org.nghru_bd.ghru.vo.*
import org.nghru_bd.ghru.vo.request.IntakeRequestNew
import org.nghru_bd.ghru.vo.request.IntakeResponse
import java.io.Serializable
import javax.inject.Inject

class IntakeRepository  @Inject constructor (
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService

) : Serializable {

    fun addIntake(
        intakeRequest: IntakeRequestNew,
        screening_id: String
    ): LiveData<Resource<ResourceData<IntakeResponse>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<IntakeResponse>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<IntakeResponse>>> {
                return nghruService.postIntake(intakeRequest,screening_id)
            }
        }.asLiveData()
    }

    fun updateIntake(
        intakeRequest: IntakeRequestNew,
        screening_id: String
    ): LiveData<Resource<ResourceData<IntakeResponse>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<IntakeResponse>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<IntakeResponse>>> {
                return nghruService.updateIntake(intakeRequest,screening_id)
            }
        }.asLiveData()
    }
}