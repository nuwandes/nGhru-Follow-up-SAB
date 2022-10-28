package org.nghru_inn.ghru.event

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.nghru_inn.ghru.vo.HDLDto

class HDLRxBus private constructor() {
    private val relay: PublishRelay<HDLDto>

    init {
        relay = PublishRelay.create()
    }

    fun post(hDLDto: HDLDto) {
        relay.accept(hDLDto)
    }

    fun toObservable(): Observable<HDLDto> {
        return relay
    }

    companion object {

        private var instance: HDLRxBus? = null

        @Synchronized
        fun getInstance(): HDLRxBus {
            if (instance == null) {
                instance = HDLRxBus()
            }
            return instance as HDLRxBus
        }
    }
}