package org.nghru_hpv.ghru.sync

import org.nghru_hpv.ghru.vo.request.BloodPressureMetaRequest


class BloodPresureMetaRequestResponse(
    val eventType: SyncResponseEventType,
    val bloodPressureMetaRequest: BloodPressureMetaRequest
)