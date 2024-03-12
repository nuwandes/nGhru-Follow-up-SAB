package org.nghru_pk.ghru.ui.bodymeasurements.measurements.second

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.nghru_pk.ghru.vo.BodyMeasurement
import javax.inject.Inject


class MeasurementsSecondViewModel
@Inject constructor() : ViewModel() {

    var bodyMeasurement: MutableLiveData<BodyMeasurement>? = MutableLiveData<BodyMeasurement>()

    var isValidHipSize: Boolean = false

    var isValidWaistSize: Boolean = false


    fun setBodyMeasurement(mesurment: BodyMeasurement) {
        bodyMeasurement?.value = mesurment
    }


    fun getBodyMeasurement(): LiveData<BodyMeasurement> {
        if (bodyMeasurement == null) {
            bodyMeasurement = MutableLiveData<BodyMeasurement>()
            loadBodyMeasurement()
        }
        return bodyMeasurement as MutableLiveData<BodyMeasurement>
    }

    fun loadBodyMeasurement() {
        bodyMeasurement?.value = BodyMeasurement()
    }

}
