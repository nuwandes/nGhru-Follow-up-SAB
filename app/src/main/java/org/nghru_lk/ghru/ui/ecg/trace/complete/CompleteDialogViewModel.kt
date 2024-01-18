package org.nghru_lk.ghru.ui.ecg.trace.complete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_lk.ghru.repository.ECGRepository
import org.nghru_lk.ghru.repository.UserRepository
import org.nghru_lk.ghru.util.AbsentLiveData
import org.nghru_lk.ghru.vo.ECG
import org.nghru_lk.ghru.vo.ECGStatus
import org.nghru_lk.ghru.vo.Resource
import org.nghru_lk.ghru.vo.User
import org.nghru_lk.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class CompleteDialogViewModel
@Inject constructor(eCGRepository: ECGRepository,
                    userRepository: UserRepository
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
}
