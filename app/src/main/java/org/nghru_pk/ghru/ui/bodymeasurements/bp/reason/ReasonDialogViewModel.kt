package org.nghru_pk.ghru.ui.bodymeasurements.bp.reason

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_pk.ghru.repository.CancelRequestRepository
import org.nghru_pk.ghru.util.AbsentLiveData
import org.nghru_pk.ghru.vo.MessageCancel
import org.nghru_pk.ghru.vo.Resource
import org.nghru_pk.ghru.vo.request.CancelRequest
import javax.inject.Inject


class ReasonDialogViewModel
@Inject constructor(cancelRequestRepository: CancelRequestRepository) : ViewModel() {

    private val _cancelId: MutableLiveData<CancelId> = MutableLiveData()


    var cancelId: LiveData<Resource<MessageCancel>>? = Transformations
        .switchMap(_cancelId) { input ->
            input.ifExists { screeningId, cancelRequest ->
                cancelRequestRepository.syncCancelRequestUpdated(screeningId, cancelRequest)
            }
        }

    fun setLogin(screeningId : String?, cancelRequest: CancelRequest?) {
        val update = CancelId(screeningId, cancelRequest)
        if (_cancelId.value == update) {
            return
        }
        _cancelId.value = update
    }

    data class CancelId(val screeningId : String?, val cancelRequest: CancelRequest?) {
        fun <T> ifExists(f: (String, CancelRequest) -> LiveData<T>): LiveData<T> {
            return if (screeningId == null || cancelRequest == null) {
                AbsentLiveData.create()
            } else {
                f(screeningId, cancelRequest)
            }
        }
    }
}
