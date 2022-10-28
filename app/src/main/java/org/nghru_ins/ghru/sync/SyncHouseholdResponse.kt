package org.nghru_ins.ghru.sync

import org.nghru_ins.ghru.vo.request.HouseholdRequest

class SyncHouseholdResponse(val eventType: SyncResponseEventType, val household: HouseholdRequest)