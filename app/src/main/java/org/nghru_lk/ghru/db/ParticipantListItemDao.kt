package org.nghru_lk.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

    @Query("DELETE FROM participant_list_item")
    fun deleteAll()

    @Query("SELECT * FROM participant_list_item")
    fun getAllParticipantListItems(): LiveData<List<ParticipantListItem>>

    @Insert
    fun insertAll(participantListItemList: List<ParticipantListItem>)

    @Query("SELECT * FROM participant_list_item")
    fun getAllParticipantListItemsToHome(): LiveData<List<ParticipantListItem>>

    @Query("UPDATE participant_list_item SET inablitiy_reason = :inabilityReason, is_able = :isAble, is_rescheduled= :isReschedule, is_verified = :isVerified , is_sync = :isSync WHERE participant_id =:participantId")
    fun updateSingleParticipantListItem(inabilityReason: String?, isAble: Boolean?, isReschedule: Boolean?, isVerified: Boolean?, isSync: Boolean?, participantId: String) : Int

    @Query("SELECT * FROM participant_list_item WHERE participant_id = :id")
    fun getSingleParticipantByParticipantId(id: String): LiveData<ParticipantListItem>

    @Query("SELECT * FROM participant_list_item WHERE is_sync = :isSync")
    fun getAllUnSyncParticipantListItem(isSync: Boolean?): LiveData<ParticipantListItem>

    @Query("UPDATE participant_list_item SET is_sync= 0 WHERE is_sync = 1 AND participant_id = :screeningId")
    fun update(screeningId: String): Int
}