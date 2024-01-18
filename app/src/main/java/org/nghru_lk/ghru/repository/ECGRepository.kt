package org.nghru_lk.ghru.repository

import androidx.lifecycle.LiveData
import com.birbit.android.jobqueue.JobManager
import org.nghru_lk.ghru.AppExecutors
import org.nghru_lk.ghru.api.ApiResponse
import org.nghru_lk.ghru.api.NghruService
import org.nghru_lk.ghru.db.ECGStatusDao
import org.nghru_lk.ghru.db.MetaNewDao
import org.nghru_lk.ghru.jobs.SyncECGJob
import org.nghru_lk.ghru.vo.*
import org.nghru_lk.ghru.vo.request.ParticipantRequest
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class ECGRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val jobManager: JobManager,
    private val metaNewDao: MetaNewDao,
    private val ecgStatusDao: ECGStatusDao
) : Serializable {

    fun syncECG(
        screeningId : String?,
        ecgStatus: ECGStatus?,
        isOnline : Boolean
    ): LiveData<Resource<ECG>> {
        return object : MyNetworkBoundResource<ECG,ResourceData<ECG>>(appExecutors) {

            override fun createJob(insertedID: Long) {
                ecgStatus?.syncPending = !isOnline
                ecgStatus?.id = insertedID
                jobManager.addJobInBackground(

                    SyncECGJob(screeningId, ecgStatus!!)
                )
            }
            override fun isNetworkAvilable(): Boolean {

                return !isOnline
            }
            override fun saveDb(): Long {
                ecgStatus?.syncPending = !isOnline
                var ecgMetaNewId = metaNewDao.insert(ecgStatus?.meta!!)
                ecgStatus.metaId = ecgMetaNewId
                ecgStatus.screeningId = screeningId!!
                return  ecgStatusDao.insert(ecgStatus)
            }
            override fun createCall(): LiveData<ApiResponse<ResourceData<ECG>>> {
                ecgStatus?.syncPending = !isOnline
                return nghruService.addECGSync(screeningId!!, ecgStatus!!)
            }
        }.asLiveData()
    }

//    fun getECGRequestFromLocalDB(
//
//    ): MutableLiveData<MutableList<ECGStatus>> {
//        return object : MyLocalBoundResource<MutableList<ECGStatus>>(appExecutors) {
//            override fun loadFromDb(): MutableLiveData<MutableList<ECGStatus>> {
//
//                var request = MutableLiveData<MutableList<ECGStatus>>()
//                var requestList : MutableList<ECGStatus> = ArrayList()
//                var ecgRequestList : List<ECGStatus> = ecgStatusDao.getECGStatusesSyncPending()
//                for(ecg in ecgRequestList)
//                {
//                    var ecgStatus : ECGStatus = ECGStatus(ecg.traceStatus,ecg.comment,ecg.device_id, metaNewDao.getNewMeta(ecg.metaId))
//                    ecgStatus.screeningId = ecg.screeningId
//                    requestList.add(ecgStatus)
//
//                }
//                request.postValue(requestList)
//                return request
//            }
//        }.asLiveData()
//    }

    fun getECGRequestFromLocalDB(

    ): LiveData<Resource<List<ECGStatus>>> {
        return object : LocalBoundResource<List<ECGStatus>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<ECGStatus>> {
                return ecgStatusDao.getECGStatusesSyncPending()
            }
        }.asLiveData()
    }

    fun syncECGStatus(
        mECGStatus:ECGStatus
    ): LiveData<Resource<ResourceData<ECG>>> {
        return object : SyncNetworkOnlyBcakgroundBoundResource<ResourceData<ECG>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ECG>>> {
                return nghruService.addECGSync(mECGStatus.screeningId, mECGStatus)
            }

            override fun deleteCall() {
                return ecgStatusDao.deleteRequest(mECGStatus.id)
            }
        }.asLiveData()
    }

    fun insertEcg(
        ecgStatus: ECGStatus?
    ): LiveData<Resource<ECGStatus>> {
        return object : LocalBoundInsertResource<ECGStatus>(appExecutors) {
            override fun loadFromDb(rowId: Long): LiveData<ECGStatus> {
                return ecgStatusDao.getEcgByScreeningId(ecgStatus!!.screeningId)
            }

            override fun insertDb(): Long {

                return ecgStatusDao.insert(ecgStatus!!)
            }
        }.asLiveData()
    }
}
