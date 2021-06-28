package org.southasia.ghrufollowup_sab.sync

import org.southasia.ghrufollowup_sab.vo.request.BloodPressureMetaRequest


class BloodPresureMetaRequestResponse(
    val eventType: SyncResponseEventType,
    val bloodPressureMetaRequest: BloodPressureMetaRequest
)