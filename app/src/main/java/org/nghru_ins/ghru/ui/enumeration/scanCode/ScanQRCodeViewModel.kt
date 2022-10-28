package org.nghru_ins.ghru.ui.enumeration.scanCode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_ins.ghru.repository.HouseholdRequestRepository
import org.nghru_ins.ghru.util.AbsentLiveData
import org.nghru_ins.ghru.vo.Resource
import org.nghru_ins.ghru.vo.request.HouseholdRequestMeta
import javax.inject.Inject


class ScanQRCodeViewModel
@Inject constructor(householdRequestRepository: HouseholdRequestRepository) : ViewModel() {

    private val _invitationId: MutableLiveData<String> = MutableLiveData()

    var householdRequestCheck: LiveData<Resource<HouseholdRequestMeta>>? = Transformations
        .switchMap(_invitationId) { householdRequest ->
            if (householdRequest == null) {
                AbsentLiveData.create()
            } else {
                householdRequestRepository.getItemId(householdRequest)
            }
        }

    fun getItemId(screeningId: String?) {
        _invitationId.value = screeningId
    }
}