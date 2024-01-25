package org.nghru_pk.ghru.jobs

import retrofit2.Response

class RemoteException(val response: Response<*>) : Exception()
