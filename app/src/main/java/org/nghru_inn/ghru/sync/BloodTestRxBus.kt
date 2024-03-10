package org.nghru_inn.ghru.sync

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.nghru_inn.ghru.vo.BloodTestRequest


class BloodTestRxBus {

    private val relay: PublishRelay<BloodTestRequestResponse>

    init {
        relay = PublishRelay.create()
    }

    fun post(eventType: SyncResponseEventType, bloodTestRequest: BloodTestRequest) {
        relay.accept(BloodTestRequestResponse(eventType, bloodTestRequest))
    }

    fun toObservable(): Observable<BloodTestRequestResponse> {
        return relay
    }

    companion object {

        private var instance: BloodTestRxBus? = null

        @Synchronized
        fun getInstance(): BloodTestRxBus {
            if (instance == null) {
                instance = BloodTestRxBus()
            }
            return instance as BloodTestRxBus
        }
    }
}