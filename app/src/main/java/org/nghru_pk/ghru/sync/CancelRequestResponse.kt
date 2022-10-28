package org.nghru_pk.ghru.sync

import org.nghru_pk.ghru.vo.request.CancelRequest

class CancelRequestResponse( val eventType: SyncResponseEventType,
                             val cancelRequest: CancelRequest
) {
}