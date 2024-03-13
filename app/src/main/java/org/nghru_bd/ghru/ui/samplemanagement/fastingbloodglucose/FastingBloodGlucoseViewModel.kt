package org.nghru_bd.ghru.ui.samplemanagement.fastingbloodglucose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_bd.ghru.repository.ParticipantRepository
import org.nghru_bd.ghru.repository.SampleRepository
import org.nghru_bd.ghru.repository.StationDevicesRepository
import org.nghru_bd.ghru.repository.UserRepository
import org.nghru_bd.ghru.util.AbsentLiveData
import org.nghru_bd.ghru.vo.*
import org.nghru_bd.ghru.vo.request.FastingBloodGlucoseWithMeta
import org.nghru_bd.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class FastingBloodGlucoseViewModel
@Inject constructor(stationDevicesRepository: StationDevicesRepository,
                    participantRepository: ParticipantRepository,
                    sampleRepository: SampleRepository,
                    userRepository: UserRepository
) : ViewModel() {

    var fastingBloodGlucose: MutableLiveData<String> = MutableLiveData<String>().apply { "" }
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

    //    to get participant request ----------------------------------------------------------------------------

    private val _screeningId: MutableLiveData<String> = MutableLiveData()

    var getParticipant: LiveData<Resource<ResourceData<ParticipantRequest>>> = Transformations
        .switchMap(_screeningId) { screeningId ->
            if (screeningId == null) {
                AbsentLiveData.create()
            } else {
                participantRepository.getParticipantRequest(screeningId, "fasting_blood_glucose")
            }
        }

    fun setParticipantId(screeningId: String?) {
        if (_screeningId.value == screeningId) {
            return
        }
        _screeningId.value = screeningId
    }

//    ------------------------------------------------------------------------------------------------------

    //    Follow up sample process --------------------------------------------------------------------------------

    private val _followUpSampleMangementId: MutableLiveData<FollowUpSampleMangementId> = MutableLiveData()

    fun setFollowUpBloodGlucose(
        screenId: String, fastingBloodGlucose: FastingBloodGlucoseWithMeta?
    ) {

        val update = FollowUpSampleMangementId(screenId, fastingBloodGlucose)
        if (_followUpSampleMangementId.value == update) {
            return
        }
        _followUpSampleMangementId.value = update
    }

    data class FollowUpSampleMangementId(
        val screenId: String?,
        val fastingBloodGlucose: FastingBloodGlucoseWithMeta?
    ) {
        fun <T> ifExists(f: (String?, FastingBloodGlucoseWithMeta?) -> LiveData<T>): LiveData<T> {
            return if (screenId == null || fastingBloodGlucose == null) {
                AbsentLiveData.create()
            } else {
                f(screenId, fastingBloodGlucose)
            }
        }
    }

    var folloUpSampleManagementPocess: LiveData<Resource<ResourceData<CommonResponce>>>?= Transformations
        .switchMap(_followUpSampleMangementId) { input ->
            input.ifExists {screenID, fastingBloodGlucose ->

                sampleRepository.syncSampleProcessFollowUp(screenId = screenID!!, fastingBloodGlucose = fastingBloodGlucose!!)
            }
        }

//    ---------------------------------------------------------------------------------------------------------

//    to set and get user -------------------------------------------------------------------------------------

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

//    ---------------------------------------------------------------------------------------------------------
}
