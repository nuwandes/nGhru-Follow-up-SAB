package org.nghru_ins.ghru.ui.samplemanagement.bloodtesthome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_ins.ghru.repository.*
import org.nghru_ins.ghru.util.AbsentLiveData
import org.nghru_ins.ghru.vo.*
import org.nghru_ins.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class BloodTestHomeViewModel
@Inject constructor(participantRepository: ParticipantRepository,
                    userRepository: UserRepository,
                    bloodTestRepository: BloodTestRepository) :
    ViewModel() {

    //    get participant request ------------------------------------------------------------------------------------

    private val _screeningId: MutableLiveData<String> = MutableLiveData()

    var participant: LiveData<Resource<ResourceData<ParticipantRequest>>> = Transformations
        .switchMap(_screeningId) { screeningId ->
            if (screeningId == null) {
                AbsentLiveData.create()
            } else {
                participantRepository.getParticipantRequest(screeningId, "blood-test")
            }
        }

    fun setScreeningId(screeningId: String?) {
        if (_screeningId.value == screeningId) {
            return
        }
        _screeningId.value = screeningId
    }

    // get user data ----------------------------------------------------------------------------------------------------

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

    // to post blood Test data -------------------------------------------------------------------------------------------

    private val _bloodRequest: MutableLiveData<BloodTestRequest> = MutableLiveData()
    private var _participant: String? = null

    fun setBloodRequest(bloodRequest: BloodTestRequest, participantId: String?) {
        _participant = participantId
        if (_bloodRequest.value == bloodRequest) {
            return
        }
        _bloodRequest.value = bloodRequest
    }

    var syncBloodRequest: LiveData<Resource<ResourceData<Message>>>? = Transformations
        .switchMap(_bloodRequest) { cogPostRequest ->
            if (cogPostRequest == null) {
                AbsentLiveData.create()
            } else {
                bloodTestRepository.syncBloodTest(cogPostRequest,_participant!!)
            }
        }

    // ----------------------------------------------------------------------------

}
