package org.nghru_lk.ghru.repository

import androidx.lifecycle.LiveData
import org.nghru_lk.ghru.AppExecutors
import org.nghru_lk.ghru.api.ApiResponse
import org.nghru_lk.ghru.api.NghruService
import org.nghru_lk.ghru.db.HouseholdRequestMetaMetaDao
import org.nghru_lk.ghru.db.NGRHUDb
import org.nghru_lk.ghru.vo.HouseholdBodyData
import org.nghru_lk.ghru.vo.Resource
import org.nghru_lk.ghru.vo.ResourceData
import org.nghru_lk.ghru.vo.ResponceData
import org.nghru_lk.ghru.vo.request.HouseholdRequestMeta
import org.nghru_lk.ghru.vo.request.HouseholdRequestMetaResponce
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class HouseholdRequestRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val householdRequestMetaMetaDao: HouseholdRequestMetaMetaDao,
    private val nghruService: NghruService,
    private val nGRHUDb: NGRHUDb
) : Serializable {

    fun syncHousehold(
        household: HouseholdRequestMeta
    ): LiveData<Resource<ResponceData>> {
        return object : NetworkOnlyBoundResource<ResponceData>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResponceData>> {
                return nghruService.addHouseholdRequest(household)
            }
        }.asLiveData()
    }

    fun insertHouseholdRequest(
        household: HouseholdRequestMeta
    ): LiveData<Resource<HouseholdRequestMeta>> {
        return object : LocalBoundInsertResource<HouseholdRequestMeta>(appExecutors) {
            override fun loadFromDb(rowId: Long): LiveData<HouseholdRequestMeta> {
                return householdRequestMetaMetaDao.getHouseholdRequestMeta(rowId)
            }

            override fun insertDb(): Long {
                return householdRequestMetaMetaDao.insert(household)
            }
        }.asLiveData()
    }


    fun insertHouseholdRequestAll(
        householdRequestMetas: List<HouseholdRequestMeta>?
    ): LiveData<Resource<List<HouseholdRequestMeta>>> {
        return object : LocalBoundInsertAllResource<List<HouseholdRequestMeta>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<HouseholdRequestMeta>> {
                return householdRequestMetaMetaDao.getHouseholdRequestMetas()
            }

            override fun insertDb(): Unit {
                nGRHUDb.beginTransaction()
                return try {
                    householdRequestMetaMetaDao.deleteAll(false)
                    householdRequestMetaMetaDao.insert(householdRequestMetas!!)
                    nGRHUDb.setTransactionSuccessful()
                } catch (e: Exception) {

                } finally {
                    nGRHUDb.endTransaction()
                }
            }
        }.asLiveData()
    }


    fun searchHouseholds(
        search: String
    ): LiveData<Resource<List<HouseholdRequestMeta>>> {
        return object : LocalBoundResource<List<HouseholdRequestMeta>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<HouseholdRequestMeta>> {
                return householdRequestMetaMetaDao.searchHouseholdRequestMetas(search)
            }
        }.asLiveData()
    }

    fun getHouseholdRequestMetasByStatus(
        syncStatus: Boolean
    ): LiveData<Resource<List<HouseholdRequestMeta>>> {
        return object : LocalBoundResource<List<HouseholdRequestMeta>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<HouseholdRequestMeta>> {
                return householdRequestMetaMetaDao.getHouseholdBySyncStatus(syncStatus)
            }
        }.asLiveData()
    }

    fun getHouseholdRequestMetas(
    ): LiveData<Resource<List<HouseholdRequestMeta>>> {
        return object : LocalBoundResource<List<HouseholdRequestMeta>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<HouseholdRequestMeta>> {
                return householdRequestMetaMetaDao.getHouseholdRequestMetas()
            }
        }.asLiveData()
    }


    fun getHouseholdByEnumerationId(
        enumerationId: String
    ): LiveData<Resource<HouseholdRequestMeta>> {
        return object : LocalBoundResource<HouseholdRequestMeta>(appExecutors) {
            override fun loadFromDb(): LiveData<HouseholdRequestMeta> {
                return householdRequestMetaMetaDao.getHouseholdByEnumerationId(enumerationId)
            }
        }.asLiveData()
    }

    fun getHouseHold(
        enumerationId: String
    ): LiveData<Resource<ResourceData<HouseholdBodyData>>> {
        return object : NetworkOnlyBoundResource<ResourceData<HouseholdBodyData>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<HouseholdBodyData>>> {
                return nghruService.getHouseHold(enumerationId)
            }
        }.asLiveData()
    }

    fun delete(
        householdBodyData: HouseholdRequestMeta
    ): LiveData<Resource<HouseholdRequestMeta>> {
        return object : LocalBoundIDeleteResource<HouseholdRequestMeta>(appExecutors) {
            override fun deleteDb() {
                //L.d("HouseholdRequestMeta id" + householdBodyData.id)
                return householdRequestMetaMetaDao.delete(householdBodyData)
            }

        }.asLiveData()
    }

    fun getItemId(
        invitationId: String
    ): LiveData<Resource<HouseholdRequestMeta>> {
        return object : NetworkBoundResource<HouseholdRequestMeta, ResourceData<HouseholdRequestMeta>>(appExecutors) {
            override fun saveCallResult(item: ResourceData<HouseholdRequestMeta>) {
                householdRequestMetaMetaDao.insert(item.data!!)
            }


            override fun shouldFetch(data: HouseholdRequestMeta?): Boolean = data == null

            override fun loadFromDb(): LiveData<HouseholdRequestMeta> {
                return householdRequestMetaMetaDao.getHouseholdByEnumerationId(invitationId)
            }

            override fun createCall(): LiveData<ApiResponse<ResourceData<HouseholdRequestMeta>>> {
                return nghruService.getHouseHoldX(invitationId)
            }

        }.asLiveData()
    }


    fun getHouseHoldsRead(
    ): LiveData<Resource<List<HouseholdRequestMeta>>> {
        return object : LocalBoundResource<List<HouseholdRequestMeta>>(appExecutors) {

            override fun loadFromDb(): LiveData<List<HouseholdRequestMeta>> {
                return householdRequestMetaMetaDao.getHouseholdRequestMetas()
            }

        }.asLiveData()
    }

    fun getHouseHolds(
    ): LiveData<Resource<ResourceData<List<HouseholdRequestMetaResponce>>>> {

        return object : NetworkOnlyBoundResource<ResourceData<List<HouseholdRequestMetaResponce>>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<List<HouseholdRequestMetaResponce>>>> {
                return nghruService.getHouseHolds()
            }
        }.asLiveData()
    }

}
