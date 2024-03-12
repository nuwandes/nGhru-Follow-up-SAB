package org.nghru_pk.ghru.jobs

import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import org.nghru_pk.ghru.sync.CancelRequestRxBus
import org.nghru_pk.ghru.sync.SyncResponseEventType
import org.nghru_pk.ghru.vo.request.CancelRequest
import org.nghru_pk.ghru.vo.request.ParticipantRequest
import timber.log.Timber

class SyncCancelrequestJob(
    private val screeningId : String,
    private val cancelRequest: CancelRequest
) : Job(
    Params(JobPriority.CANCEL_REQUEST)
        .setRequiresNetwork(true)
        .groupBy("survey")
        .persist()
) {


    override fun onAdded() {
        Timber.d("Executing onAdded() for comment $screeningId")
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
        Timber.d("Executing onRun() for household $screeningId")
        if(cancelRequest.stationType == "axivity" || cancelRequest.stationType == "spirometry" || cancelRequest.stationType == "blood-pressure")
        {
            RemoteHouseholdService().getInstance().addCancelAxivityRequestSync(screeningId, cancelRequest)
        }
        else {
            RemoteHouseholdService().getInstance().addCancelRequestSync(screeningId, cancelRequest)
        }
        //  SyncSampleStorageRequestRxBus.getInstance().post(SyncResponseEventType.SUCCESS, survey)
        CancelRequestRxBus.getInstance().post(SyncResponseEventType.SUCCESS,cancelRequest)
    }

    override fun onCancel(cancelReason: Int, throwable: Throwable?) {
        Timber.d("canceling job. reason: %d, throwable: %s", cancelReason, throwable)
        //Crashlytics.logException(throwable)
        //Crashlytics.log("sampleStorageRequest " + survey.toString())
        // sync to remote failed
        //  SyncSampleStorageRequestRxBus.getInstance().post(SyncResponseEventType.FAILED, survey)
        CancelRequestRxBus.getInstance().post(SyncResponseEventType.FAILED,cancelRequest)
    }
}