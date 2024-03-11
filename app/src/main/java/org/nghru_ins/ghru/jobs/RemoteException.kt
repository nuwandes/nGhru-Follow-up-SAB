package org.nghru_ins.ghru.jobs

import retrofit2.Response

class RemoteException(val response: Response<*>) : Exception()
