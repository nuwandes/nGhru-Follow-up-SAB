package org.nghru_hpv.ghru.sync

import org.nghru_hpv.ghru.vo.FundoscopyRequest

class FundoscopyRequestResponce(
    val eventType: SyncResponseEventType,
    val fundoscopyRequest: FundoscopyRequest
)
{
}