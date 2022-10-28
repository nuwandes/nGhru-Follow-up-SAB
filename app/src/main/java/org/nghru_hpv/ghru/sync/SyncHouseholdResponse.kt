package org.nghru_hpv.ghru.sync

import org.nghru_hpv.ghru.vo.request.HouseholdRequest

class SyncHouseholdResponse(val eventType: SyncResponseEventType, val household: HouseholdRequest)