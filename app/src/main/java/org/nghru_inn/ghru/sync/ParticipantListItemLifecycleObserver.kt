package org.nghru_inn.ghru.sync

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import org.nghru_inn.ghru.db.ParticipantListItemDao
import timber.log.Timber

class ParticipantListItemLifecycleObserver(var participantListItemDao: ParticipantListItemDao) : LifecycleObserver {

    private val disposables = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        Timber.d("onResume lifecycle event.")
        disposables.add(
            ParticipantListItemRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    Log.d("Result", "household SyncCommentLifecycleObserver ${result.participantListItem}")
                    handleSyncResponse(result)
                }, { error ->
                    error.printStackTrace()
                })
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        Timber.d("onPause lifecycle event.")
        disposables.clear()
    }

    private fun handleSyncResponse(participantListItemRequestResponce: ParticipantListItemRequestResponce) {
        if (participantListItemRequestResponce.eventType === SyncResponseEventType.SUCCESS) {
            onSyncCommentSuccess(participantListItemRequestResponce)
        } else {
            onSyncCommentFailed(participantListItemRequestResponce)
        }
    }

    private fun onSyncCommentSuccess(participantListItemRequestResponce: ParticipantListItemRequestResponce) {
        participantListItemRequestResponce.participantListItem.isSync = false
        participantListItemDao.update(participantListItemRequestResponce.participantListItem.participant_id!!)
        //participantListItemRequestResponce.participantListItem.participant_id = "00000"
    }

    private fun onSyncCommentFailed(participantListItemRequestResponce: ParticipantListItemRequestResponce) {
        Timber.d("received sync comment failed event for comment %s", participantListItemRequestResponce)
    }
}