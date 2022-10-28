package org.nghru_inn.ghru.ui.registerpatient_new.basicdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_inn.ghru.repository.UserRepository
import org.nghru_inn.ghru.util.AbsentLiveData
import org.nghru_inn.ghru.vo.Date
import org.nghru_inn.ghru.vo.Resource
import org.nghru_inn.ghru.vo.User
import org.nghru_inn.ghru.vo.request.Gender
import javax.inject.Inject

class BasicDetailsViewModelNew
@Inject constructor(userRepository: UserRepository) : ViewModel() {

    var gender: MutableLiveData<String> = MutableLiveData<String>()

    var birthYear: Int = 1998

    var birthDate: MutableLiveData<String> = MutableLiveData<String>()

    var birthDateVal: MutableLiveData<Date> = MutableLiveData<Date>()

    var contactNo: MutableLiveData<String> = MutableLiveData<String>()

    var age: MutableLiveData<String> = MutableLiveData<String>()


    fun setGender(g: Gender) {
        gender.postValue(g.gender)
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
