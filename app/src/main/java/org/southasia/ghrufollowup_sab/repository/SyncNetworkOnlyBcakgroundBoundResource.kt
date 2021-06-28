package org.southasia.ghrufollowup_sab.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import org.southasia.ghrufollowup_sab.AppExecutors
import org.southasia.ghrufollowup_sab.api.ApiEmptyResponse
import org.southasia.ghrufollowup_sab.api.ApiErrorResponse
import org.southasia.ghrufollowup_sab.api.ApiResponse
import org.southasia.ghrufollowup_sab.api.ApiSuccessResponse
import org.southasia.ghrufollowup_sab.vo.Resource

abstract class SyncNetworkOnlyBcakgroundBoundResource <RequestType> @MainThread
internal constructor(private val appExecutors: AppExecutors) {

        private val result = MediatorLiveData<Resource<RequestType>>()

        init {
            result.postValue(Resource.loading(null))
            // LiveData<ResultType> dbSource = loadFromDb();

            val apiResponse = createCall()
            // result.addSource(apiResponse, newData -> result.setValue(Resource.loading(newData.body)));
            result.addSource(apiResponse) { response ->

                when (response) {
                    is ApiSuccessResponse -> {
                        appExecutors.mainThread().execute {
                            // reload from disk whatever we had

                            deleteCall()

                            result.postValue(Resource.success(response.body))
                            asLiveData()
                        }
                    }

                    is ApiEmptyResponse -> {
                        appExecutors.mainThread().execute {
                            // reload from disk whatever we had
                            result.postValue(Resource.success(null))
                            asLiveData()
                        }
                    }

                    is ApiErrorResponse -> {

                        result.postValue(Resource.error(response.errorMessage, null))
                        onFetchFailed()
                    }

                }

            }

        }


        protected fun onFetchFailed() {}

        fun asLiveData(): LiveData<Resource<RequestType>> {
            return result
        }

        @MainThread
        protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>

        @MainThread
        protected abstract fun deleteCall()
}