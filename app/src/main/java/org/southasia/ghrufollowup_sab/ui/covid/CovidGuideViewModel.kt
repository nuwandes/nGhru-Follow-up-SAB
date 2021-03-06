package org.southasia.ghrufollowup_sab.ui.covid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.southasia.ghrufollowup_sab.repository.CovidRepository
import org.southasia.ghrufollowup_sab.repository.ParticipantRepository
import org.southasia.ghrufollowup_sab.repository.UserRepository
import org.southasia.ghrufollowup_sab.util.AbsentLiveData
import org.southasia.ghrufollowup_sab.vo.*
import org.southasia.ghrufollowup_sab.vo.request.ParticipantRequest
import javax.inject.Inject

class CovidGuideViewModel
@Inject constructor(userRepository: UserRepository,
                    covidRepository: CovidRepository,
                    participantRepository: ParticipantRepository) : ViewModel() {

    private val _screeningId: MutableLiveData<String> = MutableLiveData()
    val screeningId: LiveData<String>
        get() = _screeningId

    // ----------------------------- post Cognition-----------------------------------

    private val _cogPostRequest: MutableLiveData<CovidRequest> = MutableLiveData()
    private var _participantId: String? = null

    fun setPostCog(cogRequest: CovidRequest, participantId: String?) {
        _participantId = participantId
        if (_cogPostRequest.value == cogRequest) {
            return
        }
        _cogPostRequest.value = cogRequest
    }

    var cogPostComplete:LiveData<Resource<ResourceData<Message>>>? = Transformations
        .switchMap(_cogPostRequest) { cogPostRequest ->
            if (cogPostRequest == null) {
                AbsentLiveData.create()
            } else {
                covidRepository.syncCovidNew(cogPostRequest,_participantId!!)
            }
        }

    // ----------------------------------------------------------------------------

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
