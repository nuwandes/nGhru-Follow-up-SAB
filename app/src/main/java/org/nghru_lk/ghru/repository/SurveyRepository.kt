package org.nghru_lk.ghru.repository

import androidx.lifecycle.LiveData
import org.nghru_lk.ghru.AppExecutors
import org.nghru_lk.ghru.api.ApiResponse
import org.nghru_lk.ghru.api.NghruService
import org.nghru_lk.ghru.vo.CommonResponce
import org.nghru_lk.ghru.vo.QuestionMeta
import org.nghru_lk.ghru.vo.Resource
import org.nghru_lk.ghru.vo.ResourceData
import org.nghru_lk.ghru.vo.request.ParticipantRequest
import org.nghru_lk.ghru.vo.request.ReportRequestMeta
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class SurveyRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService
) : Serializable {

    fun syncSurvey(
        questionMeta: QuestionMeta

    ): LiveData<Resource<ResourceData<CommonResponce>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<CommonResponce>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<CommonResponce>>> {
                return nghruService.addSurveySync(questionMeta)
            }
        }.asLiveData()
    }

    fun syncSurveyComplte(
        participant: ParticipantRequest, reportRequestMeta: ReportRequestMeta
    ): LiveData<Resource<ResourceData<CommonResponce>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<CommonResponce>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<CommonResponce>>> {
                return nghruService.addSurveyCompleteSync(participant.screeningId, reportRequestMeta)
            }
        }.asLiveData()
    }


}
