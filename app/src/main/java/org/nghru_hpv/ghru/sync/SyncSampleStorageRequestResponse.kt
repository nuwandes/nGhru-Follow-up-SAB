package org.nghru_hpv.ghru.sync

import org.nghru_hpv.ghru.vo.request.SampleStorageRequest

class SyncSampleStorageRequestResponse(
    val eventType: SyncResponseEventType,
    val sampleStorageRequest: SampleStorageRequest
)