package org.nghru_hpv.ghru

interface LifecycleDelegate {

    fun onAppBackgrounded()
    fun onAppForegrounded()
    fun onScreenLocked()

}