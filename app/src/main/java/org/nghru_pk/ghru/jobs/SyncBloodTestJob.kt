package org.nghru_pk.ghru.jobs

import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import org.nghru_pk.ghru.sync.BloodTestRxBus
import org.nghru_pk.ghru.sync.SyncResponseEventType
import org.nghru_pk.ghru.vo.BloodTestRequest
import timber.log.Timber

class SyncBloodTestJob(private val bloodTestRequest: BloodTestRequest) : Job(
    Params(JobPriority.BLOOD_TEST)
        .setRequiresNetwork(true)
        .groupBy("BLOOD_TEST")
        .persist()
) {


    override fun onAdded() {
        Timber.d("Executing onAdded() for comment ${bloodTestRequest.screeningId}")
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
        Timber.d("Executing onRun() for household $bloodTestRequest.screeningId")
        RemoteHouseholdService().getInstance().addBloodTest(bloodTestRequest)
        BloodTestRxBus.getInstance().post(SyncResponseEventType.SUCCESS,bloodTestRequest)
    }

    override fun onCancel(cancelReason: Int, throwable: Throwable?) {
        Timber.d("canceling job. reason: %d, throwable: %s", cancelReason, throwable)
        BloodTestRxBus.getInstance().post(SyncResponseEventType.FAILED,bloodTestRequest)
    }
    companion object {

        private val TAG = SyncBloodTestJob::class.java.getCanonicalName()
    }
}