package org.nghru_ins.ghru.ui.registerpatient_new.scanbarcode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_ins.ghru.repository.ParticipantMetaRepository
import org.nghru_ins.ghru.util.AbsentLiveData
import org.nghru_ins.ghru.vo.Resource
import org.nghru_ins.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class ScanBarcodeViewModelNew
@Inject constructor(participantMetaRepository: ParticipantMetaRepository) : ViewModel() {

    private val _screeningId: MutableLiveData<String> = MutableLiveData()

    var screeningIdCheck: LiveData<Resource<ParticipantRequest>>? = Transformations
        .switchMap(_screeningId) { screeningId ->
            if (screeningId == null) {
                AbsentLiveData.create()
            } else {
                participantMetaRepository.getItemId(screeningId)
            }
        }

    fun setScreeningId(screeningId: String?) {
        _screeningId.value = screeningId
    }
}
