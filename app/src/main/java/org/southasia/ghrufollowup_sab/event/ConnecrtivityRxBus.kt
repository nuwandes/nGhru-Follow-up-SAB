package org.southasia.ghrufollowup_sab.event

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.southasia.ghrufollowup_sab.sync.SyncResponseEventType


class ConnecrtivityRxBus private constructor() {
    private val relay: PublishRelay<SyncResponseEventType>

    init {
        relay = PublishRelay.create()
    }

    fun post(syncResponseEventType: SyncResponseEventType) {
        relay.accept(syncResponseEventType)
    }

    fun toObservable(): Observable<SyncResponseEventType> {
        return relay
    }

    companion object {

        private var instance: ConnecrtivityRxBus? = null

        @Synchronized
        fun getInstance(): ConnecrtivityRxBus {
            if (instance == null) {
                instance = ConnecrtivityRxBus()
            }
            return instance as ConnecrtivityRxBus
        }
    }
}