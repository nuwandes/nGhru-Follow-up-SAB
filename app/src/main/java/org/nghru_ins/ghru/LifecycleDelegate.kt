package org.nghru_ins.ghru

interface LifecycleDelegate {

    fun onAppBackgrounded()
    fun onAppForegrounded()
    fun onScreenLocked()

}