package org.nghru_hpv.ghru.ui.codeheck

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject


class CodeCheckDialogViewModel
@Inject constructor() : ViewModel() {

    var codecheckMsg: MutableLiveData<String> = MutableLiveData<String>().apply { }
}