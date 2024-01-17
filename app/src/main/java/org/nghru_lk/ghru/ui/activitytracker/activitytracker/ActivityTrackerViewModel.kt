package org.nghru_lk.ghru.ui.activitytracker.activitytracker


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_lk.ghru.repository.AssertRepository
import org.nghru_lk.ghru.repository.AxivityRepository
import org.nghru_lk.ghru.repository.ParticipantRepository
import org.nghru_lk.ghru.repository.UserRepository
import org.nghru_lk.ghru.util.AbsentLiveData
import org.nghru_lk.ghru.vo.*
import org.nghru_lk.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class ActivityTackeViewModel @Inject constructor(
    val assertRepository: AssertRepository,
    axivityRepository: AxivityRepository,
    userRepository: UserRepository,
    participantRepository: ParticipantRepository
) : ViewModel() {
    var activitytackerSyncError: MutableLiveData<Boolean>? = MutableLiveData<Boolean>().apply { }


    private val _participantId: MutableLiveData<ParticipantRequest> = MutableLiveData()

    private val _participantIdComplte: MutableLiveData<ParticipantRequest> = MutableLiveData()

    var isChecked: Boolean = false

    private var comment: String? = null


    fun setHasExplained(explained: Boolean) {
        isChecked = explained
    }

    private val _axivityId: MutableLiveData<AxivityId> = MutableLiveData()


    var asserts: LiveData<Resource<ResourceData<List<Asset>>>>? = Transformations
        .switchMap(_participantId) { participantId ->
            if (participantId == null) {
                AbsentLiveData.create()
            } else {
                assertRepository.getAssets(participantId, "activitytacker")
            }
        }

//    var activitytackerComplete: LiveData<Resource<ResourceData<ECG>>>? = Transformations
//            .switchMap(_participantIdComplte) { participantId ->
//                if (participantId == null) {
//                    AbsentLiveData.create()
//                } else {
//                    activitytackerRepository.syncFundoscopy(participantId, comment)
//                }
//            }

    var axivitySync: LiveData<Resource<Message>>? = Transformations
        .switchMap(_axivityId) { input ->
            input.ifExists { axivity, screeningId ->
                axivityRepository.syncAxivity(screeningId = screeningId!!, axivity = axivity!! )
            }
        }

    fun setParticipant(participantId: ParticipantRequest) {
        if (_participantId.value == participantId) {
            return
        }
        _participantId.value = participantId
    }

    fun setAxivity(screeningId: String?, axivity: Axivity) {
        val update = AxivityId(axivity = axivity, screeningId = screeningId)
        if (_axivityId.value == update) {
            return
        }
        _axivityId.value = update
    }

    data class AxivityId(val axivity: Axivity?, val screeningId: String?) {

        fun <T> ifExists(f: (Axivity?, String?) -> LiveData<T>): LiveData<T> {
            return if (axivity == null && screeningId == null) {
                AbsentLiveData.create()
            } else {
//                axivity!!.comment="COMMENT"
//                axivity!!.startTime="START_TIME"
//                axivity!!.endTime="END_TIME"
//                axivity!!.meta!!.collectedBy = "COLLECTED_BY"
//                axivity!!.meta!!.startTime = "META_START_TIME"
//                axivity!!.meta!!.endTime = "META_END_TIME"
                f(axivity, screeningId)
            }
        }
    }

    // Add Axivity Tracker to the Measurement List -------------------------------------

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

    //------------------------------------------------------------------------------------

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
