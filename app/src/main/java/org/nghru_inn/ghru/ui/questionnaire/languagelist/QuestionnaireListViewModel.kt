package org.nghru_inn.ghru.ui.questionnaire.languagelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_inn.ghru.repository.ParticipantRepository
import org.nghru_inn.ghru.repository.QuestionnaireRepository
import org.nghru_inn.ghru.repository.UserRepository
import org.nghru_inn.ghru.util.AbsentLiveData
import org.nghru_inn.ghru.vo.Questionnaire
import org.nghru_inn.ghru.vo.Resource
import org.nghru_inn.ghru.vo.ResourceData
import org.nghru_inn.ghru.vo.User
import org.nghru_inn.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class QuestionnaireListViewModel
@Inject constructor(questionnaireRepository: QuestionnaireRepository, participantRepository: ParticipantRepository, userRepository: UserRepository) : ViewModel() {
    private val _language = MutableLiveData<QuestionnaireId>()

    val language: LiveData<Resource<List<Questionnaire>>>? = Transformations
        .switchMap(_language) { input ->
            input.ifExists { language, network ->
                questionnaireRepository.getQuestionnaireList(language = language, network = network)
            }
        }


    fun getQuestionnaire(
        language: String?,
        network: Boolean?
    ) {
        val update =
            QuestionnaireId(language, network)
        if (_language.value == update) {
            return
        }
        _language.value = update
    }


    data class QuestionnaireId(
        val language: String?,
        val network: Boolean?
    ) {
        fun <T> ifExists(f: (String, Boolean) -> LiveData<T>): LiveData<T> {
            return if (language == null || network == null) {
                AbsentLiveData.create()
            } else {
                f(language, network)
            }
        }
    }

    fun retry() {
        _language.value?.let {
            _language.value = it
        }
    }

    //    get participant request ----------------------------------------------------------------------------

    private val _screeningId: MutableLiveData<String> = MutableLiveData()

    var participant: LiveData<Resource<ResourceData<ParticipantRequest>>> = Transformations
        .switchMap(_screeningId) { screeningId ->
            if (screeningId == null) {
                AbsentLiveData.create()
            } else {
                participantRepository.getParticipantRequest(screeningId, "questionnaire")
            }
        }

    fun setScreeningId(screeningId: String?) {
        if (_screeningId.value == screeningId) {
            return
        }
        _screeningId.value = screeningId
    }

//    ----------------------------------------------------------------------------------------------------

//    get participant request offline --------------------------------------------------------------------

    private val _screeningIdOffline: MutableLiveData<String> = MutableLiveData()

    var participantOffline: LiveData<Resource<ParticipantRequest>>? = Transformations
        .switchMap(_screeningIdOffline) { screeningIdOffline ->
            if (screeningIdOffline == null) {
                AbsentLiveData.create()
            } else {
                participantRepository.getParticipantOffline(screeningIdOffline)
            }
        }

    fun setScreeningIdOffline(screeningIdOffline: String?) {
        if (_screeningIdOffline.value == screeningIdOffline) {
            return
        }
        _screeningIdOffline.value = screeningIdOffline
    }

//    ----------------------------------------------------------------------------------------------------

//    get user and set user -------------------------------------------------------------------------------

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

//    ----------------------------------------------------------------------------------------------------
}
