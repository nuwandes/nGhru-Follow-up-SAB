package org.nghru_pk.ghru.sync

import org.nghru_pk.ghru.vo.ParticipantListItem

class ParticipantListItemRequestResponce(val eventType: SyncResponseEventType,
                               val participantListItem: ParticipantListItem
) {
}