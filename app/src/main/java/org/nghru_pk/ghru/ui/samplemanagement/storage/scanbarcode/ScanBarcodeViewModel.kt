package org.nghru_pk.ghru.ui.samplemanagement.storage.scanbarcode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_pk.ghru.repository.SampleRequestRepository
import org.nghru_pk.ghru.repository.UserRepository
import org.nghru_pk.ghru.util.AbsentLiveData
import org.nghru_pk.ghru.vo.Resource
import org.nghru_pk.ghru.vo.User
import org.nghru_pk.ghru.vo.request.SampleRequest
import javax.inject.Inject


class ScanBarcodeViewModel
@Inject constructor(sampleRequestRepository: SampleRequestRepository, userRepository: UserRepository) : ViewModel() {

    private val _sampleId: MutableLiveData<String> = MutableLiveData()

    private val _sampleIdOffline: MutableLiveData<String> = MutableLiveData()

    var sampleOffline: LiveData<Resource<SampleRequest>>? = Transformations
            .switchMap(_sampleIdOffline) { sampleIdOffline ->
                if (sampleIdOffline == null) {
                    AbsentLiveData.create()
                } else {
                    sampleRequestRepository.getSampleRequestByStorageIDOfflineByStorageID(sampleIdOffline)
                }
            }

    fun setSampleIdOffline(sampleIdOffline: String?) {
        if (_sampleIdOffline.value == sampleIdOffline) {
            return
        }
        _sampleIdOffline.value = sampleIdOffline
    }


    fun setSampleId(sampleId: String?) {
        if (_sampleId.value == sampleId) {
            return
        }
        _sampleId.value = sampleId
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
}
