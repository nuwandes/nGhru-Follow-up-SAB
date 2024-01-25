package org.nghru_pk.ghru.ui.participantlist.attendance.consent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_pk.ghru.repository.AssertRepository
import org.nghru_pk.ghru.util.AbsentLiveData
import org.nghru_pk.ghru.vo.Message
import org.nghru_pk.ghru.vo.Resource
import javax.inject.Inject


class ConsentViewModel
@Inject constructor(
    assetRepository: AssertRepository
) : ViewModel() {

    private val _uploadConsent: MutableLiveData<UploadConsentId> = MutableLiveData()

    fun setUploadConcent(consentPhoto: String?, screeningId: String?) {
        val update = UploadConsentId(consentPhoto, screeningId)
        if (_uploadConsent.value == update) {
            return
        }
        _uploadConsent.value = update
    }

    var uploadConsent: LiveData<Resource<Message>>? = Transformations
        .switchMap(_uploadConsent) { upload ->
            upload.ifExists { consentPhoto, screeningId ->
                assetRepository.uploadConcentNew(consentPhoto, screeningId)
            }
        }

    data class UploadConsentId(val consentPhoto: String?, val screeningId: String?) {
        fun <T> ifExists(f: (String, String) -> LiveData<T>): LiveData<T> {
            return if (consentPhoto == null || screeningId == null) {
                AbsentLiveData.create()
            } else {
                f(consentPhoto, screeningId)
            }
        }
    }
}
