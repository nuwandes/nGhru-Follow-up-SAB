package org.nghru_pk.ghru.sync

import org.nghru_pk.ghru.vo.request.SampleStorageRequest

class SyncSampleStorageRequestResponse(
    val eventType: SyncResponseEventType,
    val sampleStorageRequest: SampleStorageRequest
)