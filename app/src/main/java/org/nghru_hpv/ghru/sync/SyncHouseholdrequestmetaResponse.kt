package org.nghru_hpv.ghru.sync

import org.nghru_hpv.ghru.vo.request.HouseholdRequestMeta

class SyncHouseholdrequestmetaResponse(
    val eventType: SyncResponseEventType,
    val householdRequestMeta: HouseholdRequestMeta
)