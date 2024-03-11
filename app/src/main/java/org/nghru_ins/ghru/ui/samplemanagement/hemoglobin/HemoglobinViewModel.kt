package org.nghru_ins.ghru.ui.samplemanagement.hemoglobin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_ins.ghru.repository.StationDevicesRepository
import org.nghru_ins.ghru.vo.Measurements
import org.nghru_ins.ghru.vo.Resource
import org.nghru_ins.ghru.vo.StationDeviceData
import javax.inject.Inject

class HemoglobinViewModel
@Inject constructor(stationDevicesRepository: StationDevicesRepository) : ViewModel() {

    var hemoglobin: MutableLiveData<String> = MutableLiveData<String>().apply { "" }

    var isValidateError: Boolean = false

    private val _stationName = MutableLiveData<String>()

    fun setStationName(stationName: Measurements) {
        val update = stationName.toString().toLowerCase()
        if (_stationName.value == update) {
            return
        }
        _stationName.value = update
    }

    var stationDeviceList: LiveData<Resource<List<StationDeviceData>>>? = Transformations
        .switchMap(_stationName) { input ->
            stationDevicesRepository.getStationDeviceList(_stationName.value!!)
        }
}
