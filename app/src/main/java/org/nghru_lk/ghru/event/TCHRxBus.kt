package org.nghru_lk.ghru.event

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.nghru_lk.ghru.vo.BloodTestData

class TCHRxBus private constructor() {

    private val relay: PublishRelay<BloodTestData>

    init {
        relay = PublishRelay.create()
    }

    fun post(record: BloodTestData) {
        relay.accept(record)
    }

    fun toObservable(): Observable<BloodTestData> {
        return relay
    }

    companion object {

        private var instance: TCHRxBus? = null

        @Synchronized
        fun getInstance(): TCHRxBus {
            if (instance == null) {
                instance = TCHRxBus()
            }
            return instance as TCHRxBus
        }
    }
}