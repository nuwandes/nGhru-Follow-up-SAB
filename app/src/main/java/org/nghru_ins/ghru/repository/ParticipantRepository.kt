package org.nghru_ins.ghru.repository

import androidx.lifecycle.LiveData
import org.nghru_ins.ghru.AppExecutors
import org.nghru_ins.ghru.api.ApiResponse
import org.nghru_ins.ghru.api.NghruService
import org.nghru_ins.ghru.db.ParticipantRequestDao
import org.nghru_ins.ghru.vo.Participant
import org.nghru_ins.ghru.vo.Resource
import org.nghru_ins.ghru.vo.ResourceData
import org.nghru_ins.ghru.vo.request.ParticipantRequest
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class ParticipantRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val participantRequestDao: ParticipantRequestDao
) : Serializable {
    fun getParticipant(
        screeningId: String
    ): LiveData<Resource<ResourceData<Participant>>> {
        return object : NetworkOnlyBoundResource<ResourceData<Participant>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Participant>>> {
                return nghruService.getParticipant(screeningId)
            }
        }.asLiveData()
    }

    fun getParticipantRequest(
        screeningId: String, station: String
    ): LiveData<Resource<ResourceData<ParticipantRequest>>> {
        return object : NetworkOnlyBoundResource<ResourceData<ParticipantRequest>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ParticipantRequest>>> {
                return nghruService.getParticipantRequest(screeningId, station)
            }
        }.asLiveData()
    }


    fun getParticipantOffline(
        screeningId: String
    ): LiveData<Resource<ParticipantRequest>> {
        return object : LocalBoundResource<ParticipantRequest>(appExecutors) {
            override fun loadFromDb(): LiveData<ParticipantRequest> {
                return participantRequestDao.getParticipantRequestByScreenId(screeningId)
            }
        }.asLiveData()
    }

    fun getParticipantByIdnumber(
        idNumber: String
    ): LiveData<Resource<ParticipantRequest>> {
        return object : LocalBoundResource<ParticipantRequest>(appExecutors) {
            override fun loadFromDb(): LiveData<ParticipantRequest> {
                return participantRequestDao.getParticipantByIdnumber(idNumber)
            }
        }.asLiveData()
    }

    fun getItemId(
        screeningId: String
    ): LiveData<Resource<ParticipantRequest>> {
        return object : NetworkBoundResource<ParticipantRequest, ResourceData<ParticipantRequest>>(appExecutors) {
            override fun saveCallResult(item: ResourceData<ParticipantRequest>) {
                participantRequestDao.insert(item.data!!)
            }


            override fun shouldFetch(data: ParticipantRequest?): Boolean = data == null

            override fun loadFromDb(): LiveData<ParticipantRequest> {
                return participantRequestDao.getParticipantRequestByScreenId(screeningId)
            }

            override fun createCall(): LiveData<ApiResponse<ResourceData<ParticipantRequest>>> {
                return nghruService.getParticipantRequest(screeningId)
            }

        }.asLiveData()
    }

}
