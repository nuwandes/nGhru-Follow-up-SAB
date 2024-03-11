package org.nghru_ins.ghru.sync

import org.nghru_ins.ghru.vo.request.HouseholdRequestMeta

class SyncHouseholdrequestmetaResponse(
    val eventType: SyncResponseEventType,
    val householdRequestMeta: HouseholdRequestMeta
)