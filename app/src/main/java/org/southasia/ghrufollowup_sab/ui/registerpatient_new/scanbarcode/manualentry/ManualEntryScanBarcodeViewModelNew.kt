package org.southasia.ghrufollowup_sab.ui.registerpatient_new.scanbarcode.manualentry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.southasia.ghrufollowup_sab.repository.ParticipantMetaRepository
import org.southasia.ghrufollowup_sab.util.AbsentLiveData
import org.southasia.ghrufollowup_sab.vo.Resource
import org.southasia.ghrufollowup_sab.vo.request.ParticipantRequest
import javax.inject.Inject


class ManualEntryScanBarcodeViewModelNew
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
