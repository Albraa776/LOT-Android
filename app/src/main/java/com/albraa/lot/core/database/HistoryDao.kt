package com.albraa.lot.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM translation_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM translation_history WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM translation_history WHERE mode = :mode ORDER BY timestamp DESC")
    fun getByMode(mode: String): Flow<List<HistoryEntity>>

    @Query("""
        SELECT * FROM translation_history 
        WHERE sourceText LIKE '%' || :query || '%' 
           OR translatedText LIKE '%' || :query || '%' 
        ORDER BY timestamp DESC
    """)
    fun search(query: String): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HistoryEntity): Long

    @Update
    suspend fun update(item: HistoryEntity)

    @Delete
    suspend fun delete(item: HistoryEntity)

    @Query("UPDATE translation_history SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM translation_history")
    suspend fun clearAll()
}
