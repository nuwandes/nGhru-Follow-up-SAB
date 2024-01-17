package org.nghru_lk.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.nghru_lk.ghru.vo.BloodTestRequest
import org.nghru_lk.ghru.vo.ECGStatus

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


}