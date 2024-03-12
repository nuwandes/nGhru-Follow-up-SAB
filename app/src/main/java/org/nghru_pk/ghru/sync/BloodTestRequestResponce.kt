package org.nghru_pk.ghru.sync

import org.nghru_pk.ghru.vo.BloodTestRequest

class BloodTestRequestResponse(val eventType: SyncResponseEventType,
                               val bloodTestRequest: BloodTestRequest
) {
}