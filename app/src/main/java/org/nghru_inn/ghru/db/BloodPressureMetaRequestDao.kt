package org.nghru_inn.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.nghru_inn.ghru.vo.request.BloodPressureMetaRequest

@Dao
interface BloodPressureMetaRequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bloodPressureMetaRequest: BloodPressureMetaRequest): Long

    @Insert
    fun insertAll(bloodPressureMetaRequest: List<BloodPressureMetaRequest>)

    @Update
    fun update(bloodPressureRequest: BloodPressureMetaRequest): Int

    @Query("DELETE FROM blood_pressure_meta")
    fun deleteAll()

    @Query("SELECT * FROM blood_pressure_meta WHERE id = :bloodPressureMetaRequestId")
    fun getBloodPressureMetaRequest(bloodPressureMetaRequestId: Long): LiveData<BloodPressureMetaRequest>

    @Query("SELECT * FROM blood_pressure_meta")
    fun getBloodPressureMetaRequests(): LiveData<List<BloodPressureMetaRequest>>

    @Query("SELECT * FROM blood_pressure_meta WHERE blood_pressure_request_id = :bloodPressureID")
    fun getBloodPressureMetaRequestByBloodPressureID(bloodPressureID: Long): BloodPressureMetaRequest

    @Query("SELECT * FROM blood_pressure_meta WHERE bodyscreening_id = :screeningId")
    fun getBPByScreeningId(screeningId: String): LiveData<BloodPressureMetaRequest>

    @Query("SELECT * FROM blood_pressure_meta WHERE sync_pending = 1 ORDER BY id ASC")
    fun getBPStatusesSyncPending():LiveData<List<BloodPressureMetaRequest>>

}