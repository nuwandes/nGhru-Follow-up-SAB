package org.nghru_inn.ghru.ui.participantlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_inn.ghru.repository.ParticipantListRepository
import org.nghru_inn.ghru.repository.UserRepository
import org.nghru_inn.ghru.util.AbsentLiveData
import org.nghru_inn.ghru.vo.*
import org.nghru_inn.ghru.vo.request.ParticipantListWithMeta
import javax.inject.Inject

class ParticipantListViewModel
@Inject constructor(repository: ParticipantListRepository, userRepository: UserRepository) : ViewModel() {


    private val _site = MutableLiveData<String>()

    private val _email = MutableLiveData<String>()

    private val _status = MutableLiveData<String>()

    val email: LiveData<String>
        get() = _email



    private val _siteId: MutableLiveData<SiteId> = MutableLiveData()

    private val _pageId: MutableLiveData<PageId> = MutableLiveData()

//    Get all Participants ----------------------------------------------------------------------------------

    private val _participantlist = MutableLiveData<String>()

    val participantlistItem: LiveData<Resource<ResourceData<ParticipantListWithMeta>>> = Transformations
        .switchMap(_participantlist) { login ->
            if (login == null) {
                AbsentLiveData.create()
            } else {
                repository.getParticipantListItems()
            }
        }

    fun setId(lang: String?) {
        if (_participantlist.value != lang) {
            _participantlist.value = lang
        }
    }

//    Get participants with search and filter ---------------------------------------------------------------

    private val _filterId: MutableLiveData<FilterId> = MutableLiveData()

    var filterparticipantListItems: LiveData<Resource<ResourceData<ParticipantListWithMeta>>>? = Transformations
        .switchMap(_filterId) { input ->
            input.ifExists { page, status, site, keyWord ->
                repository.filterParticipantListItems(page!!, status!!.toString(), site!!.toString(), keyWord!!.toString())

            }
        }

    fun setFilterId(page: Int, status: String, site: String, keyWord: String) {
        val update =
            FilterId(page = page, status = status, site = site, keyWord = keyWord)
        if (_filterId.value == update) {
            return
        }
        _filterId.value = update
    }

    data class FilterId(val page: Int?, val status: String?, val site: String?, val keyWord: String?) {

        fun <T> ifExists(f: (Int?, String?, String?, String?) -> LiveData<T>): LiveData<T> {
            return if (page == null || status == null || site == null || keyWord == null) {
                AbsentLiveData.create()
            } else {
                f(page, status, site, keyWord)
            }
        }
    }
//    --------------------------------------------------------------------------------------------------------

//    Get single participant stations ------------------------------------------------------------------------

//    private val _participantId: MutableLiveData<ParticipantId> = MutableLiveData()
//
//    var getSingleParticipantStations: LiveData<Resource<ResourceData<ParticipantStationRequest>>>? = Transformations
//        .switchMap(_participantId) { input ->
//            input.ifExists { participant ->
//                repository.getSingleParticipantStations(participant!!.toString())
//
//            }
//        }
//
//    fun setParticipantId(participant: String) {
//        val update =
//            ParticipantId(participant = participant)
//        if (_participantId.value == update) {
//            return
//        }
//        _participantId.value = update
//    }
//
//    data class ParticipantId(val participant: String?) {
//
//        fun <T> ifExists(f: (String?) -> LiveData<T>): LiveData<T> {
//            return if (participant == null) {
//                AbsentLiveData.create()
//            } else {
//                f(participant)
//            }
//        }
//    }


//    --------------------------------------------------------------------------------------------------------



//    val firstParticipantPage: LiveData<Resource<List<ParticipantListItem>>> = Transformations
//        .switchMap(_participantlist) { login ->
//            if (login == null) {
//                AbsentLiveData.create()
//            } else {
//                repository.firstPage()
//            }
//        }
//
//    val previousParticipantPage: LiveData<Resource<List<ParticipantListItem>>> = Transformations
//        .switchMap(_participantlist) { login ->
//            if (login == null) {
//                AbsentLiveData.create()
//            } else {
//                repository.previousPage()
//            }
//        }
//
//    val nextParticipantPage: LiveData<Resource<List<ParticipantListItem>>> = Transformations
//        .switchMap(_participantlist) { login ->
//            if (login == null) {
//                AbsentLiveData.create()
//            } else {
//                repository.nextPage()
//            }
//        }
//
//    val lastParticipantPage: LiveData<Resource<List<ParticipantListItem>>> = Transformations
//        .switchMap(_participantlist) { login ->
//            if (login == null) {
//                AbsentLiveData.create()
//            } else {
//                repository.lastPage()
//            }
//        }

    val user: LiveData<Resource<User>>? = Transformations
        .switchMap(_email) { email ->
            if (email == null) {
                AbsentLiveData.create()
            } else {
                userRepository.loadUser()
            }
        }

    fun setUser(email: String?) {
        if (_email.value != email) {
            _email.value = email
        }
    }



    var siteSpinnerItem: LiveData<Resource<ResourceData<Array<String>>>>? = Transformations
        .switchMap(_siteId) { input ->
            input.ifExists { id ->
                repository.getSiteSpinnerItems(id!!)

            }
        }

    fun setSiteId(id: String) {
        val update =
            SiteId(id = id)
        if (_siteId.value == update) {
            return
        }
        _siteId.value = update
    }

    data class SiteId(val id: String?) {

        fun <T> ifExists(f: (String?) -> LiveData<T>): LiveData<T> {
            return if (id == null) {
                AbsentLiveData.create()
            } else {
                f(id)
            }
        }
    }

    fun setSite(lang: String?) {
        if (_site.value != lang) {
            _site.value = lang
        }
    }

    var paginateParticipantItems: LiveData<Resource<ResourceData<ParticipantListWithMeta>>>? = Transformations
        .switchMap(_pageId) { input ->
            input.ifExists { page ->
                repository.paginateParticipantList(page!!)

            }
        }

    fun setPageId(page: Int) {
        val update =
            PageId(page = page)
        if (_pageId.value == update) {
            return
        }
        _pageId.value = update
    }

    data class PageId(val page: Int?) {

        fun <T> ifExists(f: (Int?) -> LiveData<T>): LiveData<T> {
            return if (page == null) {
                AbsentLiveData.create()
            } else {
                f(page)
            }
        }
    }

}