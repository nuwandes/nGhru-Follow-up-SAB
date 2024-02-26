package org.nghru_bd.ghru.sync

import org.nghru_bd.ghru.vo.request.HouseholdRequest

class SyncHouseholdResponse(val eventType: SyncResponseEventType, val household: HouseholdRequest)