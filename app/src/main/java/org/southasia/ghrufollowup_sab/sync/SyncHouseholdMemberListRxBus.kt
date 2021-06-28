package org.southasia.ghrufollowup_sab.sync

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.southasia.ghrufollowup_sab.vo.request.Member

class SyncHouseholdMemberListRxBus private constructor() {
    private val relay: PublishRelay<SyncHouseholdMemberListResponse>

    init {
        relay = PublishRelay.create()
    }

    fun post(eventType: SyncResponseEventType, member: List<Member>) {
        relay.accept(SyncHouseholdMemberListResponse(eventType, member))
    }

    fun toObservable(): Observable<SyncHouseholdMemberListResponse> {
        return relay
    }

    companion object {

        private var instance: SyncHouseholdMemberListRxBus? = null

        @Synchronized
        fun getInstance(): SyncHouseholdMemberListRxBus {
            if (instance == null) {
                instance = SyncHouseholdMemberListRxBus()
            }
            return instance as SyncHouseholdMemberListRxBus
        }
    }
}