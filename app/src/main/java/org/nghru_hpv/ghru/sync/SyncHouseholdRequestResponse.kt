package org.nghru_hpv.ghru.sync

import org.nghru_hpv.ghru.vo.request.HouseholdRequest

class SyncHouseholdRequestResponse(val eventType: SyncResponseEventType, val householdRequest: HouseholdRequest)