package org.southasia.ghrufollowup_sab.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.southasia.ghrufollowup_sab.vo.request.ParticipantMeta

/**
 * Interface for database access for User related operations.
 */
@Dao
interface ParticipantMetaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(participantMeta: ParticipantMeta): Long

    @Update
    fun update(participantMeta: ParticipantMeta): Int

    @Delete
    fun delete(participantMeta: ParticipantMeta)


    @Query("SELECT * FROM participant_meta WHERE id = :id")
    fun getParticipantMeta(id: Long): LiveData<ParticipantMeta>

    @Query("SELECT * FROM participant_meta")
    fun getParticipantMetas(): LiveData<List<ParticipantMeta>>

}
