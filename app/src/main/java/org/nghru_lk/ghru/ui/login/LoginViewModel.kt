package org.nghru_lk.ghru.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_lk.ghru.repository.AccessTokenRepository
import org.nghru_lk.ghru.repository.ParticipantListRepository
import org.nghru_lk.ghru.repository.StationDevicesRepository
import org.nghru_lk.ghru.util.AbsentLiveData
import org.nghru_lk.ghru.vo.*
import org.nghru_lk.ghru.vo.request.ParticipantListWithMeta
import javax.inject.Inject


class LoginViewModel
@Inject constructor(
    accessTokenRepository: AccessTokenRepository,
    stationDevicesRepository: StationDevicesRepository,
    repository: ParticipantListRepository
) : ViewModel() {


    private val _device = MutableLiveData<String>()

    val device: LiveData<String>
        get() = _device


    private val _loginId: MutableLiveData<LoginId> = MutableLiveData()


    private val _accessToken = MutableLiveData<AccessToken>()


    private val _email = MutableLiveData<String>()

    //private val _hemoDevice = MutableLiveData<String>()

    private var isOnline : Boolean = false

    val accessTokenOffline: LiveData<Resource<AccessToken>>? = Transformations
        .switchMap(_email) { email ->
            if (email == null) {
                AbsentLiveData.create()
            } else {
                accessTokenRepository.getTokerByEmail(email)
            }
        }

    fun setEmail(email: String) {
        val update = email
        if (_email.value == update) {
            return
        }
        _email.value = update
    }

    var accessToken: LiveData<Resource<AccessToken>>? = Transformations
        .switchMap(_loginId) { input ->
            input.ifExists { email, password ->
                accessTokenRepository.loginUser(email, password,isOnline)
            }
        }

    var refreshToken: LiveData<Resource<AccessToken>>? = Transformations
        .switchMap(_accessToken) { accessToken ->
            if (accessToken == null) {
                AbsentLiveData.create()
            } else {
                accessTokenRepository.refreshToken(accessToken)
            }
        }

    fun setRefreshToken(accessToken: AccessToken?) {
        if (_accessToken.value != accessToken) {
            _accessToken.value = accessToken
        }
    }


    fun setLogin(email: String?, password: String, online : Boolean) {
        isOnline = online
        val update = LoginId(email, password)
        if (_loginId.value == update) {
            return
        }
        _loginId.value = update
    }

    fun onError() {
        val update = LoginId("", "")
        _loginId.value = update
    }

    data class LoginId(val email: String?, val password: String?) {
        fun <T> ifExists(f: (String, String) -> LiveData<T>): LiveData<T> {
            return if (email.isNullOrBlank() || password.isNullOrBlank()) {
                AbsentLiveData.create()
            } else {
                f(email!!, password!!)
            }
        }

    }

    // Get All Participant data from API

    //    Get participants with search and filter ---------------------------------------------------------------

    private val _filterId: MutableLiveData<FilterId> = MutableLiveData()

    var filterparticipantListItems: LiveData<Resource<ResourceData<ParticipantListWithMeta>>>? = Transformations
        .switchMap(_filterId) { input ->
            input.ifExists { page, status, site, keyWord ->
                repository.filterParticipantListItems(page!!, status!!.toString(), site!!.toString(), keyWord!!.toString())

            }
        }

    fun setFilterId(page: Int, status: String, site: String, keyWord: String) {
        val update =
            FilterId(page = page, status = status, site = site, keyWord = keyWord)
        if (_filterId.value == update) {
            return
        }
        _filterId.value = update
    }

    data class FilterId(val page: Int?, val status: String?, val site: String?, val keyWord: String?) {

        fun <T> ifExists(f: (Int?, String?, String?, String?) -> LiveData<T>): LiveData<T> {
            return if (page == null || status == null || site == null || keyWord == null) {
                AbsentLiveData.create()
            } else {
                f(page, status, site, keyWord)
            }
        }
    }
//    --------------------------------------------------------------------------------------------------------

    private val _participantListItemList = MutableLiveData<List<ParticipantListItem>>()

    fun setParticipantListItemList(participantListItemList: List<ParticipantListItem>) {
        val update = participantListItemList
        if (_participantListItemList.value == update) {
            return
        }
        _participantListItemList.value = update
    }

    var getParticipantListItemList: LiveData<Resource<List<ParticipantListItem>>> = Transformations
        .switchMap(_participantListItemList) { input ->
            repository.insertParticipantListItemList(_participantListItemList.value!!)
        }

    // -------------------------------------------------------------------------------------------------------

    private val _stationDevice = MutableLiveData<String>()

    fun setStationDevice(stationDevice: String) {
        val update = stationDevice
        if (_stationDevice.value == update) {
            return
        }
        _stationDevice.value = update
    }

    var stationDevices: LiveData<Resource<ResourceData<List<StationDeviceData>>>>? = Transformations
        .switchMap(_stationDevice) { input ->
            stationDevicesRepository.loadStationDevices()
        }

    //---------------------------------------------------------------------------------------------------------

    private val _stationDeviceList = MutableLiveData<List<StationDeviceData>>()

    fun setStationDeviceList(stationDeviceList: List<StationDeviceData>) {
        val update = stationDeviceList
        if (_stationDeviceList.value == update) {
            return
        }
        _stationDeviceList.value = update
    }

    var stationDeviceList: LiveData<Resource<List<StationDeviceData>>>? = Transformations
        .switchMap(_stationDeviceList) { input ->
            stationDevicesRepository.insertStationDeviceList(_stationDeviceList.value!!)
        }
}
