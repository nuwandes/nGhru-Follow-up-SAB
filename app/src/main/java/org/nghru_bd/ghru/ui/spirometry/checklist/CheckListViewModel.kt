package org.nghru_bd.ghru.ui.spirometry.checklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_bd.ghru.repository.ParticipantMetaRepository
import org.nghru_bd.ghru.repository.UserRepository
import org.nghru_bd.ghru.util.AbsentLiveData
import org.nghru_bd.ghru.vo.Resource
import org.nghru_bd.ghru.vo.User
import org.nghru_bd.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class CheckListViewModel
@Inject constructor(userRepository: UserRepository, participantMetaRepository: ParticipantMetaRepository) :
    ViewModel() {

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

    val participantMetas: LiveData<Resource<List<ParticipantRequest>>>? = Transformations
        .switchMap(_email) { emailx ->
            if (emailx == null) {
                AbsentLiveData.create()
            } else {
                participantMetaRepository.syncParticipantMetas()
            }
        }





}
