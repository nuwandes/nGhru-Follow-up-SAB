package org.southasia.ghrufollowup_sab.repository

import androidx.lifecycle.LiveData
import org.southasia.ghrufollowup_sab.AppExecutors
import org.southasia.ghrufollowup_sab.api.NghruService
import org.southasia.ghrufollowup_sab.db.UserDao
import org.southasia.ghrufollowup_sab.util.TokenManager
import org.southasia.ghrufollowup_sab.vo.Resource
import org.southasia.ghrufollowup_sab.vo.ResourceData
import org.southasia.ghrufollowup_sab.vo.User
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that handles User objects.
 */

@Singleton
class UserRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val userDao: UserDao,
    private val nghruService: NghruService,
    private val tokenManager: TokenManager

) {

    fun loadUser(): LiveData<Resource<User>> {
        return object : NetworkBoundResource<User, ResourceData<User>>(appExecutors) {
            override fun saveCallResult(item: ResourceData<User>) {
                print("User " + item.data)
                userDao.insert(item.data!!)
            }

            override fun shouldFetch(data: User?): Boolean = data == null

            override fun loadFromDb() = userDao.findByLogin(tokenManager.getEmail()!!)

            override fun createCall() = nghruService.getUser()
        }.asLiveData()
    }

    fun loadUserDB(): LiveData<Resource<User>> {
        return object : NetworkBoundResource<User, ResourceData<User>>(appExecutors) {


            override fun saveCallResult(item: ResourceData<User>) {
                userDao.insert(item.data!!)
            }

            override fun shouldFetch(data: User?): Boolean = data == null

            override fun loadFromDb() = userDao.findByLogin(tokenManager.getEmail()!!)

            override fun createCall() = nghruService.getUser()
        }.asLiveData()
    }

}
