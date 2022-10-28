package org.nghru_inn.ghru.sync

import org.nghru_inn.ghru.vo.request.BloodPressureMetaRequest


class BloodPresureMetaRequestResponse(
    val eventType: SyncResponseEventType,
    val bloodPressureMetaRequest: BloodPressureMetaRequest
)