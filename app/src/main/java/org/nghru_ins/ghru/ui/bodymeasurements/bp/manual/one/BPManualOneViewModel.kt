package org.nghru_ins.ghru.ui.bodymeasurements.bp.manual.one

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_ins.ghru.repository.BloodPressureRequestRepository
import org.nghru_ins.ghru.repository.ParticipantRepository
import org.nghru_ins.ghru.repository.StationDevicesRepository
import org.nghru_ins.ghru.repository.UserRepository
import org.nghru_ins.ghru.util.AbsentLiveData
import org.nghru_ins.ghru.vo.*
import org.nghru_ins.ghru.vo.request.BloodPressureMetaRequest
import org.nghru_ins.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class BPManualOneViewModel
@Inject constructor(stationDevicesRepository: StationDevicesRepository,
                    userRepository: UserRepository,
                    bloodPressureRequestRepository: BloodPressureRequestRepository,
                    participantRepository: ParticipantRepository
) : ViewModel() {


    var bodyMeasurement: MutableLiveData<BodyMeasurement>? = MutableLiveData<BodyMeasurement>()

    var bloodPressure: MutableLiveData<BloodPressure>? = null

    private val _bloodPressureRequestRemote: MutableLiveData<BloodPressureMetaRequestId> = MutableLiveData()

    private val _stationName = MutableLiveData<String>()
    var hogtt: MutableLiveData<String> = MutableLiveData<String>().apply { "" }
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

    private val _email = MutableLiveData<String>()

    val user: LiveData<Resource<User>>? = Transformations
            .switchMap(_email) { emailx ->
                if (emailx == null) {
                    AbsentLiveData.create()
                } else {
                    userRepository.loadUserDB()
                }
            }

    fun setUser(email: String?) {
        if (_email.value != email) {
            _email.value = email
        }
    }

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


    private fun loadBodyMeasurement() {
        bodyMeasurement?.value = BodyMeasurement()
    }

    fun setArm(arm: Arm) {
        bodyMeasurement?.value?.bloodPressures?.value!![0].arm.postValue(arm.name)
    }

    fun getBloodPressure(): LiveData<BloodPressure> {

        if (bloodPressure == null) {
            bloodPressure = MutableLiveData<BloodPressure>()
            bloodPressure?.value = BloodPressure(0)
        }

        return bloodPressure as MutableLiveData<BloodPressure>
    }

    var bloodPressureRequestRemote: LiveData<Resource<BloodPressureMetaRequest>>? = Transformations
            .switchMap(_bloodPressureRequestRemote) { member ->
                member.ifExists { bloodPressureMetaRequest, participantRequest ->
                    bloodPressureRequestRepository.syncBloodPressureMetaRequest(bloodPressureMetaRequest!!, participantRequest!!)
                }
            }

    fun setBloodPressureMetaRequestRemote(bloodPressureMetaRequest: BloodPressureMetaRequest, participant: ParticipantRequest) {
        val measurementRequestId = BloodPressureMetaRequestId(bloodPressureMetaRequest, participant)
        if (_bloodPressureRequestRemote.value == measurementRequestId) {
            return
        }
        _bloodPressureRequestRemote.value = measurementRequestId
    }

    data class BloodPressureMetaRequestId(val bloodPressureMetaRequest: BloodPressureMetaRequest?, val participant: ParticipantRequest?) {
        fun <T> ifExists(f: (BloodPressureMetaRequest?, ParticipantRequest?) -> LiveData<T>): LiveData<T> {
            return if (bloodPressureMetaRequest == null || participant == null) {
                AbsentLiveData.create()
            } else {
                f(bloodPressureMetaRequest, participant)
            }
        }
    }

//    get participant request ---------------------------------------------------------------------------------

    private val _screeningId: MutableLiveData<String> = MutableLiveData()

    var participant: LiveData<Resource<ResourceData<ParticipantRequest>>> = Transformations
        .switchMap(_screeningId) { screeningId ->
            if (screeningId == null) {
                AbsentLiveData.create()
            } else {
                participantRepository.getParticipantRequest(screeningId, "bp")
            }
        }

    fun setScreeningId(screeningId: String?) {
        if (_screeningId.value == screeningId) {
            return
        }
        _screeningId.value = screeningId
    }

//    ---------------------------------------------------------------------------------------------------------


}
