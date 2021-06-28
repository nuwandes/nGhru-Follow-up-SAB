package org.southasia.ghrufollowup_sab.ui.samplecollection.manualentry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.southasia.ghrufollowup_sab.repository.SampleRequestRepository
import org.southasia.ghrufollowup_sab.util.AbsentLiveData
import org.southasia.ghrufollowup_sab.vo.Resource
import org.southasia.ghrufollowup_sab.vo.request.SampleRequest
import javax.inject.Inject


class ManualEntrySampleBagBarcodeViewModel
@Inject constructor(sampleRequestRepository: SampleRequestRepository) : ViewModel() {

    private val _sampleId: MutableLiveData<String> = MutableLiveData()
    private val _sampleIdAll: MutableLiveData<String> = MutableLiveData()
    var screeningIdCheck: LiveData<Resource<SampleRequest>>? = Transformations
        .switchMap(_sampleId) { sampleId ->
            if (sampleId == null) {
                AbsentLiveData.create()
            } else {
                sampleRequestRepository.getItemId(sampleId)
            }
        }

    var screeningIdCheckAll: LiveData<Resource<List<SampleRequest>>>? = Transformations
        .switchMap(_sampleIdAll) { sampleId ->
            if (sampleId == null) {
                AbsentLiveData.create()
            } else {
                sampleRequestRepository.getSamples()
            }
        }

    fun setSampleId(sampleId: String?) {
        _sampleId.value = sampleId
    }

    fun setSampleIdAll(sampleId: String?) {
        _sampleIdAll.value = sampleId
    }
}
