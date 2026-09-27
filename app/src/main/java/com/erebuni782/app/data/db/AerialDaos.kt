package com.erebuni782.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AerialSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(session: AerialSessionEntity)

    @Query("SELECT * FROM aerial_sessions ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<AerialSessionEntity>>

    @Query("SELECT * FROM aerial_sessions WHERE id = :id")
    suspend fun byId(id: String): AerialSessionEntity?

    @Query("DELETE FROM aerial_sessions WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface AerialMarkerDao {
    @Insert
    suspend fun insert(marker: AerialMarkerEntity): Long

    @Insert
    suspend fun insertAll(markers: List<AerialMarkerEntity>)

    @Query("SELECT * FROM aerial_markers WHERE sessionId = :sessionId ORDER BY number")
    fun observeForSession(sessionId: String): Flow<List<AerialMarkerEntity>>

    @Query("SELECT * FROM aerial_markers WHERE sessionId = :sessionId ORDER BY number")
    suspend fun forSession(sessionId: String): List<AerialMarkerEntity>

    @Query("SELECT MAX(number) FROM aerial_markers WHERE sessionId = :sessionId")
    suspend fun maxNumber(sessionId: String): Int?

    @Query("DELETE FROM aerial_markers WHERE id = :markerId")
    suspend fun delete(markerId: Long)

    @Query("UPDATE aerial_markers SET note = :note WHERE id = :markerId")
    suspend fun setNote(markerId: Long, note: String)
}
