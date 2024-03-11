package org.nghru_ins.ghru.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_ins.ghru.repository.AccessTokenRepository
import org.nghru_ins.ghru.repository.ParticipantListRepository
import org.nghru_ins.ghru.repository.StationDevicesRepository
import org.nghru_ins.ghru.util.AbsentLiveData
import org.nghru_ins.ghru.vo.*
import org.nghru_ins.ghru.vo.request.ParticipantListWithMeta
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

    var getAllParticipantsOffline: LiveData<Resource<ResourceData<ParticipantListWithMeta>>>? = Transformations
        .switchMap(_filterId) { input ->
            input.ifExists { page, status, site, keyWord ->
                repository.getAllParticipantListItemsOffline(page!!, status!!.toString(), site!!.toString(), keyWord!!.toString(), true)

            }
        }

    fun setOfflineAllParticipants(page: Int, status: String, site: String, keyWord: String) {
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

    // -------------------------------------------------------------------------------------------------

    private val _siteId: MutableLiveData<SiteId> = MutableLiveData()

    var insertSites: LiveData<Resource<Site>>? = Transformations
        .switchMap(_siteId) { input ->
            input.ifExists { id ->
                repository.insertSites(id!!)

            }
        }

    fun setSiteIdToInsert(id: Site) {
        val update =
            SiteId(id = id)
        if (_siteId.value == update) {
            return
        }
        _siteId.value = update
    }

    data class SiteId(val id: Site?) {

        fun <T> ifExists(f: (Site?) -> LiveData<T>): LiveData<T> {
            return if (id == null) {
                AbsentLiveData.create()
            } else {
                f(id)
            }
        }
    }

    // -------------------------------------------------------------------------------------

    private val _siteIdApi: MutableLiveData<SiteIdAPI> = MutableLiveData()

    var getApiSites: LiveData<Resource<Site>>? = Transformations
        .switchMap(_siteIdApi) { input ->
            input.ifExists { id ->
                repository.insertSites(id!!)

            }
        }

    fun setSiteIdApi(id: Site) {
        val update =
            SiteIdAPI(id = id)
        if (_siteIdApi.value == update) {
            return
        }
        _siteIdApi.value = update
    }

    data class SiteIdAPI(val id: Site?) {

        fun <T> ifExists(f: (Site?) -> LiveData<T>): LiveData<T> {
            return if (id == null) {
                AbsentLiveData.create()
            } else {
                f(id)
            }
        }
    }

    // ------------------------------- Site data for Spinner --------------------------------

    private val _siteSpinnerId: MutableLiveData<SiteSpinnerId> = MutableLiveData()

    var getSiteSpinnerItems: LiveData<Resource<ResourceData<Array<String>>>>? = Transformations
        .switchMap(_siteSpinnerId) { input ->
            input.ifExists { id ->
                repository.getSiteSpinnerItems(id!!)

            }
        }

    fun setSiteSpinnerId(id: String) {
        val update =
            SiteSpinnerId(id = id)
        if (_siteSpinnerId.value == update) {
            return
        }
        _siteSpinnerId.value = update
    }

    data class SiteSpinnerId(val id: String?) {

        fun <T> ifExists(f: (String?) -> LiveData<T>): LiveData<T> {
            return if (id == null) {
                AbsentLiveData.create()
            } else {
                f(id)
            }
        }
    }

    // --------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------------------

    private val _storageId = MutableLiveData<String>()

    fun setStorageIds(storageId: String) {
        val update = storageId
        if (_storageId.value == update) {
            return
        }
        _storageId.value = update
    }

    var getStorageIds: LiveData<Resource<ResourceData<List<StorageIdData>>>>? = Transformations
        .switchMap(_storageId) { input ->
            stationDevicesRepository.getStorageIds()
        }

    //---------------------------------------------------------------------------------------------------------

    private val _storageIdList = MutableLiveData<List<StorageIdData>>()

    fun setStorageIdList(storageList: List<StorageIdData>) {
        val update = storageList
        if (_storageIdList.value == update) {
            return
        }
        _storageIdList.value = update
    }

    var getStorageIdList: LiveData<Resource<List<StorageIdData>>>? = Transformations
        .switchMap(_storageIdList) { input ->
            stationDevicesRepository.insertStorageIdList(_storageIdList.value!!)
        }

    // -------------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------------------

    private val _sampleId = MutableLiveData<String>()

    fun setSampleIds(sampleId: String) {
        val update = sampleId
        if (_sampleId.value == update) {
            return
        }
        _sampleId.value = update
    }

    var getSampleIds: LiveData<Resource<ResourceData<List<SampleIdData>>>>? = Transformations
        .switchMap(_sampleId) { input ->
            stationDevicesRepository.getSampleIds()
        }

    //---------------------------------------------------------------------------------------------------------

    private val _sampleIdList = MutableLiveData<List<SampleIdData>>()

    fun setSampleIdList(sampleList: List<SampleIdData>) {
        val update = sampleList
        if (_sampleIdList.value == update) {
            return
        }
        _sampleIdList.value = update
    }

    var getSampleIdList: LiveData<Resource<List<SampleIdData>>>? = Transformations
        .switchMap(_sampleIdList) { input ->
            stationDevicesRepository.insertSampleIdList(_sampleIdList.value!!)
        }

    // -------------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------------------

    private val _freezerId = MutableLiveData<String>()

    fun setFreezerIds(freezerId: String) {
        val update = freezerId
        if (_freezerId.value == update) {
            return
        }
        _freezerId.value = update
    }

    var getFreezerIds: LiveData<Resource<ResourceData<List<FreezerIdData>>>>? = Transformations
        .switchMap(_freezerId) { input ->
            stationDevicesRepository.getFreezerIds()
        }

    //---------------------------------------------------------------------------------------------------------

    private val _freezerIdList = MutableLiveData<List<FreezerIdData>>()

    fun setFreezerIdList(freezerIdList: List<FreezerIdData>) {
        val update = freezerIdList
        if (_freezerIdList.value == update) {
            return
        }
        _freezerIdList.value = update
    }

    var getFreezerIdList: LiveData<Resource<List<FreezerIdData>>>? = Transformations
        .switchMap(_freezerIdList) { input ->
            stationDevicesRepository.insertFreezerIdList(_freezerIdList.value!!)
        }

    // -------------------------------------------------------------------------------------------------
}
