package org.nghru_pk.ghru.repository

import androidx.lifecycle.LiveData
import com.birbit.android.jobqueue.JobManager
import org.nghru_pk.ghru.AppExecutors
import org.nghru_pk.ghru.api.ApiResponse
import org.nghru_pk.ghru.api.NghruService
import org.nghru_pk.ghru.db.BloodTestDao
import org.nghru_pk.ghru.db.MetaNewDao
import org.nghru_pk.ghru.jobs.SyncBloodTestJob
import org.nghru_pk.ghru.jobs.SyncECGJob
import org.nghru_pk.ghru.vo.BloodTestRequest
import org.nghru_pk.ghru.vo.*
import org.nghru_pk.ghru.vo.request.ParticipantRequest
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class BloodTestRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val bloodTestDao: BloodTestDao,
    private val jobManager: JobManager
) : Serializable {

//    fun syncBloodTest(
//        chkRequest: BloodTestRequest,
//        screening_id: String
//
//    ): LiveData<Resource<ResourceData<Message>>> {
//        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<Message>>(appExecutors) {
//            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
//                return nghruService.addBloodTestSync(screening_id, chkRequest)
//            }
//        }.asLiveData()
//    }

    fun syncBloodTestWhenBackToOnline(
        bloodRequest: BloodTestRequest
    ): LiveData<Resource<ResourceData<ECG>>> {
        return object : SyncNetworkOnlyBcakgroundBoundResource<ResourceData<ECG>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ECG>>> {
                return nghruService.addBloodTestSync(bloodRequest.screeningId, bloodRequest)
            }

            override fun deleteCall() {
                return bloodTestDao.deleteRequest(bloodRequest.id)
            }
        }.asLiveData()
    }

    fun syncBloodTest(
        bloodRequest: BloodTestRequest
    ): LiveData<Resource<ECG>> {
        return object : MyNetworkBoundResource<ECG,ResourceData<ECG>>(appExecutors) {

            override fun createJob(insertedID: Long) {
                bloodRequest.id = insertedID
                jobManager.addJobInBackground(

                    SyncBloodTestJob(bloodRequest)
                )
            }
            override fun isNetworkAvilable(): Boolean {
                return false
            }
            override fun saveDb(): Long {
                bloodRequest.syncPending = true
                return  bloodTestDao.insert(bloodRequest)
            }
            override fun createCall(): LiveData<ApiResponse<ResourceData<ECG>>> {
                bloodRequest.syncPending = false
                return nghruService.addBloodTestSync(bloodRequest.screeningId, bloodRequest)
            }
        }.asLiveData()
    }

    fun insertBloodTest(
        bloodTestRequest: BloodTestRequest
    ): LiveData<Resource<BloodTestRequest>> {
        return object : LocalBoundInsertResource<BloodTestRequest>(appExecutors) {
            override fun loadFromDb(rowId: Long): LiveData<BloodTestRequest> {
                return bloodTestDao.getBloodTestByScreeningId(bloodTestRequest!!.screeningId)
            }

            override fun insertDb(): Long {

                return bloodTestDao.insert(bloodTestRequest!!)
            }
        }.asLiveData()
    }

    fun getBloodTestListFromLocalDB(

    ): LiveData<Resource<List<BloodTestRequest>>> {
        return object : LocalBoundResource<List<BloodTestRequest>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<BloodTestRequest>> {
                return bloodTestDao.getECGStatusesSyncPending()
            }
        }.asLiveData()
    }
}
