package org.nghru_hpv.ghru.jobs

import retrofit2.Response

class RemoteException(val response: Response<*>) : Exception()
