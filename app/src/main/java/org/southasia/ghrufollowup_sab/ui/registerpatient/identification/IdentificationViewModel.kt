package org.southasia.ghrufollowup_sab.ui.registerpatient.identification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.southasia.ghrufollowup_sab.repository.ParticipantRepository
import org.southasia.ghrufollowup_sab.util.AbsentLiveData
import org.southasia.ghrufollowup_sab.vo.Resource
import org.southasia.ghrufollowup_sab.vo.request.ParticipantRequest
import javax.inject.Inject


class IdentificationViewModel
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
