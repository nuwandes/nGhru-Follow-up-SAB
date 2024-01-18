package org.nghru_lk.ghru.ui.samplemanagement.bloodtesthome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_lk.ghru.repository.*
import org.nghru_lk.ghru.util.AbsentLiveData
import org.nghru_lk.ghru.vo.*
import org.nghru_lk.ghru.vo.request.ParticipantRequest
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

    var syncBloodRequest: LiveData<Resource<ECG>>? = Transformations
        .switchMap(_bloodRequest) { cogPostRequest ->
            if (cogPostRequest == null) {
                AbsentLiveData.create()
            } else {
                bloodTestRepository.syncBloodTest(cogPostRequest)
            }
        }

    // ----------------------------------------------------------------------------

    private val _bTLocal: MutableLiveData<BloodTestRequest> = MutableLiveData()

    fun setBTLocal(bloodRequest: BloodTestRequest) {
        if (_bTLocal.value != bloodRequest) {
            _bTLocal.postValue(bloodRequest)
        }
    }

    var insertBTLocal: LiveData<Resource<BloodTestRequest>>? = Transformations
        .switchMap(_bTLocal) { bTLocal ->
            if (bTLocal == null) {
                AbsentLiveData.create()
            } else {
                bloodTestRepository.insertBloodTest(bTLocal)
            }
        }

}
