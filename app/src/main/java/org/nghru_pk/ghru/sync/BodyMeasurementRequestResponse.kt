package org.nghru_pk.ghru.sync

import org.nghru_pk.ghru.vo.request.BodyMeasurementRequest

class BodyMeasurementRequestResponse(
    val eventType: SyncResponseEventType,
    val bodyMeasurementRequest: BodyMeasurementRequest
)