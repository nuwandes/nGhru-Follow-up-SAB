package org.nghru_ins.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.nghru_ins.ghru.vo.BloodTestRequest

@Dao
interface BloodTestDao {

    @Query("DELETE FROM blood_test_request")
    fun nukeTable(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bloodTestRequest: BloodTestRequest): Long


    @Query("UPDATE blood_test_request SET sync_pending = 0 WHERE sync_pending = 1")
    fun update()

    @Delete
    fun delete(bloodTestRequest: BloodTestRequest)

    @Query("DELETE FROM blood_test_request WHERE id = :id")
    fun deleteRequest(id : Long)

    @Query("SELECT * FROM blood_test_request WHERE id = :id")
    fun getECGStatus(id: Long): LiveData<BloodTestRequest>

    @Query("SELECT * FROM blood_test_request")
    fun getECGStatuses(): LiveData<List<BloodTestRequest>>

    @Query("SELECT * FROM blood_test_request WHERE sync_pending = 1 ORDER BY id ASC")
    fun getECGStatusesSyncPending():LiveData<List<BloodTestRequest>>

    @Query("SELECT * FROM blood_test_request WHERE screening_id = :screeningId")
    fun getBloodTestByScreeningId(screeningId: String): LiveData<BloodTestRequest>
}