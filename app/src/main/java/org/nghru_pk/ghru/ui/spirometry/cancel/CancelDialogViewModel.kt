package org.nghru_pk.ghru.ui.spirometry.cancel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_pk.ghru.repository.CancelRequestRepository
import org.nghru_pk.ghru.util.AbsentLiveData
import org.nghru_pk.ghru.vo.MessageCancel
import org.nghru_pk.ghru.vo.Resource
import org.nghru_pk.ghru.vo.request.CancelRequest
import org.nghru_pk.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class CancelDialogViewModel
@Inject constructor(cancelRequestRepository: CancelRequestRepository) : ViewModel() {

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

    private val _cancelIdNew: MutableLiveData<CancelIdNew> = MutableLiveData()

    var cancelIdNew: LiveData<Resource<MessageCancel>>? = Transformations
        .switchMap(_cancelIdNew) { input ->
            input.ifExists { screeningId, cancelRequest ->
                cancelRequestRepository.syncCancelRequestUpdated(screeningId, cancelRequest)
            }
        }

    fun setLoginNew(screeningId: String?, cancelRequest: CancelRequest?) {
        val update = CancelIdNew(screeningId, cancelRequest)
        if (_cancelIdNew.value == update) {
            return
        }
        _cancelIdNew.value = update
    }

    data class CancelIdNew(val screeningId : String?, val cancelRequest: CancelRequest?) {
        fun <T> ifExists(f: (String, CancelRequest) -> LiveData<T>): LiveData<T> {
            return if (screeningId == null || cancelRequest == null) {
                AbsentLiveData.create()
            } else {
                f(screeningId, cancelRequest)
            }
        }
    }
}
