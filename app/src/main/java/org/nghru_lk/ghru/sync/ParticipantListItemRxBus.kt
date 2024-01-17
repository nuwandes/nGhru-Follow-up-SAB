package org.nghru_lk.ghru.sync

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.nghru_lk.ghru.vo.BloodTestRequest
import org.nghru_lk.ghru.vo.ECGStatus
import org.nghru_lk.ghru.vo.ParticipantListItem


class ParticipantListItemRxBus {

    private val relay: PublishRelay<ParticipantListItemRequestResponce>

    init {
        relay = PublishRelay.create()
    }

    fun post(eventType: SyncResponseEventType, participantListItem: ParticipantListItem) {
        relay.accept(ParticipantListItemRequestResponce(eventType, participantListItem))
    }

    fun toObservable(): Observable<ParticipantListItemRequestResponce> {
        return relay
    }

    companion object {

        private var instance: ParticipantListItemRxBus? = null

        @Synchronized
        fun getInstance(): ParticipantListItemRxBus {
            if (instance == null) {
                instance = ParticipantListItemRxBus()
            }
            return instance as ParticipantListItemRxBus
        }
    }
}