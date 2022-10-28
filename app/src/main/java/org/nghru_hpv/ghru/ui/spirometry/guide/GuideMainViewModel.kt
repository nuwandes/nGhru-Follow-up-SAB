package org.nghru_hpv.ghru.ui.spirometry.guide

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_hpv.ghru.repository.ParticipantRepository
import org.nghru_hpv.ghru.util.AbsentLiveData
import org.nghru_hpv.ghru.vo.Resource
import org.nghru_hpv.ghru.vo.ResourceData
import org.nghru_hpv.ghru.vo.request.ParticipantRequest
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
