package org.nghru_bd.ghru.ui.ecg.trace.complete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_bd.ghru.repository.ECGRepository
import org.nghru_bd.ghru.repository.ParticipantListRepository
import org.nghru_bd.ghru.repository.UserRepository
import org.nghru_bd.ghru.util.AbsentLiveData
import org.nghru_bd.ghru.vo.*
import javax.inject.Inject


class CompleteDialogViewModel
@Inject constructor(eCGRepository: ECGRepository,
                    userRepository: UserRepository,
                    participantListRepository: ParticipantListRepository
) : ViewModel() {

    private val _participantRequestRemote: MutableLiveData<ECGId> = MutableLiveData()
    private var isOnline : Boolean = false

//    var eCGSaveRemote: LiveData<Resource<ResourceData<ECG>>>? = Transformations
//            .switchMap(_participantRequestRemote) { participant ->
//                if (participant == null) {
//                    AbsentLiveData.create()
//                } else {
//                    eCGRepository.syncECG(participant)
//                }
//            }

    var eCGSaveRemote: LiveData<Resource<ECG>>? = Transformations
        .switchMap(_participantRequestRemote) { input ->
            input.ifExists { screeningId, ecgStatus ->
                eCGRepository.syncECG(screeningId, ecgStatus, isOnline)
            }
        }

    fun setECGRemote(screeningId: String?, ecgStatus: ECGStatus?, online : Boolean) {
        isOnline = online
        val update = ECGId(screeningId, ecgStatus)
        if (_participantRequestRemote.value == update) {
            return
        }
        _participantRequestRemote.value = update
//        if (_participantRequestRemote.value != participantRequest) {
//            _participantRequestRemote.postValue(participantRequest)
//        }
    }

    data class ECGId(
        val screeningId: String?,
        val ecgStatus: ECGStatus?
    ) {
        fun <T> ifExists(f: (String, ECGStatus) -> LiveData<T>): LiveData<T> {
            return if (screeningId == null || ecgStatus == null) {
                AbsentLiveData.create()
            } else {
                f(screeningId, ecgStatus)
            }
        }
    }

    //    ---------------------------------------------------------------------------------------------------------

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

    // ----------------------------------------------------------------------------------------------------

    private val _ecgLocal: MutableLiveData<ECGStatus> = MutableLiveData()

    fun setEcgLocal(ecgStatus: ECGStatus?) {
        if (_ecgLocal.value != ecgStatus) {
            _ecgLocal.postValue(ecgStatus)
        }
    }

    var insertEcgLocal: LiveData<Resource<ECGStatus>>? = Transformations
        .switchMap(_ecgLocal) { ecgLocal ->
            if (ecgLocal == null) {
                AbsentLiveData.create()
            } else {
                eCGRepository.insertEcg(ecgLocal)
            }
        }

    // ------------------------------------------------------------------------------------------------------

    private val _localParticipantUpdateRequest: MutableLiveData<ParticipantListItem> = MutableLiveData()

    fun setLocalUpdateParticipantEcgStatus(participantItem: ParticipantListItem) {
        if (_localParticipantUpdateRequest.value == participantItem) {
            return
        }
        _localParticipantUpdateRequest.value = participantItem
    }

    var getLocalUpdateParticipantEcgStatus:LiveData<Resource<ParticipantListItem>>? = Transformations
        .switchMap(_localParticipantUpdateRequest) { participantRequest ->
            if (participantRequest == null) {
                AbsentLiveData.create()
            } else {
                participantListRepository.updateParticipantEcgStatus(participantRequest)
            }
        }
}
