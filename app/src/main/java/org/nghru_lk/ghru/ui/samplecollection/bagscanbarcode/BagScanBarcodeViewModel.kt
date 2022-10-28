package org.nghru_lk.ghru.ui.samplecollection.bagscanbarcode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_lk.ghru.repository.SampleRequestRepository
import org.nghru_lk.ghru.util.AbsentLiveData
import org.nghru_lk.ghru.vo.Resource
import org.nghru_lk.ghru.vo.request.SampleRequest
import javax.inject.Inject


class BagScanBarcodeViewModel
@Inject constructor(sampleRequestRepository: SampleRequestRepository) : ViewModel() {

    private val _sampleId: MutableLiveData<String> = MutableLiveData()
    var screeningIdCheck: LiveData<Resource<SampleRequest>>? = Transformations
        .switchMap(_sampleId) { sampleId ->
            if (sampleId == null) {
                AbsentLiveData.create()
            } else {
                sampleRequestRepository.getItemId(sampleId)
            }
        }
    private val _sampleIdAll: MutableLiveData<String> = MutableLiveData()

    var screeningIdCheckAll: LiveData<Resource<List<SampleRequest>>>? = Transformations
        .switchMap(_sampleIdAll) { sampleId ->
            if (sampleId == null) {
                AbsentLiveData.create()
            } else {
                sampleRequestRepository.getSamples()
            }
        }

    fun setSampleIdAll(sampleId: String?) {
        _sampleIdAll.value = sampleId
    }

    fun setSampleId(sampleId: String?) {
        _sampleId.value = sampleId
    }
}