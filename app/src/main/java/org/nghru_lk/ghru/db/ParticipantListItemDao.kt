package org.nghru_lk.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.nghru_lk.ghru.api.ApiResponse
import org.nghru_lk.ghru.vo.ParticipantListItem
import org.nghru_lk.ghru.vo.Resource
import org.nghru_lk.ghru.vo.ResourceData

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

    @Query("SELECT * FROM participant_list_item WHERE participant_id = :key OR firstname = :key OR last_name = :key")
    fun getSearchParticipant(key: String): LiveData<List<ParticipantListItem>>

    @Query("SELECT * FROM participant_list_item WHERE status = :status")
    fun getStatusParticipant(status: String): LiveData<List<ParticipantListItem>>

    @Query("UPDATE participant_list_item SET bm_status = 100 WHERE participant_id = :screeningId")
    fun updateBMStatus(screeningId: String): Int

    @Query("UPDATE participant_list_item SET bp_status = 100 WHERE participant_id = :screeningId")
    fun updateBPStatus(screeningId: String): Int

    @Query("UPDATE participant_list_item SET bt_status = 100 WHERE participant_id = :screeningId")
    fun updateBTStatus(screeningId: String): Int

    @Query("UPDATE participant_list_item SET sam_status = 1 WHERE participant_id = :screeningId")
    fun updateSampleStatus(screeningId: String): Int

    @Query("UPDATE participant_list_item SET ecg_status = 1 WHERE participant_id = :screeningId")
    fun updateEcgStatus(screeningId: String): Int

    @Query("UPDATE participant_list_item SET fun_status = 1 WHERE participant_id = :screeningId")
    fun updateFundoStatus(screeningId: String): Int

    @Query("UPDATE participant_list_item SET act_status = 1 WHERE participant_id = :screeningId")
    fun updateActivityStatus(screeningId: String): Int

    @Query("SELECT * FROM participant_list_item WHERE participant_id = :screeningId")
    fun getSingleParticipant(screeningId: String): LiveData<ParticipantListItem>

    @Query("SELECT * FROM participant_list_item WHERE site = :site")
    fun getSiteParticipant(site: String): LiveData<List<ParticipantListItem>>

    @Query("UPDATE participant_list_item SET que_status = 100 WHERE participant_id = :screeningId")
    fun updateQueStatus(screeningId: String): Int

}