package org.nghru_inn.ghru.sync

import org.nghru_inn.ghru.vo.request.HouseholdRequest

class SyncHouseholdResponse(val eventType: SyncResponseEventType, val household: HouseholdRequest)