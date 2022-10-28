package org.nghru_pk.ghru.sync

import org.nghru_pk.ghru.vo.request.HouseholdRequestMeta

class SyncHouseholdrequestmetaResponse(
    val eventType: SyncResponseEventType,
    val householdRequestMeta: HouseholdRequestMeta
)