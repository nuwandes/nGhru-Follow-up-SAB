package org.nghru_lk.ghru.ui.fundoscopy.reading

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_lk.ghru.repository.*
import org.nghru_lk.ghru.util.AbsentLiveData
import org.nghru_lk.ghru.vo.*
import org.nghru_lk.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class FundoscopyReadingViewModel
@Inject constructor(
    val assertRepository: AssertRepository,
    fundoscopyRepository: FundoscopyRepository,
    stationDevicesRepository: StationDevicesRepository,
    participantRepository: ParticipantRepository,
    userRepository: UserRepository
) : ViewModel() {
    var fundoscopySyncError: MutableLiveData<Boolean>? = MutableLiveData<Boolean>().apply { }


    private val _participantId: MutableLiveData<ParticipantRequest> = MutableLiveData()

    private val _participantIdComplte: MutableLiveData<ParticipantRequest> = MutableLiveData()
    private var comment: String? = null
    private var device_id: String? = null
    private var pupil_dilation : Boolean = false
    private var isOnline : Boolean = false
    private var cataractObservation : String? = null

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

    var asserts: LiveData<Resource<ResourceData<List<Asset>>>>? = Transformations
        .switchMap(_participantId) { participantId ->
            if (participantId == null) {
                AbsentLiveData.create()
            } else {
                assertRepository.getAssets(participantId, "fundoscopy")
            }
        }

    var fundoscopyComplete: LiveData<Resource<ECG>>? = Transformations
        .switchMap(_participantIdComplte) { participantId ->
            if (participantId == null) {
                AbsentLiveData.create()
            } else {
                fundoscopyRepository.syncFundoscopy(participantId, comment, device_id,pupil_dilation,isOnline,cataractObservation!!)
            }
        }

    fun setParticipant(participantId: ParticipantRequest, mComment: String?, mDevice_id: String,dilation: Boolean,observation : String) {
        comment = mComment
        device_id = mDevice_id
        pupil_dilation = dilation
        cataractObservation = observation

        if (_participantId.value == participantId) {
            return
        }
        _participantId.value = participantId
    }

    fun setParticipantComplete(participantId: ParticipantRequest, mComment: String?, mDevice_id: String,dilation: Boolean,online: Boolean,observation : String) {
        comment = mComment
        device_id = mDevice_id
        pupil_dilation = dilation
        isOnline = online
        cataractObservation = observation

        if (_participantIdComplte.value == participantId) {
            return
        }
        _participantIdComplte.value = participantId
    }

    //    get participant request ---------------------------------------------------------------------------------

    private val _screeningId: MutableLiveData<String> = MutableLiveData()

    var participant: LiveData<Resource<ResourceData<ParticipantRequest>>> = Transformations
        .switchMap(_screeningId) { screeningId ->
            if (screeningId == null) {
                AbsentLiveData.create()
            } else {
                participantRepository.getParticipantRequest(screeningId, "fundoscopy")
            }
        }

    fun setScreeningId(screeningId: String?) {
        if (_screeningId.value == screeningId) {
            return
        }
        _screeningId.value = screeningId
    }

//    ---------------------------------------------------------------------------------------------------------

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
}
