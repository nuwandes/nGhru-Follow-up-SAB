package org.nghru_pk.ghru

interface LifecycleDelegate {

    fun onAppBackgrounded()
    fun onAppForegrounded()
    fun onScreenLocked()

}