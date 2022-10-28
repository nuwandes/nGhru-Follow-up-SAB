package org.nghru_hpv.ghru.sync

import org.nghru_hpv.ghru.vo.request.BodyMeasurementRequest

class BodyMeasurementRequestResponse(
    val eventType: SyncResponseEventType,
    val bodyMeasurementRequest: BodyMeasurementRequest
)