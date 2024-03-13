package org.nghru_bd.ghru.sync

import org.nghru_bd.ghru.vo.ParticipantListItem

class ParticipantListItemRequestResponce(val eventType: SyncResponseEventType,
                               val participantListItem: ParticipantListItem
) {
}