package org.southasia.ghrufollowup_sab.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.southasia.ghrufollowup_sab.vo.Meta

/**
 * Interface for database access for User related operations.
 */
@Dao
interface MetaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(meta: Meta): Long

    @Update
    fun update(meta: Meta): Int

    @Delete
    fun delete(meta: Meta)


    @Query("SELECT * FROM household_request_meta WHERE id = :id")
    fun getMeta(id: Long): LiveData<Meta>

    @Query("SELECT * FROM household_request_meta")
    fun getMetas(): LiveData<List<Meta>>

}