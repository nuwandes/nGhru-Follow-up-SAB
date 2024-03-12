package org.nghru_pk.ghru.ui.samplemanagement.hb1ac

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_pk.ghru.repository.StationDevicesRepository
import org.nghru_pk.ghru.vo.Measurements
import org.nghru_pk.ghru.vo.Resource
import org.nghru_pk.ghru.vo.StationDeviceData
import javax.inject.Inject


class Hb1AcViewModel
@Inject constructor(stationDevicesRepository: StationDevicesRepository) : ViewModel() {

    var hb1Ac: MutableLiveData<String> = MutableLiveData<String>().apply { "" }

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
