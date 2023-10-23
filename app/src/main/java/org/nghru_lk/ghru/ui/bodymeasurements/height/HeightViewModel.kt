package org.nghru_lk.ghru.ui.bodymeasurements.height

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_lk.ghru.repository.StationDevicesRepository
import org.nghru_lk.ghru.vo.Measurements
import org.nghru_lk.ghru.vo.Resource
import org.nghru_lk.ghru.vo.StationDeviceData
import javax.inject.Inject


class HeightViewModel
@Inject constructor(stationDevicesRepository: StationDevicesRepository) : ViewModel() {


    var isValidateError: Boolean = false

    var isValidHeight: Boolean = false

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