package org.nghru_inn.ghru.sync

import org.nghru_inn.ghru.vo.ECGStatus


class ECGStatusRequestResponse(
    val eventType: SyncResponseEventType,
    val ecgStatus: ECGStatus) {

}