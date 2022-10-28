package org.nghru_inn.ghru.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import org.nghru_inn.ghru.AppExecutors
import org.nghru_inn.ghru.api.ApiEmptyResponse
import org.nghru_inn.ghru.api.ApiErrorResponse
import org.nghru_inn.ghru.api.ApiResponse
import org.nghru_inn.ghru.api.ApiSuccessResponse
import org.nghru_inn.ghru.vo.Resource

abstract class NetworkOnlyBoundResource<RequestType> @MainThread
internal constructor(private val appExecutors: AppExecutors) {

    private val result = MediatorLiveData<Resource<RequestType>>()

    init {
        result.value = Resource.loading(null)
        // LiveData<ResultType> dbSource = loadFromDb();

        val apiResponse = createCall()
        // result.addSource(apiResponse, newData -> result.setValue(Resource.loading(newData.body)));
        result.addSource(apiResponse) { response ->

            when (response) {
                is ApiSuccessResponse -> {
                    appExecutors.mainThread().execute {
                        // reload from disk whatever we had
                        result.value = Resource.success(response.body)
                        asLiveData()
                    }
                }

                is ApiEmptyResponse -> {
                    appExecutors.mainThread().execute {
                        // reload from disk whatever we had
                        result.value = Resource.success(null)
                        asLiveData()
                    }
                }

                is ApiErrorResponse -> {

                    result.value = Resource.error(response.errorMessage, null);
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
}
