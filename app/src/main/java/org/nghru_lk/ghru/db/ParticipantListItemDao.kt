package org.nghru_lk.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.nghru_lk.ghru.vo.Axivity
import org.nghru_lk.ghru.vo.ParticipantListItem

@Dao
interface ParticipantListItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(participantListItem: ParticipantListItem): Long

    @Query("SELECT * FROM participant_list_item WHERE id =:id")
    fun findById(id: String): LiveData<ParticipantListItem>

    @Query("SELECT * FROM participant_list_item WHERE id = 1 ORDER BY id ASC")
    fun getAxivityRequestSyncPending(): LiveData<List<ParticipantListItem>>

    @Query("DELETE FROM participant_list_item WHERE id = :id")
    fun deleteRequest(id : Long)
}