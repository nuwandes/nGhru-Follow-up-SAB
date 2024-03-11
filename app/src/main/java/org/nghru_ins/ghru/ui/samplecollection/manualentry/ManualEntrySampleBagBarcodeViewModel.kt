package org.nghru_ins.ghru.ui.samplecollection.manualentry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_ins.ghru.repository.SampleRequestRepository
import org.nghru_ins.ghru.repository.StationDevicesRepository
import org.nghru_ins.ghru.util.AbsentLiveData
import org.nghru_ins.ghru.vo.Resource
import org.nghru_ins.ghru.vo.SampleIdData
import org.nghru_ins.ghru.vo.request.SampleRequest
import javax.inject.Inject


class ManualEntrySampleBagBarcodeViewModel
@Inject constructor(
    sampleRequestRepository: SampleRequestRepository,
    stationDevicesRepository: StationDevicesRepository
) : ViewModel() {

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

    // ----------------------------------------------------------------------------------------------------

    private val _sampleIdLocal = MutableLiveData<String>()

    fun setSampleIdFromDb(status: String) {
        val update = status
        if (_sampleIdLocal.value == update) {
            return
        }
        _sampleIdLocal.value = update
    }

    var getSampleIdFromDb: LiveData<Resource<SampleIdData>>? = Transformations
        .switchMap(_sampleIdLocal) { input ->
            stationDevicesRepository.getSampleId(input)
        }

    // -------------------------------------------------------------------------------------------------------
}
