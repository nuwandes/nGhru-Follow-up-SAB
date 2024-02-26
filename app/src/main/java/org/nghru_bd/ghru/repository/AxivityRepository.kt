package org.nghru_bd.ghru.repository

import androidx.lifecycle.LiveData
import com.birbit.android.jobqueue.JobManager
import org.nghru_bd.ghru.AppExecutors
import org.nghru_bd.ghru.api.ApiResponse
import org.nghru_bd.ghru.api.NghruService
import org.nghru_bd.ghru.db.AxivityDao
import org.nghru_bd.ghru.jobs.SyncAxivityJob
import org.nghru_bd.ghru.vo.*
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class AxivityRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val jobManager: JobManager,
    private val axivityDao: AxivityDao
    ) : Serializable {

    fun syncAxivity(
        screeningId: String, axivity: Axivity?
    ): LiveData<Resource<Message>> {
        return object : MyNetworkBoundResource<Message,ResourceData<Message>>(appExecutors) {

            override fun createJob(insertedID: Long) {
                axivity?.id = insertedID
                jobManager.addJobInBackground(SyncAxivityJob(screeningId, axivity!!))
            }
            override fun isNetworkAvilable(): Boolean {

                return axivity?.syncPending!!
            }
            override fun saveDb(): Long {

                return axivityDao.insert(axivity!!)
            }
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                // val mECGStatus = ECGStatus(status, comment)
                return nghruService.addAxivity(screeningId, axivity!!)
            }

        }.asLiveData()
    }

    fun getAxivityListFromLocalDB(

    ): LiveData<Resource<List<Axivity>>> {
        return object : LocalBoundResource<List<Axivity>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<Axivity>> {
                return axivityDao.getAxivityRequestSyncPending()
            }
        }.asLiveData()
    }
    fun syncAxivityRequest(
        axivity: Axivity?,
        screeningId : String
    ): LiveData<Resource<ResourceData<Message>>>  {
        return object : SyncNetworkOnlyBcakgroundBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>>  {
                return nghruService.addAxivity(screeningId, axivity!!)
            }

            override fun deleteCall() {

                axivityDao.deleteRequest(axivity?.id!!)
            }
        }.asLiveData()
    }

    fun insertAxivity(
        axivity: Axivity?
    ): LiveData<Resource<Axivity>> {
        return object : LocalBoundInsertResource<Axivity>(appExecutors) {
            override fun loadFromDb(rowId: Long): LiveData<Axivity> {
                return axivityDao.getAxivityByScreeningId(axivity?.screeningId!!)
            }

            override fun insertDb(): Long {

                return axivityDao.insert(axivity!!)
            }
        }.asLiveData()
    }
}
