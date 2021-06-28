package org.southasia.ghrufollowup_sab.ui.enumeration.createhousehold

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.southasia.ghrufollowup_sab.jobs.SyncHouseholdMemberJob
import org.southasia.ghrufollowup_sab.repository.HouseholdRequestRepository
import org.southasia.ghrufollowup_sab.repository.MembersRepository
import org.southasia.ghrufollowup_sab.util.AbsentLiveData
import org.southasia.ghrufollowup_sab.vo.Resource
import org.southasia.ghrufollowup_sab.vo.ResourceData
import org.southasia.ghrufollowup_sab.vo.ResponceData
import org.southasia.ghrufollowup_sab.vo.request.HouseholdRequestMeta
import org.southasia.ghrufollowup_sab.vo.request.Member
import java.util.*
import javax.inject.Inject


class CreateHouseholdViewModel
@Inject constructor(membersRepository: MembersRepository, householdRequestRepository: HouseholdRequestRepository) :
    ViewModel() {

    private val _token: MutableLiveData<String> = MutableLiveData()
    val token: LiveData<String>
        get() = _token

    private val _householdRequestSync: MutableLiveData<HouseholdRequestMeta> = MutableLiveData()


    private val _householdRequest: MutableLiveData<HouseholdRequestMeta> = MutableLiveData()

    private val _householdRequestDelete: MutableLiveData<HouseholdRequestMeta> = MutableLiveData()

    private val _memberId: MutableLiveData<MemberId> = MutableLiveData()

    private val _memberIdRemote: MutableLiveData<MemberId> = MutableLiveData()


    var householdRequestLocal: LiveData<Resource<HouseholdRequestMeta>>? = Transformations
        .switchMap(_householdRequest) { householdRequest ->
            if (householdRequest == null) {
                AbsentLiveData.create()
            } else {
                householdRequestRepository.insertHouseholdRequest(householdRequest)
            }
        }

    var householdRequestDelete: LiveData<Resource<HouseholdRequestMeta>>? = Transformations
        .switchMap(_householdRequestDelete) { householdRequest ->
            if (householdRequest == null) {
                AbsentLiveData.create()
            } else {
                householdRequestRepository.delete(householdRequest)
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

    var memberSyncRemote: LiveData<Resource<ResourceData<List<SyncHouseholdMemberJob.MemberDTO>>>>? = Transformations
        .switchMap(_memberIdRemote) { member ->
            member.ifExists { memberX, participantRequest ->
                membersRepository.syncMembers(memberX, participantRequest)
            }

        }


    var memberLocal: LiveData<Resource<List<Member>>>? = Transformations
        .switchMap(_memberId) { member ->
            member.ifExists { memberX, participantRequest ->
                membersRepository.insertMembers(memberX!!, participantRequest)
            }
        }


    fun setHouseholdRequestLocal(household: HouseholdRequestMeta) {
        if (_householdRequest.value != household) {
            _householdRequest.postValue(household)
        }
    }

    fun setHouseholdRequestSyncRemote(household: HouseholdRequestMeta) {
        if (_householdRequestSync.value != household) {
            _householdRequestSync.postValue(household)
        }
    }


    fun setDelete(household: HouseholdRequestMeta) {
        if (_householdRequestDelete.value != household) {
            _householdRequestDelete.postValue(household)
        }
    }

    fun setMemberSyncedLocal(members: ArrayList<Member>?, household: HouseholdRequestMeta) {
        val memberId = MemberId(members!!, household)
        if (_memberId.value == memberId) {
            return
        }
        _memberId.value = memberId
    }


    fun setMemberSyncedRemote(members: ArrayList<Member>, household: HouseholdRequestMeta) {
        val memberId = MemberId(members, household)
        if (_memberIdRemote.value == memberId) {
            return
        }
        _memberIdRemote.value = memberId
    }


    data class MemberId(val members: ArrayList<Member>?, val householdRequest: HouseholdRequestMeta?) {
        fun <T> ifExists(f: (ArrayList<Member>?, HouseholdRequestMeta) -> LiveData<T>): LiveData<T> {
            return if (members == null || householdRequest == null) {
                AbsentLiveData.create()
            } else {
                f(members, householdRequest)
            }
        }
    }

}
