package org.southasia.ghrufollowup_sab.sync

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.southasia.ghrufollowup_sab.vo.SpirometryRequest


class SpirometryRequestRxBus private constructor() {

    private val relay: PublishRelay<SpirometryRequest>

    init {
        relay = PublishRelay.create()
    }

    fun post(bodyMeasurementMeta: SpirometryRequest) {
        relay.accept(bodyMeasurementMeta)
    }

    fun toObservable(): Observable<SpirometryRequest> {
        return relay
    }

    companion object {

        private var instance: SpirometryRequestRxBus? = null

        @Synchronized
        fun getInstance(): SpirometryRequestRxBus {
            if (instance == null) {
                instance = SpirometryRequestRxBus()
            }
            return instance as SpirometryRequestRxBus
        }
    }
}