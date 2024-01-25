package org.nghru_pk.ghru.sync

import org.nghru_pk.ghru.vo.request.HouseholdRequest

class SyncHouseholdResponse(val eventType: SyncResponseEventType, val household: HouseholdRequest)