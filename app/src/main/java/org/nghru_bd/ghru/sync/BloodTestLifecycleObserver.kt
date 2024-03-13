package org.nghru_bd.ghru.sync

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import org.nghru_bd.ghru.db.BloodTestDao
import timber.log.Timber

class BloodTestLifecycleObserver(var ecgStatusDao: BloodTestDao) : LifecycleObserver {

    private val disposables = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        Timber.d("onResume lifecycle event.")
        disposables.add(
            BloodTestRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    Log.d("Result", "household SyncCommentLifecycleObserver ${result.bloodTestRequest}")
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

    private fun handleSyncResponse(bloodTestRequestResponse: BloodTestRequestResponse) {
        if (bloodTestRequestResponse.eventType === SyncResponseEventType.SUCCESS) {
            onSyncCommentSuccess(bloodTestRequestResponse)
        } else {
            onSyncCommentFailed(bloodTestRequestResponse)
        }
    }

    private fun onSyncCommentSuccess(bloodTestRequestResponse: BloodTestRequestResponse) {
        bloodTestRequestResponse?.bloodTestRequest.syncPending = false
        bloodTestRequestResponse?.bloodTestRequest.screeningId = "00000"
    }

    private fun onSyncCommentFailed(bloodTestRequestResponse: BloodTestRequestResponse) {
        Timber.d("received sync comment failed event for comment %s", bloodTestRequestResponse)
    }
}