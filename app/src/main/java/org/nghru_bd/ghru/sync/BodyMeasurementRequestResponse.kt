package org.nghru_bd.ghru.sync

import org.nghru_bd.ghru.vo.request.BodyMeasurementRequest

class BodyMeasurementRequestResponse(
    val eventType: SyncResponseEventType,
    val bodyMeasurementRequest: BodyMeasurementRequest
)