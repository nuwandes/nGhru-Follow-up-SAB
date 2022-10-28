package org.nghru_inn.ghru.ui.registerpatient_sg.review

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.nghru_inn.ghru.vo.Date
import org.nghru_inn.ghru.vo.request.Gender
import javax.inject.Inject


class ReviewViewModelSG
@Inject constructor() : ViewModel() {

    var gender: MutableLiveData<String> = MutableLiveData<String>()

    var birthYear: Int = 1998

    var birthDate: MutableLiveData<String> = MutableLiveData<String>()

    var birthDateVal: MutableLiveData<Date> = MutableLiveData<Date>()

    var contactNo: MutableLiveData<String> = MutableLiveData<String>()

    var age: MutableLiveData<String> = MutableLiveData<String>()

    fun setGender(g: Gender) {
        gender.postValue(g.gender)
    }

}
