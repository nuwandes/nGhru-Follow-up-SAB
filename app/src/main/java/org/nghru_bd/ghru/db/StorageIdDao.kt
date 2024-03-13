package org.nghru_bd.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.nghru_bd.ghru.vo.StorageIdData

@Dao
interface StorageIdDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(storageIdData: StorageIdData)

    @Insert
    fun insertAll(storageIdList: List<StorageIdData>)

    @Query("DELETE FROM storage_ids")
    fun deleteAll()

    @Query("SELECT * FROM storage_ids WHERE id = :id")
    fun getStorageId(id: Int): LiveData<StorageIdData>

    @Query("SELECT * FROM storage_ids")
    fun getAllStorageIds(): LiveData<List<StorageIdData>>

    @Query("SELECT * FROM storage_ids WHERE storage_id = :storageId")
    fun storageIdList(storageId: String): LiveData<List<StorageIdData>>

    @Query("SELECT * FROM storage_ids WHERE storage_id = :storageId")
    fun getStorageId(storageId: String): LiveData<StorageIdData>

}