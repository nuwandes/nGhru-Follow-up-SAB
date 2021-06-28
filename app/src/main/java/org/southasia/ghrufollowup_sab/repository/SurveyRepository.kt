package org.southasia.ghrufollowup_sab.repository

import androidx.lifecycle.LiveData
import org.southasia.ghrufollowup_sab.AppExecutors
import org.southasia.ghrufollowup_sab.api.ApiResponse
import org.southasia.ghrufollowup_sab.api.NghruService
import org.southasia.ghrufollowup_sab.vo.CommonResponce
import org.southasia.ghrufollowup_sab.vo.QuestionMeta
import org.southasia.ghrufollowup_sab.vo.Resource
import org.southasia.ghrufollowup_sab.vo.ResourceData
import org.southasia.ghrufollowup_sab.vo.request.ParticipantRequest
import org.southasia.ghrufollowup_sab.vo.request.ReportRequestMeta
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
