package org.southasia.ghrufollowup_sab.sync

import org.southasia.ghrufollowup_sab.vo.ECGStatus


class ECGStatusRequestResponse(
    val eventType: SyncResponseEventType,
    val ecgStatus: ECGStatus) {

}