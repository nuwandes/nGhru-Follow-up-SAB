package org.nghru_bd.ghru.sync

import org.nghru_bd.ghru.vo.BloodTestRequest

class BloodTestRequestResponse(val eventType: SyncResponseEventType,
                               val bloodTestRequest: BloodTestRequest
) {
}