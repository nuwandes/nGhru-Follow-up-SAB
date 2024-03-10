package org.nghru_inn.ghru.jobs

import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import org.nghru_inn.ghru.sync.ECGStatusRxBus
import org.nghru_inn.ghru.sync.SyncResponseEventType
import org.nghru_inn.ghru.vo.ECGStatus
import timber.log.Timber

class SyncECGJob(private val screeningId: String?,
                 private val eCGStatus: ECGStatus) : Job(
    Params(JobPriority.ECG)
        .setRequiresNetwork(true)
        .groupBy("ECG")
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
        RemoteHouseholdService().getInstance().addECG(screeningId!!, eCGStatus)
        //   SyncSampleStorageRequestRxBus.getInstance().post(SyncResponseEventType.SUCCESS, sampleStorageRequest)
        ECGStatusRxBus.getInstance().post(SyncResponseEventType.SUCCESS,eCGStatus)
    }

    override fun onCancel(cancelReason: Int, throwable: Throwable?) {
        Timber.d("canceling job. reason: %d, throwable: %s", cancelReason, throwable)
        ECGStatusRxBus.getInstance().post(SyncResponseEventType.FAILED,eCGStatus)
        //Crashlytics.logException(throwable)

        //Crashlytics.log("SyncSpirometryJob " + participantRequest.toString())
        // sync to remote failed
        //    SyncSampleStorageRequestRxBus.getInstance().post(SyncResponseEventType.FAILED, sampleStorageRequest)
    }
    companion object {

        private val TAG = SyncECGJob::class.java.getCanonicalName()
    }
}