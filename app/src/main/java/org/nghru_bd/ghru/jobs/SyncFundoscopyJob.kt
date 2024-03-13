package org.nghru_bd.ghru.jobs

import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import org.nghru_bd.ghru.sync.FundoscopyRequestRxBus
import org.nghru_bd.ghru.sync.SyncResponseEventType
import org.nghru_bd.ghru.vo.FundoscopyRequest
import timber.log.Timber

class SyncFundoscopyJob(
    private val screeningId: String?,
    private val fundoscopyRequest : FundoscopyRequest
) : Job(
    Params(JobPriority.FUNDOSCOPY)
        .setRequiresNetwork(true)
        .groupBy("fundoscopy")
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
        RemoteHouseholdService().getInstance().addFundoscopy(screeningId!!,fundoscopyRequest)
        FundoscopyRequestRxBus.getInstance().post(SyncResponseEventType.SUCCESS,fundoscopyRequest)
        //   SyncSampleStorageRequestRxBus.getInstance().post(SyncResponseEventType.SUCCESS, sampleStorageRequest)
    }

    override fun onCancel(cancelReason: Int, throwable: Throwable?) {
        Timber.d("canceling job. reason: %d, throwable: %s", cancelReason, throwable)
        FundoscopyRequestRxBus.getInstance().post(SyncResponseEventType.FAILED,fundoscopyRequest)
        //Crashlytics.logException(throwable)
        //Crashlytics.log("SyncSpirometryJob " + participantRequest.toString())
        // sync to remote failed
        //    SyncSampleStorageRequestRxBus.getInstance().post(SyncResponseEventType.FAILED, sampleStorageRequest)
    }
    companion object {

        private val TAG = SyncFundoscopyJob::class.java.getCanonicalName()
    }
}