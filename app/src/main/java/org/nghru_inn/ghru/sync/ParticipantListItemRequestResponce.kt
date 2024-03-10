package org.nghru_inn.ghru.sync

import org.nghru_inn.ghru.vo.ParticipantListItem

class ParticipantListItemRequestResponce(val eventType: SyncResponseEventType,
                               val participantListItem: ParticipantListItem
) {
}