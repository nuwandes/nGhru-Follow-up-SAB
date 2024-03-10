package org.nghru_inn.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.nghru_inn.ghru.vo.request.BodyMeasurementMeta


/**
 * Interface for database access for User related operations.
 */
@Dao
interface BodyMeasurementMetaDao {


    @Query("DELETE FROM body_measurement_meta")
    fun nukeTable(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bodyMeasurementMeta: BodyMeasurementMeta): Long

    @Delete
    fun delete(bodyMeasurementMeta: BodyMeasurementMeta)

    @Query("DELETE FROM body_measurement_meta WHERE id = :id")
    fun deleteRequest(id : Long)

    @Query("UPDATE body_measurement_meta SET sync_pending = 0 WHERE sync_pending = 1 AND screening_id = :screeningId")
    fun update(screeningId: String): Int


    @Query("SELECT * FROM body_measurement_meta WHERE id = :id")
    fun getBodyMeasurementMeta(id: Long): LiveData<BodyMeasurementMeta>

    @Query("SELECT * FROM body_measurement_meta")
    fun getBodyMeasurementMetas(): LiveData<List<BodyMeasurementMeta>>

    @Query("SELECT * FROM body_measurement_meta WHERE screening_id=:screeningId ORDER BY id DESC LIMIT 1")
    fun getBodyMeasurementMetas(screeningId: String): LiveData<BodyMeasurementMeta>

    @Query("SELECT * FROM body_measurement_meta WHERE sync_pending = 1 ORDER BY id ASC")
    fun getBodyMeasurementMetasSyncPending(): LiveData<List<BodyMeasurementMeta>>

    @Query("SELECT * FROM body_measurement_meta WHERE screening_id = :screeningId")
    fun getBMByScreeningId(screeningId: String): LiveData<BodyMeasurementMeta>

    //SELECT * FROM access_token LIMIT 1

}
