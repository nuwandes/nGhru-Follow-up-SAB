package org.nghru_hpv.ghru.sync

import org.nghru_hpv.ghru.vo.request.CancelRequest

class CancelRequestResponse( val eventType: SyncResponseEventType,
                             val cancelRequest: CancelRequest
) {
}