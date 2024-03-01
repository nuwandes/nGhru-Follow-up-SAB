package org.nghru_pk.ghru.sync

import org.nghru_pk.ghru.vo.ECGStatus


class ECGStatusRequestResponse(
    val eventType: SyncResponseEventType,
    val ecgStatus: ECGStatus) {

}