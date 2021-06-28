package org.southasia.ghrufollowup_sab.sync

import org.southasia.ghrufollowup_sab.vo.FundoscopyRequest

class FundoscopyRequestResponce(
    val eventType: SyncResponseEventType,
    val fundoscopyRequest: FundoscopyRequest
)
{
}