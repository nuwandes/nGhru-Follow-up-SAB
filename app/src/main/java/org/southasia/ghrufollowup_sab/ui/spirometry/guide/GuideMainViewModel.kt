package org.southasia.ghrufollowup_sab.ui.spirometry.guide

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.southasia.ghrufollowup_sab.repository.ParticipantRepository
import org.southasia.ghrufollowup_sab.util.AbsentLiveData
import org.southasia.ghrufollowup_sab.vo.Resource
import org.southasia.ghrufollowup_sab.vo.ResourceData
import org.southasia.ghrufollowup_sab.vo.request.ParticipantRequest
import javax.inject.Inject

class GuideMainViewModel
@Inject constructor(participantRepository: ParticipantRepository) : ViewModel() {

    var hasExplained: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { false }

    var isChecked: Boolean = false

    fun setHasExplained(explained: Boolean) {
        isChecked = explained
        hasExplained.value = (explained)
    }

//    to get participant request ------------------------------------------------------------------------

    private val _screeningId: MutableLiveData<String> = MutableLiveData()

    var getParticipant: LiveData<Resource<ResourceData<ParticipantRequest>>> = Transformations
        .switchMap(_screeningId) { screeningId ->
            if (screeningId == null) {
                AbsentLiveData.create()
            } else {
                participantRepository.getParticipantRequest(screeningId, "spirometry")
            }
        }

    fun setScreeningId(screeningId: String?) {
        if (_screeningId.value == screeningId) {
            return
        }
        _screeningId.value = screeningId
    }

//    ---------------------------------------------------------------------------------------------------
}
