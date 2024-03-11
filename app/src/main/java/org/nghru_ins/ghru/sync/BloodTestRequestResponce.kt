package org.nghru_ins.ghru.sync

import org.nghru_ins.ghru.vo.BloodTestRequest

class BloodTestRequestResponse(val eventType: SyncResponseEventType,
                               val bloodTestRequest: BloodTestRequest
) {
}