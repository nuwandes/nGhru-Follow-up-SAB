package org.nghru_bd.ghru.jobs

import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import org.nghru_bd.ghru.sync.SyncResponseEventType
import org.nghru_bd.ghru.sync.SyncSampleRequestRxBus
import org.nghru_bd.ghru.vo.request.SampleCreateRequest
import org.nghru_bd.ghru.vo.request.SampleRequest
import timber.log.Timber

class SyncSampledRequestJob(private val sampleRequest: SampleRequest,private val sampleCreateRequest : SampleCreateRequest) : Job(
    Params(JobPriority.SAMPLE_COLLECT)
        .setRequiresNetwork(true)
        .groupBy("SampleCollect")
        .persist()
) {


    override fun onAdded() {
        Timber.d("Executing onAdded() for comment $sampleRequest")
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
        Timber.d("Executing onRun() for household $sampleRequest")
        RemoteHouseholdService().getInstance().addSampleRequest(sampleRequest,sampleCreateRequest)
        SyncSampleRequestRxBus.getInstance().post(SyncResponseEventType.SUCCESS, sampleRequest)
    }

    override fun onCancel(cancelReason: Int, throwable: Throwable?) {
        Timber.d("canceling job. reason: %d, throwable: %s", cancelReason, throwable)
        //Crashlytics.logException(throwable)
        // sync to remote failed
        //Crashlytics.log("sampleRequest " + sampleRequest.toString())
        SyncSampleRequestRxBus.getInstance().post(SyncResponseEventType.FAILED, sampleRequest)
    }
}