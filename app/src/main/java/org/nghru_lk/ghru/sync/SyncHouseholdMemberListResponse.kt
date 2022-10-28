package org.nghru_lk.ghru.sync

import org.nghru_lk.ghru.vo.request.Member

class SyncHouseholdMemberListResponse(val eventType: SyncResponseEventType, val member: List<Member>)