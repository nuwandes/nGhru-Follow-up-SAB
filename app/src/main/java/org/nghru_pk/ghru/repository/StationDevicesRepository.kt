package org.nghru_pk.ghru.repository

import androidx.lifecycle.LiveData
import org.nghru_pk.ghru.AppExecutors
import org.nghru_pk.ghru.api.ApiResponse
import org.nghru_pk.ghru.api.NghruService
import org.nghru_pk.ghru.db.FreezerIdDao
import org.nghru_pk.ghru.db.SampleIdDao
import org.nghru_pk.ghru.db.StationDevicesDao
import org.nghru_pk.ghru.db.StorageIdDao
import org.nghru_pk.ghru.vo.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationDevicesRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val stationDevicesDao: StationDevicesDao,
    private val storageIdDao: StorageIdDao,
    private val sampleIdDao: SampleIdDao,
    private val freezerIdDao: FreezerIdDao

) {

    fun loadStationDevices(): LiveData<Resource<ResourceData<List<StationDeviceData>>>> {

        return object : NetworkOnlyBoundResource<ResourceData<List<StationDeviceData>>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<List<StationDeviceData>>>> {
                return nghruService.getStationDevices()
            }
        }.asLiveData()
    }

    fun insertStationDeviceList(stationDevicesList: List<StationDeviceData>):
            LiveData<Resource<List<StationDeviceData>>> {
        stationDevicesDao.deleteAll()
        return object : LocalBoundInsertAllResource<List<StationDeviceData>>(appExecutors) {

            override fun loadFromDb(): LiveData<List<StationDeviceData>> {
                return stationDevicesDao.getAllDevice()
            }

            override fun insertDb(): Unit {

                return stationDevicesDao.insertAll(stationDevicesList)
            }
        }.asLiveData()
    }

    fun getStationDeviceList(
        measurement: String
    ): LiveData<Resource<List<StationDeviceData>>> {
        return object : LocalBoundResource<List<StationDeviceData>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<StationDeviceData>> {
                return stationDevicesDao.stationDeviceList(measurement)
            }
        }.asLiveData()
    }

    // ------------------------------------ Storage data ---------------------------------------------------

    fun getStorageIds(): LiveData<Resource<ResourceData<List<StorageIdData>>>> {

        return object : NetworkOnlyBoundResource<ResourceData<List<StorageIdData>>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<List<StorageIdData>>>> {
                return nghruService.getStorageIds()
            }
        }.asLiveData()
    }

    fun insertStorageIdList(storageIdList: List<StorageIdData>):
            LiveData<Resource<List<StorageIdData>>> {
        storageIdDao.deleteAll()
        return object : LocalBoundInsertAllResource<List<StorageIdData>>(appExecutors) {

            override fun loadFromDb(): LiveData<List<StorageIdData>> {
                return storageIdDao.getAllStorageIds()
            }

            override fun insertDb(): Unit {

                return storageIdDao.insertAll(storageIdList)
            }
        }.asLiveData()
    }

     // ---------------------------------------------------------------------------------------------------

    // ----------------------------------- Sample data -----------------------------------------------------

    fun getSampleIds(): LiveData<Resource<ResourceData<List<SampleIdData>>>> {

        return object : NetworkOnlyBoundResource<ResourceData<List<SampleIdData>>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<List<SampleIdData>>>> {
                return nghruService.getSampleIds()
            }
        }.asLiveData()
    }

    fun insertSampleIdList(sampleIdList: List<SampleIdData>):
            LiveData<Resource<List<SampleIdData>>> {
        storageIdDao.deleteAll()
        return object : LocalBoundInsertAllResource<List<SampleIdData>>(appExecutors) {

            override fun loadFromDb(): LiveData<List<SampleIdData>> {
                return sampleIdDao.getAllSampleIds()
            }

            override fun insertDb(): Unit {

                return sampleIdDao.insertAll(sampleIdList)
            }
        }.asLiveData()
    }

    //------------------------------------------------------------------------------------------------------

    // ----------------------------------- Freezer data -----------------------------------------------------

    fun getFreezerIds(): LiveData<Resource<ResourceData<List<FreezerIdData>>>> {

        return object : NetworkOnlyBoundResource<ResourceData<List<FreezerIdData>>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<List<FreezerIdData>>>> {
                return nghruService.getFreezerIds()
            }
        }.asLiveData()
    }

    fun insertFreezerIdList(freezerIdList: List<FreezerIdData>):
            LiveData<Resource<List<FreezerIdData>>> {
        freezerIdDao.deleteAll()
        return object : LocalBoundInsertAllResource<List<FreezerIdData>>(appExecutors) {

            override fun loadFromDb(): LiveData<List<FreezerIdData>> {
                return freezerIdDao.getAllFreezerIds()
            }

            override fun insertDb(): Unit {

                return freezerIdDao.insertAll(freezerIdList)
            }
        }.asLiveData()
    }

    //------------------------------------------------------------------------------------------------------

    fun getFreezerId(
        freezerId: String
    ): LiveData<Resource<FreezerIdData>> {
        return object : LocalBoundResource<FreezerIdData>(appExecutors) {
            override fun loadFromDb(): LiveData<FreezerIdData> {
                return freezerIdDao.getFreezerId(freezerId)
            }
        }.asLiveData()
    }

    // ------------------------------------------------------------------------------------------------------

    fun getStorageId(
        storageId: String
    ): LiveData<Resource<StorageIdData>> {
        return object : LocalBoundResource<StorageIdData>(appExecutors) {
            override fun loadFromDb(): LiveData<StorageIdData> {
                return storageIdDao.getStorageId(storageId)
            }
        }.asLiveData()
    }

    // ----------------------------------------------------------------------------------------------------

    fun getSampleId(
        sampleId: String
    ): LiveData<Resource<SampleIdData>> {
        return object : LocalBoundResource<SampleIdData>(appExecutors) {
            override fun loadFromDb(): LiveData<SampleIdData> {
                return sampleIdDao.getSampleId(sampleId)
            }
        }.asLiveData()
    }

    // ------------------------------------------------------------------------------------

    fun insertSampleIdLocally(
        sampleId: SampleIdData
    ): LiveData<Resource<SampleIdData>> {
        return object : LocalBoundInsertAllResource<SampleIdData>(appExecutors) {

            override fun loadFromDb(): LiveData<SampleIdData> {
                return sampleIdDao.getSampleId("GET")
            }

            override fun insertDb(): Unit {

                return sampleIdDao.insert(sampleId)
            }
        }.asLiveData()
    }

    //-------------------------------------------------------------------------------------------

    fun insertFreezerIdLocally(
        freezerIdData: FreezerIdData
    ): LiveData<Resource<FreezerIdData>> {
        return object : LocalBoundInsertAllResource<FreezerIdData>(appExecutors) {

            override fun loadFromDb(): LiveData<FreezerIdData> {
                return freezerIdDao.getFreezerId("GET")
            }

            override fun insertDb(): Unit {

                return freezerIdDao.insert(freezerIdData)
            }
        }.asLiveData()
    }

    // --------------------------------------------------------------------------------------

    fun insertStorageIdLocally(
        storageIdData: StorageIdData
    ): LiveData<Resource<StorageIdData>> {
        return object : LocalBoundInsertAllResource<StorageIdData>(appExecutors) {

            override fun loadFromDb(): LiveData<StorageIdData> {
                return storageIdDao.getStorageId("GET")
            }

            override fun insertDb(): Unit {

                return storageIdDao.insert(storageIdData)
            }
        }.asLiveData()
    }

}