package org.nghru_inn.ghru.ui.samplemanagement.storage.transfer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_inn.ghru.repository.SampleRequestRepository
import org.nghru_inn.ghru.repository.StationDevicesRepository
import org.nghru_inn.ghru.util.AbsentLiveData
import org.nghru_inn.ghru.vo.FreezerIdData
import org.nghru_inn.ghru.vo.Message
import org.nghru_inn.ghru.vo.Resource
import org.nghru_inn.ghru.vo.StorageDto
import org.nghru_inn.ghru.vo.request.SampleRequest
import javax.inject.Inject

class TransferViewModel @Inject constructor(sampleRequestRepository: SampleRequestRepository, stationDevicesRepository: StationDevicesRepository) : ViewModel() {

    private val _sampleStorageId: MutableLiveData<SampleStorageId> = MutableLiveData()
    private val _sampleStorageIdDelete: MutableLiveData<SampleRequest> = MutableLiveData()
    var isChecked: Boolean = false

    var sampleMangementPocess: LiveData<Resource<Message>>? = Transformations
            .switchMap(_sampleStorageId) { input ->
                input.ifExists { storageDto, sampleRequest ->

                    sampleRequestRepository.syncSampleStorageFRequest(sampleRequest, storageDto!!)
                }
            }


    var sampleMangementPocessDelete: LiveData<Resource<SampleRequest>>? = Transformations
            .switchMap(_sampleStorageIdDelete) { sample ->
                if (sample == null) {
                    AbsentLiveData.create()
                } else {
                    sampleRequestRepository.delete(sample)
                }
            }

    fun setSync(storageDto: StorageDto?, sampleRequest: SampleRequest?) {
        val update = SampleStorageId(storageDto, sampleRequest)
        if (_sampleStorageId.value == update) {
            return
        }
        _sampleStorageId.value = update
    }


    fun setDelete(sampleRequest: SampleRequest?) {

        if (_sampleStorageIdDelete.value == sampleRequest) {
            return
        }
        _sampleStorageIdDelete.value = sampleRequest
    }

    data class SampleStorageId(val storageDto: StorageDto?, val sampleRequest: SampleRequest?) {
        fun <T> ifExists(f: (StorageDto?, SampleRequest?) -> LiveData<T>): LiveData<T> {
            return if (storageDto == null || sampleRequest == null) {
                AbsentLiveData.create()
            } else {
                f(storageDto, sampleRequest)
            }
        }
    }

    fun setHasChecked(explained: Boolean) {
        isChecked = explained

    }

    // ----------------------------------------------------------------------------------------

    private val _freezerIdLocal = MutableLiveData<String>()

    fun setFreezerIdFromDb(status: String) {
        val update = status
        if (_freezerIdLocal.value == update) {
            return
        }
        _freezerIdLocal.value = update
    }

    var getFreezerIdFromDb: LiveData<Resource<FreezerIdData>>? = Transformations
        .switchMap(_freezerIdLocal) { input ->
            stationDevicesRepository.getFreezerId(input)
        }

    // -------------------------------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------------------------------

    private val _freezerIdLLocalInsert = MutableLiveData<FreezerIdData>()

    fun setFreezerIdLocalinsert(sampleList: FreezerIdData) {
        val update = sampleList
        if (_freezerIdLLocalInsert.value == update) {
            return
        }
        _freezerIdLLocalInsert.value = update
    }

    var getFreezerIdLocalInsert: LiveData<Resource<FreezerIdData>>? = Transformations
        .switchMap(_freezerIdLLocalInsert) { input ->
            stationDevicesRepository.insertFreezerIdLocally(_freezerIdLLocalInsert.value!!)
        }

    // -------------------------------------------------------------------------------------------------
}