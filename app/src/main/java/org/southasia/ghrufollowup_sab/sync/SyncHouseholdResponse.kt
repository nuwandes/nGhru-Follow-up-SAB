package org.southasia.ghrufollowup_sab.sync

import org.southasia.ghrufollowup_sab.vo.request.HouseholdRequest

class SyncHouseholdResponse(val eventType: SyncResponseEventType, val household: HouseholdRequest)