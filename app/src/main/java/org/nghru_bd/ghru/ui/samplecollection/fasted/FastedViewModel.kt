package org.nghru_bd.ghru.ui.samplecollection.fasted

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_bd.ghru.repository.ParticipantRepository
import org.nghru_bd.ghru.repository.UserRepository
import org.nghru_bd.ghru.util.AbsentLiveData
import org.nghru_bd.ghru.vo.Resource
import org.nghru_bd.ghru.vo.ResourceData
import org.nghru_bd.ghru.vo.User
import org.nghru_bd.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class FastedViewModel
@Inject constructor(
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

//    get participant request ------------------------------------------------------------------------------------

    private val _screeningId: MutableLiveData<String> = MutableLiveData()

    var participant: LiveData<Resource<ResourceData<ParticipantRequest>>> = Transformations
        .switchMap(_screeningId) { screeningId ->
            if (screeningId == null) {
                AbsentLiveData.create()
            } else {
                participantRepository.getParticipantRequest(screeningId, "sample")
            }
        }

    fun setScreeningId(screeningId: String?) {
        if (_screeningId.value == screeningId) {
            return
        }
        _screeningId.value = screeningId
    }

}
