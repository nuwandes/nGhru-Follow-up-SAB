package org.nghru_pk.ghru.sync

import org.nghru_pk.ghru.vo.request.BloodPressureMetaRequest


class BloodPresureMetaRequestResponse(
    val eventType: SyncResponseEventType,
    val bloodPressureMetaRequest: BloodPressureMetaRequest
)