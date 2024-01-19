package org.nghru_lk.ghru.repository

import androidx.lifecycle.LiveData
import com.birbit.android.jobqueue.JobManager
import org.nghru_lk.ghru.AppExecutors
import org.nghru_lk.ghru.api.ApiResponse
import org.nghru_lk.ghru.api.NghruService
import org.nghru_lk.ghru.db.BodyMeasurementMetaDao
import org.nghru_lk.ghru.jobs.SyncBodyMeasurementMetaJob
import org.nghru_lk.ghru.vo.Message
import org.nghru_lk.ghru.vo.Resource
import org.nghru_lk.ghru.vo.ResourceData
import org.nghru_lk.ghru.vo.request.BodyMeasurementMeta
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class BodyMeasurementMetaRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val bodyMeasurementMetaDao: BodyMeasurementMetaDao,
    private val nghruService: NghruService,
    private val jobManager: JobManager
) : Serializable {

    fun bodyMeasurementMeta(
        bodyMeasurementMeta: BodyMeasurementMeta
    ): LiveData<Resource<BodyMeasurementMeta>> {
        return object : MyNetworkBoundResource<BodyMeasurementMeta, ResourceData<Message>>(appExecutors) {
            override fun createJob(insertedID: Long) {
                bodyMeasurementMeta.id = insertedID
                jobManager.addJobInBackground(

                    SyncBodyMeasurementMetaJob(
                        bodyMeasurementMeta = bodyMeasurementMeta,
                        screeningId = bodyMeasurementMeta.screeningId
                    )
                )
            }

            override fun isNetworkAvilable(): Boolean {
                return bodyMeasurementMeta.syncPending
            }

            override fun saveDb(): Long {
                return bodyMeasurementMetaDao.insert(bodyMeasurementMeta)
            }


            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.addBodyMeasurementMeta(bodyMeasurementMeta.screeningId, bodyMeasurementMeta)
            }
        }.asLiveData()
    }

    fun getBodyMeasurementMetaListFromLocalDB(

    ): LiveData<Resource<List<BodyMeasurementMeta>>> {
        return object : LocalBoundResource<List<BodyMeasurementMeta>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<BodyMeasurementMeta>> {
                return bodyMeasurementMetaDao.getBodyMeasurementMetasSyncPending()
            }
        }.asLiveData()
    }

    fun syncBodyMeasurementMeta(
        bodyMeasurementMeta: BodyMeasurementMeta,
        screeningID : String
    ): LiveData<Resource<ResourceData<Message>>> {
        return object : SyncNetworkOnlyBcakgroundBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return  nghruService.addBodyMeasurementMeta(screeningID,bodyMeasurementMeta)
            }

            override fun deleteCall() {
                bodyMeasurementMetaDao.deleteRequest(bodyMeasurementMeta.id)
            }
        }.asLiveData()
    }

    fun insertBMMeta(
        bodyMeasurementMeta: BodyMeasurementMeta
    ): LiveData<Resource<BodyMeasurementMeta>> {
        return object : LocalBoundInsertResource<BodyMeasurementMeta>(appExecutors) {
            override fun loadFromDb(rowId: Long): LiveData<BodyMeasurementMeta> {
                return bodyMeasurementMetaDao.getBMByScreeningId(bodyMeasurementMeta.screeningId)
            }

            override fun insertDb(): Long {

                return bodyMeasurementMetaDao.insert(bodyMeasurementMeta!!)
            }
        }.asLiveData()
    }

    fun getBpStatus(
    pId : String
    ): LiveData<Resource<BodyMeasurementMeta>> {
        return object : LocalBoundResource<BodyMeasurementMeta>(appExecutors) {
            override fun loadFromDb(): LiveData<BodyMeasurementMeta> {
                return bodyMeasurementMetaDao.getBMByScreeningId(pId)
            }
        }.asLiveData()
    }
}
