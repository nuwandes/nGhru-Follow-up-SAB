package org.nghru_pk.ghru.ui.samplemanagement.storage.reasonc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_pk.ghru.repository.CancelRequestRepository
import org.nghru_pk.ghru.util.AbsentLiveData
import org.nghru_pk.ghru.vo.Message
import org.nghru_pk.ghru.vo.Resource
import org.nghru_pk.ghru.vo.ResourceData
import org.nghru_pk.ghru.vo.request.CancelRequest
import org.nghru_pk.ghru.vo.request.SampleRequest
import javax.inject.Inject


class ReasonDialogViewModel
@Inject constructor(cancelRequestRepository: CancelRequestRepository) : ViewModel() {

    private val _cancelId: MutableLiveData<CancelId> = MutableLiveData()


    var cancelId: LiveData<Resource<ResourceData<Message>>>? = Transformations
            .switchMap(_cancelId) { input ->
                input.ifExists { sampleRequestX, cancelRequest ->
                    cancelRequestRepository.syncCancelStorageRequest(sampleRequestX, cancelRequest)
                }
            }

    fun setLogin(sampleRequest: SampleRequest?, cancelRequest: CancelRequest?) {
        val update = CancelId(sampleRequest, cancelRequest)
        if (_cancelId.value == update) {
            return
        }
        _cancelId.value = update
    }

    data class CancelId(val sampleRequest: SampleRequest?, val cancelRequest: CancelRequest?) {
        fun <T> ifExists(f: (SampleRequest, CancelRequest) -> LiveData<T>): LiveData<T> {
            return if (sampleRequest == null || cancelRequest == null) {
                AbsentLiveData.create()
            } else {
                f(sampleRequest, cancelRequest)
            }
        }
    }

    private val _deleteId: MutableLiveData<SampleRequest> = MutableLiveData()
    var deleteId: LiveData<Resource<SampleRequest>>? = Transformations
        .switchMap(_deleteId) { mSampleRequest ->
            if (mSampleRequest == null) {
                AbsentLiveData.create()
            } else {
                cancelRequestRepository.delete(mSampleRequest)
            }
        }
    fun setDelete(sampleRequest: SampleRequest?) {
        if (_deleteId.value == sampleRequest) {
            return
        }
        _deleteId.value = sampleRequest
    }

}
