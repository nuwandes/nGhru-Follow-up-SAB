package org.nghru_bd.ghru.sync

import org.nghru_bd.ghru.vo.request.CancelRequest

class CancelRequestResponse( val eventType: SyncResponseEventType,
                             val cancelRequest: CancelRequest
) {
}