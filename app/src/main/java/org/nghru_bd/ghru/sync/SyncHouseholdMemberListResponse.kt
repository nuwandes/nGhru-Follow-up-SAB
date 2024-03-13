package org.nghru_bd.ghru.sync

import org.nghru_bd.ghru.vo.request.Member

class SyncHouseholdMemberListResponse(val eventType: SyncResponseEventType, val member: List<Member>)