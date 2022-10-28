package org.nghru_ins.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.nghru_ins.ghru.vo.LoginData

/**
 * Interface for database access for User related operations.
 */
@Dao
interface LoginDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(loginData: LoginData)

    @Query("SELECT * FROM login_data WHERE user_email = :email")
    fun findByLogin(email: String): LiveData<LoginData>
}
