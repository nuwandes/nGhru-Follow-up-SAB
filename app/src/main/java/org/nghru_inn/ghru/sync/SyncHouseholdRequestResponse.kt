package org.nghru_inn.ghru.sync

import org.nghru_inn.ghru.vo.request.HouseholdRequest

class SyncHouseholdRequestResponse(val eventType: SyncResponseEventType, val householdRequest: HouseholdRequest)