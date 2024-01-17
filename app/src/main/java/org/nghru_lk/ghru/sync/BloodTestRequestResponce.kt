package org.nghru_lk.ghru.sync

import org.nghru_lk.ghru.vo.BloodTestRequest

class BloodTestRequestResponse(val eventType: SyncResponseEventType,
                               val bloodTestRequest: BloodTestRequest
) {
}