package org.nghru_inn.ghru.sync

import org.nghru_inn.ghru.vo.BloodTestRequest

class BloodTestRequestResponse(val eventType: SyncResponseEventType,
                               val bloodTestRequest: BloodTestRequest
) {
}