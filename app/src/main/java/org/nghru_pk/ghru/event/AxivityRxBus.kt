package org.nghru_pk.ghru.event

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.nghru_pk.ghru.vo.Axivity

class AxivityRxBus private constructor() {
    private val relay: PublishRelay<Axivity>

    init {
        relay = PublishRelay.create()
    }

    fun post(axivity: Axivity) {
        relay.accept(axivity)
    }

    fun toObservable(): Observable<Axivity> {
        return relay
    }

    companion object {

        private var instance: AxivityRxBus? = null

        @Synchronized
        fun getInstance(): AxivityRxBus {
            if (instance == null) {
                instance = AxivityRxBus()
            }
            return instance as AxivityRxBus
        }
    }
}