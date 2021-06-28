package org.southasia.ghrufollowup_sab.sync

import org.southasia.ghrufollowup_sab.vo.request.CancelRequest

class CancelRequestResponse( val eventType: SyncResponseEventType,
                             val cancelRequest: CancelRequest
) {
}