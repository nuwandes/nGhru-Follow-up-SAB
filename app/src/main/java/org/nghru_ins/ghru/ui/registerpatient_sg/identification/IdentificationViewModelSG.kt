package org.nghru_ins.ghru.ui.registerpatient_sg.identification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_ins.ghru.repository.ParticipantRepository
import org.nghru_ins.ghru.util.AbsentLiveData
import org.nghru_ins.ghru.vo.Resource
import org.nghru_ins.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class IdentificationViewModelSG
@Inject constructor(participantRepository: ParticipantRepository) : ViewModel() {
    private val _idNumber: MutableLiveData<String> = MutableLiveData()

    var participant: LiveData<Resource<ParticipantRequest>>? = Transformations
        .switchMap(_idNumber) { idNumber ->
            if (idNumber == null) {
                AbsentLiveData.create()
            } else {
                participantRepository.getParticipantByIdnumber(idNumber)
            }
        }


    fun setIdNumber(idNumber: String?) {
        if (_idNumber.value == idNumber) {
            return
        }
        _idNumber.value = idNumber
    }
}
