package org.nghru_lk.ghru.ui.samplemanagement.storage.manualentry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_lk.ghru.repository.ParticipantRepository
import org.nghru_lk.ghru.repository.SampleRequestRepository
import org.nghru_lk.ghru.repository.StationDevicesRepository
import org.nghru_lk.ghru.util.AbsentLiveData
import org.nghru_lk.ghru.vo.Participant
import org.nghru_lk.ghru.vo.Resource
import org.nghru_lk.ghru.vo.ResourceData
import org.nghru_lk.ghru.vo.StorageIdData
import org.nghru_lk.ghru.vo.request.SampleRequest
import javax.inject.Inject

class ManualEntryViewModel @Inject constructor(
    participantRepository: ParticipantRepository,
    sampleRequestRepository: SampleRequestRepository,
    stationDevicesRepository: StationDevicesRepository) : ViewModel() {

    private val _screeningId: MutableLiveData<String> = MutableLiveData()
    val screeningId: LiveData<String>
        get() = _screeningId

    var participant: LiveData<Resource<ResourceData<Participant>>> = Transformations
            .switchMap(_screeningId) { screeningId ->
                if (screeningId == null) {
                    AbsentLiveData.create()
                } else {
                    participantRepository.getParticipant(screeningId)
                }
            }

    fun setScreeningId(screeningId: String?) {
        if (_screeningId.value == screeningId) {
            return
        }
        _screeningId.value = screeningId
    }


    private val _storageId: MutableLiveData<String> = MutableLiveData()

    var storageIdCheck: LiveData<Resource<SampleRequest>>? = Transformations
            .switchMap(_storageId) { storageId ->
                if (storageId == null) {
                    AbsentLiveData.create()
                } else {
                    sampleRequestRepository.getStorageId(storageId)
                }
            }


    fun setStorageId(storageId: String?) {
        _storageId.value = storageId
    }

    private val _storageIdLocal = MutableLiveData<String>()

    fun setStorageIdFromDb(status: String) {
        val update = status
        if (_storageIdLocal.value == update) {
            return
        }
        _storageIdLocal.value = update
    }

    var getStorageIdFromDb: LiveData<Resource<StorageIdData>>? = Transformations
        .switchMap(_storageIdLocal) { input ->
            stationDevicesRepository.getStorageId(input)
        }

    // -------------------------------------------------------------------------------------------------------
}
