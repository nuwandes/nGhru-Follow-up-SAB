package org.nghru_inn.ghru.sync

import org.nghru_inn.ghru.vo.request.Member

class SyncHouseholdMemberListResponse(val eventType: SyncResponseEventType, val member: List<Member>)