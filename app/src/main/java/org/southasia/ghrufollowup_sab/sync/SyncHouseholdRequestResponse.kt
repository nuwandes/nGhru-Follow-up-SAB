package org.southasia.ghrufollowup_sab.sync

import org.southasia.ghrufollowup_sab.vo.request.HouseholdRequest

class SyncHouseholdRequestResponse(val eventType: SyncResponseEventType, val householdRequest: HouseholdRequest)