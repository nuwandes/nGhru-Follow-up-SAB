package org.nghru_hpv.ghru.sync

import org.nghru_hpv.ghru.vo.ECGStatus


class ECGStatusRequestResponse(
    val eventType: SyncResponseEventType,
    val ecgStatus: ECGStatus) {

}