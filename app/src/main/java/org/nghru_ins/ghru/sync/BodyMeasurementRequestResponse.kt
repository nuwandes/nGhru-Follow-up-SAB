package org.nghru_ins.ghru.sync

import org.nghru_ins.ghru.vo.request.BodyMeasurementRequest

class BodyMeasurementRequestResponse(
    val eventType: SyncResponseEventType,
    val bodyMeasurementRequest: BodyMeasurementRequest
)