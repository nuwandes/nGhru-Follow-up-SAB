package org.nghru_bd.ghru.ui.samplemanagement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_bd.ghru.repository.HomeRepository
import org.nghru_bd.ghru.repository.UserRepository
import org.nghru_bd.ghru.util.AbsentLiveData
import org.nghru_bd.ghru.vo.HomeItem
import org.nghru_bd.ghru.vo.Resource
import javax.inject.Inject


class SampleMangementViewModel
@Inject constructor(repository: HomeRepository, userRepository: UserRepository) : ViewModel() {
    private val _home = MutableLiveData<String>()
    val home: LiveData<String>
        get() = _home


    val homeItem: LiveData<Resource<List<HomeItem>>> = Transformations
        .switchMap(_home) { login ->
            if (login == null) {
                AbsentLiveData.create()
            } else {
                repository.getSampleItems();
            }
        }


    fun setId(lang: String?) {
        if (_home.value != lang) {
            _home.value = lang
        }
    }


}
