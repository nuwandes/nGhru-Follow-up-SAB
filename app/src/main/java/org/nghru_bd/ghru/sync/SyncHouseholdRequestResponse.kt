package org.nghru_bd.ghru.sync

import org.nghru_bd.ghru.vo.request.HouseholdRequest

class SyncHouseholdRequestResponse(val eventType: SyncResponseEventType, val householdRequest: HouseholdRequest)