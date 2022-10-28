package org.nghru_lk.ghru.sync

import org.nghru_lk.ghru.vo.request.HouseholdRequest

class SyncHouseholdResponse(val eventType: SyncResponseEventType, val household: HouseholdRequest)