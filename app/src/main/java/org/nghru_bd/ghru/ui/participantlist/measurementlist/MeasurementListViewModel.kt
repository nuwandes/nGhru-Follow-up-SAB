package org.nghru_bd.ghru.ui.participantlist.measurementlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_bd.ghru.repository.BodyMeasurementMetaRepository
import org.nghru_bd.ghru.repository.ParticipantListRepository
import org.nghru_bd.ghru.util.AbsentLiveData
import org.nghru_bd.ghru.vo.*
import org.nghru_bd.ghru.vo.request.BodyMeasurementMeta
import javax.inject.Inject

class MeasurementListViewModel
@Inject constructor(repository: ParticipantListRepository,
                    bodyMeasurementMetaRepository: BodyMeasurementMetaRepository
) : ViewModel() {

//    Get single participant stations ------------------------------------------------------------------------

    private val _participantId: MutableLiveData<ParticipantId> = MutableLiveData()

    var getSingleParticipantStations: LiveData<Resource<StationData<List<ParticipantStation>>>>? = Transformations
        .switchMap(_participantId) { input ->
            input.ifExists { participant ->
                repository.getSingleParticipantStations(participant!!.toString())

            }
        }

    fun setParticipantId(participant: String) {
        val update =
            ParticipantId(participant = participant)
        if (_participantId.value == update) {
            return
        }
        _participantId.value = update
    }

    data class ParticipantId(val participant: String?) {

        fun <T> ifExists(f: (String?) -> LiveData<T>): LiveData<T> {
            return if (participant == null) {
                AbsentLiveData.create()
            } else {
                f(participant)
            }
        }
    }


//    --------------------------------------------------------------------------------------------------------

//    get measurement list items -----------------------------------------------------------------------------

//    private val _measurementList = MutableLiveData<String>()

    private val _stationList = MutableLiveData<List<ParticipantStation>>()

    val measurementListItem: LiveData<Resource<List<MeasurementListItem>>> = Transformations
        .switchMap(_stationList) { login ->
            if (login == null) {
                AbsentLiveData.create()
            } else {
                repository.getMeasurementListItems(login)
            }
        }

//    fun setMeasurementId(lang: String?) {
//        if (_measurementList.value != lang) {
//            _measurementList.value = lang
//        }
//    }

//    --------------------------------------------------------------------------------------------------------

//    set measurement list -----------------------------------------------------------------------------------



    val StationList: LiveData<Resource<List<ParticipantStation>>>? = Transformations
        .switchMap(_stationList) { search ->
            if (search == null) {
                AbsentLiveData.create()
            } else {
                repository.getStationList(search)
            }
        }

    fun setStationList(sList: List<ParticipantStation>?) {
        if (_stationList.value != sList) {
            _stationList.value = sList
        }
    }

//    --------------------------------------------------------------------------------------------------------

    //    update single participant ------------------------------------------------------------------------------

    private val _participantUpdateRequest: MutableLiveData<ParticipantListItem> = MutableLiveData()
    private var _participantID: String? = null
    private val _participantRequest: MutableLiveData<ParticipantListItem> = MutableLiveData()

//    var participantFollowUpStatusUpdate:LiveData<Resource<ResourceData<ParticipantListItem>>>? = Transformations
//        .switchMap(_participantUpdateRequest) { participantRequest ->
//            if (participantRequest == null) {
//                AbsentLiveData.create()
//            } else {
//                repository.updateParticipantItem(participantRequest,_participantID!!)
//            }
//        }

    fun setFollowUpParticipant(participantItem: ParticipantListItem, participantId: String?) {
        _participantID = participantId
        if (_participantRequest.value == participantItem) {
            return
        }
        _participantRequest.value = participantItem
    }

//    fun updateParticipantFollowUp(participantItem: ParticipantListItem, participantId: String?) {
//        _participantID = participantId
//        if (_participantUpdateRequest.value == participantItem) {
//            return
//        }
//        _participantUpdateRequest.value = participantItem
//    }

//    --------------------------------------------------------------------------------------------------------

    fun updateParticipantFollowUp(participantItem: ParticipantListItem) {
        if (_participantUpdateRequest.value == participantItem) {
            return
        }
        _participantUpdateRequest.value = participantItem
    }

    var participantFollowUpStatusUpdate:LiveData<Resource<ParticipantListItem>>? = Transformations
        .switchMap(_participantUpdateRequest) { participantRequest ->
            if (participantRequest == null) {
                AbsentLiveData.create()
            } else {
                repository.updateAndSyncParticipant(participantRequest)
            }
        }

    // -----------------------------------------------------------------

    private val _pId = MutableLiveData<String>()

    fun setToGetBPStatus(pId: String) {
        val update = pId
        if (_pId.value == update) {
            return
        }
        _pId.value = update
    }

    var isBPStationCompleted: LiveData<Resource<BodyMeasurementMeta>>? = Transformations
        .switchMap(_pId) { input ->
            bodyMeasurementMetaRepository.getBpStatus(input)
        }

    // ------------------------------------------------------------------------------------------------

    private val _screeningId = MutableLiveData<String>()

    fun setScreeningIdParticipantListItemsFromDb(status: String) {
        val update = status
        if (_screeningId.value == update) {
            return
        }
        _screeningId.value = update
    }

    var getScreeningIdParticipantListItemsFromDb: LiveData<Resource<ParticipantListItem>>? = Transformations
        .switchMap(_screeningId) { input ->
            repository.getSingleParticipantItem(input)
        }

}