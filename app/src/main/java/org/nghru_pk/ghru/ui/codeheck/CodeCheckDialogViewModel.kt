package org.nghru_pk.ghru.ui.codeheck

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject


class CodeCheckDialogViewModel
@Inject constructor() : ViewModel() {

    var codecheckMsg: MutableLiveData<String> = MutableLiveData<String>().apply { }
}