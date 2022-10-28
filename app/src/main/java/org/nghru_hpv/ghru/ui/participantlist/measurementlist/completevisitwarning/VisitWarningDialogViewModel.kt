package org.nghru_hpv.ghru.ui.participantlist.measurementlist.completevisitwarning

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_hpv.ghru.repository.ParticipantListRepository
import org.nghru_hpv.ghru.util.AbsentLiveData
import org.nghru_hpv.ghru.vo.ParticipantListItem
import org.nghru_hpv.ghru.vo.Resource
import org.nghru_hpv.ghru.vo.ResourceData
import javax.inject.Inject


class VisitWarningDialogViewModel
@Inject constructor(repository: ParticipantListRepository) : ViewModel() {

    //    update single participant ------------------------------------------------------------------------------

    private val _participantUpdateRequest: MutableLiveData<ParticipantListItem> = MutableLiveData()
    private var _participantID: String? = null
    private val _participantRequest: MutableLiveData<ParticipantListItem> = MutableLiveData()

    var participantFollowUpStatusUpdate: LiveData<Resource<ResourceData<ParticipantListItem>>>? = Transformations
        .switchMap(_participantUpdateRequest) { participantRequest ->
            if (participantRequest == null) {
                AbsentLiveData.create()
            } else {
                repository.updateParticipantItem(participantRequest,_participantID!!)
            }
        }

    fun setFollowUpParticipant(participantItem: ParticipantListItem, participantId: String?) {
        _participantID = participantId
        if (_participantRequest.value == participantItem) {
            return
        }
        _participantRequest.value = participantItem
    }
    fun updateParticipantFollowUp(participantItem: ParticipantListItem, participantId: String?) {
        _participantID = participantId
        if (_participantUpdateRequest.value == participantItem) {
            return
        }
        _participantUpdateRequest.value = participantItem
    }

//    --------------------------------------------------------------------------------------------------------

}
