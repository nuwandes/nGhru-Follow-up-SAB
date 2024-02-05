package org.nghru_lk.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.nghru_lk.ghru.vo.FreezerIdData
import org.nghru_lk.ghru.vo.SampleIdData

@Dao
interface FreezerIdDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(freezerIdData: FreezerIdData): Long

    @Insert
    fun insertAll(sampleIdList: List<FreezerIdData>)

    @Query("DELETE FROM freezer_ids")
    fun deleteAll()

    @Query("SELECT * FROM freezer_ids WHERE id = :id")
    fun getFreezerId(id: Int): LiveData<FreezerIdData>

    @Query("SELECT * FROM freezer_ids")
    fun getAllFreezerIds(): LiveData<List<FreezerIdData>>

    @Query("SELECT * FROM freezer_ids WHERE freezer_id = :freezerId")
    fun freezerIdList(freezerId: String): LiveData<List<FreezerIdData>>

}