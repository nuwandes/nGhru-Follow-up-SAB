package org.nghru_hpv.ghru.ui.participantlist.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_hpv.ghru.repository.AssertRepository
import org.nghru_hpv.ghru.repository.ParticipantListRepository
import org.nghru_hpv.ghru.repository.UserRepository
import org.nghru_hpv.ghru.util.AbsentLiveData
import org.nghru_hpv.ghru.vo.*
import org.nghru_hpv.ghru.vo.request.Gender
import javax.inject.Inject

class ParticipantAttendanceViewModule
@Inject constructor(userRepository: UserRepository,
                    repository: ParticipantListRepository,
                    assetRepository: AssertRepository) : ViewModel() {

    var gender: MutableLiveData<String> = MutableLiveData<String>()

    var birthYear: Int = 1998

    var birthDate: MutableLiveData<String> = MutableLiveData<String>()

    var birthDateVal: MutableLiveData<Date> = MutableLiveData<Date>()

    var contactNo: MutableLiveData<String> = MutableLiveData<String>()

    var age: MutableLiveData<String> = MutableLiveData<String>()


    fun setGender(g: Gender) {
        gender.postValue(g.gender)
    }

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

    //    update single participant ------------------------------------------------------------------------------

    private val _participantUpdateRequest: MutableLiveData<ParticipantListItem> = MutableLiveData()
    private var _participantId: String? = null
    private val _participantRequest: MutableLiveData<ParticipantListItem> = MutableLiveData()

    var participantUpdateComplete:LiveData<Resource<ResourceData<ParticipantListItem>>>? = Transformations
        .switchMap(_participantUpdateRequest) { participantRequest ->
            if (participantRequest == null) {
                AbsentLiveData.create()
            } else {
                repository.updateParticipantItem(participantRequest,_participantId!!)
            }
        }

    fun setParticipant(participantItem: ParticipantListItem, participantId: String?) {
        _participantId = participantId
        if (_participantRequest.value == participantItem) {
            return
        }
        _participantRequest.value = participantItem
    }
    fun updateParticipant(participantItem: ParticipantListItem, participantId: String?) {
        _participantId = participantId
        if (_participantUpdateRequest.value == participantItem) {
            return
        }
        _participantUpdateRequest.value = participantItem
    }

//    --------------------------------------------------------------------------------------------------------

    // ------------- to get the assets status ----------------------------------------------------------------

    private val _participantRequestId: MutableLiveData<String> = MutableLiveData()

    fun setParticipantForGetAsset(participantId: String) {

        if (_participantRequestId.value == participantId) {
            return
        }
        _participantRequestId.value = participantId
    }

    var getAssets: LiveData<Resource<ResourceData<List<Asset>>>>? = Transformations
        .switchMap(_participantRequestId) { participantId ->
            if (participantId == null) {
                AbsentLiveData.create()
            } else {
                assetRepository.getConsentAssets(participantId, "consent")
            }
        }
}
