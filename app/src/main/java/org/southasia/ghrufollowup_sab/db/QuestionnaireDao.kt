package org.southasia.ghrufollowup_sab.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.southasia.ghrufollowup_sab.vo.Questionnaire

/**
 * Interface for database access for User related operations.
 */
@Dao
interface QuestionnaireDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(questionnaire: Questionnaire): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(questionnaires: List<Questionnaire>)

    @Update
    fun update(questionnaire: Questionnaire): Int

    @Delete
    fun delete(questionnaire: Questionnaire)

    @Query("SELECT * FROM questionnaire WHERE id = :id")
    fun getQuestionnaire(id: Long): LiveData<Questionnaire>

    @Query("SELECT * FROM questionnaire")
    fun getQuestionnaires(): LiveData<List<Questionnaire>>

    @Query("SELECT * FROM questionnaire LIMIT 1")
    fun getQuestionnaire(): LiveData<Questionnaire>
    @Query("DELETE FROM questionnaire")
    fun nukeTable()
}
