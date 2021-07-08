package org.southasia.ghrufollowup_sab.ui.samplecollection.cancel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.southasia.ghrufollowup_sab.repository.CancelRequestRepository
import org.southasia.ghrufollowup_sab.util.AbsentLiveData
import org.southasia.ghrufollowup_sab.vo.MessageCancel
import org.southasia.ghrufollowup_sab.vo.Resource
import org.southasia.ghrufollowup_sab.vo.request.CancelRequest
import org.southasia.ghrufollowup_sab.vo.request.ParticipantRequest
import javax.inject.Inject

class CancelDialogViewModel @Inject constructor(cancelRequestRepository: CancelRequestRepository) : ViewModel() {

    private val _cancelId: MutableLiveData<CancelId> = MutableLiveData()


    var cancelId: LiveData<Resource<MessageCancel>>? = Transformations
        .switchMap(_cancelId) { input ->
            input.ifExists { participantRequest, cancelRequest ->
                cancelRequestRepository.syncCancelRequest(participantRequest, cancelRequest)
            }
        }

    fun setLogin(participantRequest: ParticipantRequest?, cancelRequest: CancelRequest?) {
        val update = CancelId(participantRequest, cancelRequest)
        if (_cancelId.value == update) {
            return
        }
        _cancelId.value = update
    }

    data class CancelId(val participantRequest: ParticipantRequest?, val cancelRequest: CancelRequest?) {
        fun <T> ifExists(f: (ParticipantRequest, CancelRequest) -> LiveData<T>): LiveData<T> {
            return if (participantRequest == null || cancelRequest == null) {
                AbsentLiveData.create()
            } else {
                f(participantRequest, cancelRequest)
            }
        }
    }
}