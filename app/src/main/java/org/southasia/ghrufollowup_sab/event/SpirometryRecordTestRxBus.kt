package org.southasia.ghrufollowup_sab.event

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.southasia.ghrufollowup_sab.vo.SpirometryRecord

class SpirometryRecordTestRxBus private constructor() {

    private val relay: PublishRelay<SpirometryRecord>

    init {
        relay = PublishRelay.create()
    }

    fun post(record: SpirometryRecord) {
        relay.accept(record)
    }

    fun toObservable(): Observable<SpirometryRecord> {
        return relay
    }

    companion object {

        private var instance: SpirometryRecordTestRxBus? = null

        @Synchronized
        fun getInstance(): SpirometryRecordTestRxBus {
            if (instance == null) {
                instance = SpirometryRecordTestRxBus()
            }
            return instance as SpirometryRecordTestRxBus
        }
    }
}