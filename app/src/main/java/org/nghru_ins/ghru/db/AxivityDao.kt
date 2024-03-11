package org.nghru_ins.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.nghru_ins.ghru.vo.Axivity

@Dao
interface AxivityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(axivity: Axivity): Long

    @Query("UPDATE axivity SET sync_pending = 0 WHERE sync_pending = 1 AND screening_id = :screeningId")
    fun update(screeningId: String): Int

    @Query("SELECT * FROM axivity WHERE id =:id")
    fun findById(id: String): LiveData<Axivity>

    @Query("SELECT * FROM axivity WHERE sync_pending = 1 ORDER BY id ASC")
    fun getAxivityRequestSyncPending(): LiveData<List<Axivity>>

    @Query("DELETE FROM axivity WHERE id = :id")
    fun deleteRequest(id : Long)

    @Query("SELECT * FROM axivity WHERE screening_id = :screeningId")
    fun getAxivityByScreeningId(screeningId: String): LiveData<Axivity>
}