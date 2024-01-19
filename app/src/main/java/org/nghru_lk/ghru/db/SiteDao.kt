package org.nghru_lk.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.nghru_lk.ghru.vo.Axivity
import org.nghru_lk.ghru.vo.Site

@Dao
interface SiteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(site: Site): Long

    @Query("SELECT * FROM sites WHERE id = :Id")
    fun getSiteById(Id: String): LiveData<Site>
}