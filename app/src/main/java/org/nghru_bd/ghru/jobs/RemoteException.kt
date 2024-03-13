package org.nghru_bd.ghru.jobs

import retrofit2.Response

class RemoteException(val response: Response<*>) : Exception()
