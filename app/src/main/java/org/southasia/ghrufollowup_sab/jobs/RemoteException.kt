package org.southasia.ghrufollowup_sab.jobs

import retrofit2.Response

class RemoteException(val response: Response<*>) : Exception()
