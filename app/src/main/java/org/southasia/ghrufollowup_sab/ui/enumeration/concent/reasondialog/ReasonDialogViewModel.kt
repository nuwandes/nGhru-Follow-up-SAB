package org.southasia.ghrufollowup_sab.ui.enumeration.concent.reasondialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.southasia.ghrufollowup_sab.repository.HouseholdRequestRepository
import org.southasia.ghrufollowup_sab.util.AbsentLiveData
import org.southasia.ghrufollowup_sab.vo.Resource
import org.southasia.ghrufollowup_sab.vo.ResponceData
import org.southasia.ghrufollowup_sab.vo.request.HouseholdRequestMeta
import javax.inject.Inject


class ReasonDialogViewModel
@Inject constructor(householdRequestRepository: HouseholdRequestRepository) : ViewModel() {

    private val _householdRequest: MutableLiveData<HouseholdRequestMeta> = MutableLiveData()
    private val _householdRequestSync: MutableLiveData<HouseholdRequestMeta> = MutableLiveData()

    var householdSave: LiveData<Resource<HouseholdRequestMeta>>? = Transformations
        .switchMap(_householdRequest) { householdRequest ->
            if (householdRequest == null) {
                AbsentLiveData.create()
            } else {
                householdRequestRepository.insertHouseholdRequest(householdRequest)
            }
        }


    var householdRequestSyncRemote: LiveData<Resource<ResponceData>>? = Transformations
        .switchMap(_householdRequestSync) { householdRequest ->
            if (householdRequest == null) {
                AbsentLiveData.create()
            } else {
                householdRequestRepository.syncHousehold(householdRequest)
            }
        }

    fun setHouseholdRequest(householdRequest: HouseholdRequestMeta?) {
        if (_householdRequest.value != householdRequest) {
            _householdRequest.postValue(householdRequest)
        }
    }

    fun setHouseholdRequestSyncRemote(household: HouseholdRequestMeta) {
        if (_householdRequestSync.value != household) {
            _householdRequestSync.postValue(household)
        }
    }
}
