package org.nghru_inn.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.nghru_inn.ghru.vo.SampleIdData

@Dao
interface SampleIdDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sampleIdData: SampleIdData)

    @Insert
    fun insertAll(sampleIdList: List<SampleIdData>)

    @Query("DELETE FROM sample_ids")
    fun deleteAll()

    @Query("SELECT * FROM sample_ids WHERE id = :id")
    fun getSampleId(id: Int): LiveData<SampleIdData>

    @Query("SELECT * FROM sample_ids")
    fun getAllSampleIds(): LiveData<List<SampleIdData>>

    @Query("SELECT * FROM sample_ids WHERE sample_id = :sampleId")
    fun sampleIdList(sampleId: String): LiveData<List<SampleIdData>>

    @Query("SELECT * FROM sample_ids WHERE sample_id = :sampleId")
    fun getSampleId(sampleId: String): LiveData<SampleIdData>

}