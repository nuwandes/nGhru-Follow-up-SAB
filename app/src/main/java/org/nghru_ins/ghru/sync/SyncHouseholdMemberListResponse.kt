package org.nghru_ins.ghru.sync

import org.nghru_ins.ghru.vo.request.Member

class SyncHouseholdMemberListResponse(val eventType: SyncResponseEventType, val member: List<Member>)