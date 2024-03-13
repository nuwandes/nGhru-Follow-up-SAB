package org.nghru_bd.ghru.sync

import org.nghru_bd.ghru.vo.request.SampleStorageRequest

class SyncSampleStorageRequestResponse(
    val eventType: SyncResponseEventType,
    val sampleStorageRequest: SampleStorageRequest
)