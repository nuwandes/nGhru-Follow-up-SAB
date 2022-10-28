package org.nghru_ins.ghru.ui.samplemanagement.storage.scanqrcode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_ins.ghru.repository.ParticipantRepository
import org.nghru_ins.ghru.repository.SampleRequestRepository
import org.nghru_ins.ghru.util.AbsentLiveData
import org.nghru_ins.ghru.vo.Participant
import org.nghru_ins.ghru.vo.Resource
import org.nghru_ins.ghru.vo.ResourceData
import org.nghru_ins.ghru.vo.request.SampleRequest
import javax.inject.Inject


class ScanBarcodeViewModel
@Inject constructor(participantRepository: ParticipantRepository, sampleRequestRepository: SampleRequestRepository) : ViewModel() {

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
}
