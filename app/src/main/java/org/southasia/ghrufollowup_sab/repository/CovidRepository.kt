package org.southasia.ghrufollowup_sab.repository

import androidx.lifecycle.LiveData
import com.birbit.android.jobqueue.JobManager
import com.google.gson.GsonBuilder
import com.nuvoair.sdk.launcher.NuvoairLauncherMeasurement
import org.southasia.ghrufollowup_sab.AppExecutors
import org.southasia.ghrufollowup_sab.api.ApiResponse
import org.southasia.ghrufollowup_sab.api.NghruService
import org.southasia.ghrufollowup_sab.db.*
import org.southasia.ghrufollowup_sab.jobs.SyncBodyMeasurementMetaJob
import org.southasia.ghrufollowup_sab.jobs.SyncSpirometryJob
import org.southasia.ghrufollowup_sab.vo.*
import org.southasia.ghrufollowup_sab.vo.request.BodyMeasurementMeta
import org.southasia.ghrufollowup_sab.vo.request.ParticipantRequest
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
