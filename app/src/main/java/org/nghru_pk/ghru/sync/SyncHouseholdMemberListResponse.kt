package org.nghru_pk.ghru.sync

import org.nghru_pk.ghru.vo.request.Member

class SyncHouseholdMemberListResponse(val eventType: SyncResponseEventType, val member: List<Member>)