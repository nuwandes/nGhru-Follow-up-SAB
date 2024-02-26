package org.nghru_bd.ghru.repository

import androidx.lifecycle.LiveData
import com.birbit.android.jobqueue.JobManager
import org.nghru_bd.ghru.AppExecutors
import org.nghru_bd.ghru.api.ApiResponse
import org.nghru_bd.ghru.api.NghruService
import org.nghru_bd.ghru.db.FundoscopyRequestDao
import org.nghru_bd.ghru.db.MetaNewDao
import org.nghru_bd.ghru.jobs.SyncFundoscopyJob
import org.nghru_bd.ghru.vo.*
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class FundoscopyRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val jobManager: JobManager,
    private val metaNewDao: MetaNewDao,
    private val fundoscopyRequestDao: FundoscopyRequestDao
    ) : Serializable {

    fun syncFundoscopy(
        screeningId: String?,
        fundoscopyRequest: FundoscopyRequest?
    ): LiveData<Resource<ECG>> {
        return object : MyNetworkBoundResource<ECG,ResourceData<ECG>>(appExecutors) {

            override fun createJob(insertedID: Long) {

                jobManager.addJobInBackground(
                    SyncFundoscopyJob(
                        screeningId,
                        fundoscopyRequest!!
                    )
                )
            }
            override fun isNetworkAvilable(): Boolean {
                return true
            }
            override fun saveDb(): Long {

                //var ecgMetaNewId = metaNewDao.insert(participantRequest?.meta!!)

                fundoscopyRequest!!.syncPending = false
                var id =  fundoscopyRequestDao.insert(fundoscopyRequest)
                return id
            }
            override fun createCall(): LiveData<ApiResponse<ResourceData<ECG>>> {
                return nghruService.addFundoscopyGSync(
                    screeningId!!,
                    fundoscopyRequest
                )
            }
        }.asLiveData()

    }

    fun getFundoscopyRequestFromLocalDB(

    ): LiveData<Resource<List<FundoscopyRequest>>> {
        return object : LocalBoundResource<List<FundoscopyRequest>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<FundoscopyRequest>> {
                return fundoscopyRequestDao.getFundoscopyRequestSyncPending()
            }
        }.asLiveData()
    }

    fun syncFundoscopyRequest(
        fundoscopyRequest: FundoscopyRequest
    ): LiveData<Resource<ResourceData<ECG>>> {
        return object : SyncNetworkOnlyBcakgroundBoundResource<ResourceData<ECG>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ECG>>> {
                return nghruService.addFundoscopyGSync(fundoscopyRequest?.screeningId,fundoscopyRequest)
            }

            override fun deleteCall() {

                fundoscopyRequestDao.deleteRequest(fundoscopyRequest.id)
            }
        }.asLiveData()
    }

    fun insertFundo(
        fundoscopyRequest: FundoscopyRequest
    ): LiveData<Resource<FundoscopyRequest>> {
        return object : LocalBoundInsertResource<FundoscopyRequest>(appExecutors) {
            override fun loadFromDb(rowId: Long): LiveData<FundoscopyRequest> {
                return fundoscopyRequestDao.getFundoByScreeningId(fundoscopyRequest.screeningId)
            }

            override fun insertDb(): Long {

                return fundoscopyRequestDao.insert(fundoscopyRequest)
            }
        }.asLiveData()
    }
}
