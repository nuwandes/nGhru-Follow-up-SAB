package org.nghru_lk.ghru.ui.covid.confirmation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_lk.ghru.repository.CovidRepository
import org.nghru_lk.ghru.repository.ParticipantRepository
import org.nghru_lk.ghru.repository.UserRepository
import org.nghru_lk.ghru.util.AbsentLiveData
import org.nghru_lk.ghru.vo.*
import org.nghru_lk.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class CovidConfirmationViewModel
@Inject constructor(
    covidRepository: CovidRepository,
    userRepository: UserRepository,
    participantRepository: ParticipantRepository
) : ViewModel() {

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

    val haveStaff = MutableLiveData<Boolean>()

    fun setHaveStaff(item: Boolean) {
        haveStaff.value = item
    }

    val haveAssistance = MutableLiveData<Boolean>()

    fun setHaveAssistance(item: Boolean) {
        haveAssistance.value = item
    }

    // ----------------------------- update FFQ -----------------------------------

    private val _cogUpdateRequest: MutableLiveData<CovidRequestNew> = MutableLiveData()
    private var _participantId: String? = null

    fun setUpdateCog(cogRequest: CovidRequestNew, participantId: String?) {
        _participantId = participantId
        if (_cogUpdateRequest.value == cogRequest) {
            return
        }
        _cogUpdateRequest.value = cogRequest
    }

    var cogUpdateComplete:LiveData<Resource<ResourceData<Message>>>? = Transformations
        .switchMap(_cogUpdateRequest) { cogRequest ->
            if (cogRequest == null) {
                AbsentLiveData.create()
            } else {
                covidRepository.updateCovid(cogRequest,_participantId!!)
            }
        }

    // ----------------------------------------------------------------------------

    //    get participant request ------------------------------------------------------------------------------------

    private val _screeningIdNew: MutableLiveData<String> = MutableLiveData()

    var participant: LiveData<Resource<ResourceData<ParticipantRequest>>> = Transformations
        .switchMap(_screeningIdNew) { screeningId ->
            if (screeningId == null) {
                AbsentLiveData.create()
            } else {
                participantRepository.getParticipantRequest(screeningId, "covid-questionnaire")
            }
        }

    fun setScreeningId(screeningId: String?) {
        if (_screeningIdNew.value == screeningId) {
            return
        }
        _screeningIdNew.value = screeningId
    }
}
