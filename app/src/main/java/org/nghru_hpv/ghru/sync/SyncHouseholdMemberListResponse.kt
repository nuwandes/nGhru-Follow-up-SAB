package org.nghru_hpv.ghru.sync

import org.nghru_hpv.ghru.vo.request.Member

class SyncHouseholdMemberListResponse(val eventType: SyncResponseEventType, val member: List<Member>)