package org.nghru_hpv.ghru.ui.registerpatient_sg.scanbarcode.manualentry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_hpv.ghru.repository.ParticipantMetaRepository
import org.nghru_hpv.ghru.util.AbsentLiveData
import org.nghru_hpv.ghru.vo.Resource
import org.nghru_hpv.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class ManualEntryScanBarcodeViewModelSG
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
