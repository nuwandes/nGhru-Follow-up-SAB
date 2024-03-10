package org.nghru_inn.ghru.jobs

import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import org.nghru_inn.ghru.sync.ParticipantListItemRxBus
import org.nghru_inn.ghru.sync.SyncResponseEventType
import org.nghru_inn.ghru.vo.ParticipantListItem
import timber.log.Timber

class SyncParticipantListItemJob(private val participantListItem: ParticipantListItem) : Job(
    Params(JobPriority.PARTICIPANT_LIST_ITEM)
        .setRequiresNetwork(true)
        .groupBy("PARTICIPANT_LIST_ITEM")
        .persist()
) {


    override fun onAdded() {
        Timber.d("Executing onAdded() for comment ${participantListItem.participant_id}")
    }

    override fun shouldReRunOnThrowable(throwable: Throwable, runCount: Int, maxRunCount: Int): RetryConstraint {
        if (throwable is RemoteException) {

            val statusCode = throwable.response.code()
            if (statusCode >= 422 && statusCode < 500) {
                return RetryConstraint.CANCEL
            }
        }
        // if we are here, most likely the connection was lost during job execution
        return RetryConstraint.createExponentialBackoff(runCount, 1000);
    }

    override fun onRun() {
        Timber.d("Executing onRun() for household ${participantListItem.participant_id}")
        RemoteHouseholdService().getInstance().updateParticipantVerifiedStatus(participantListItem)
        ParticipantListItemRxBus.getInstance().post(SyncResponseEventType.SUCCESS,participantListItem)
    }

    override fun onCancel(cancelReason: Int, throwable: Throwable?) {
        Timber.d("canceling job. reason: %d, throwable: %s", cancelReason, throwable)
        ParticipantListItemRxBus.getInstance().post(SyncResponseEventType.FAILED,participantListItem)
    }
    companion object {

        private val TAG = SyncParticipantListItemJob::class.java.getCanonicalName()
    }
}