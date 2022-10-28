package org.nghru_inn.ghru.sync

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.nghru_inn.ghru.vo.Axivity

class AxivityRequestRxBus {

    private val relay: PublishRelay<AxivityRequestResponce>

    init {
        relay = PublishRelay.create()
    }

    fun post(eventType: SyncResponseEventType,axivity: Axivity) {
        relay.accept(AxivityRequestResponce(eventType, axivity))
    }

    fun toObservable(): Observable<AxivityRequestResponce> {
        return relay
    }

    companion object {

        private var instance: AxivityRequestRxBus? = null

        @Synchronized
        fun getInstance(): AxivityRequestRxBus {
            if (instance == null) {
                instance = AxivityRequestRxBus()
            }
            return instance as AxivityRequestRxBus
        }
    }
}