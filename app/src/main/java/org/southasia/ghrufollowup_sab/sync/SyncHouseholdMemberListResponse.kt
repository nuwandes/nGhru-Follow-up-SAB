package org.southasia.ghrufollowup_sab.sync

import org.southasia.ghrufollowup_sab.vo.request.Member

class SyncHouseholdMemberListResponse(val eventType: SyncResponseEventType, val member: List<Member>)