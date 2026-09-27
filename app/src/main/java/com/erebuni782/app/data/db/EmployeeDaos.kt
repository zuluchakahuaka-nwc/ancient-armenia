package com.erebuni782.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtifactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(artifact: ArtifactEntity)

    @Query("SELECT * FROM artifacts WHERE deleted = 0 ORDER BY updatedAt DESC")
    fun observeActive(): Flow<List<ArtifactEntity>>

    @Query("SELECT * FROM artifacts WHERE id = :id")
    suspend fun byId(id: String): ArtifactEntity?

    @Query("UPDATE artifacts SET deleted = 1, updatedAt = :now WHERE id = :id")
    suspend fun softDelete(id: String, now: Long)

    @Query("SELECT COUNT(*) FROM artifacts WHERE deleted = 0")
    suspend fun activeCount(): Int
}

@Dao
interface AuditDao {
    @Insert
    suspend fun insert(entry: AuditEntryEntity)

    @Query("SELECT * FROM audit_log WHERE artifactId = :artifactId ORDER BY timestamp DESC")
    fun observeForArtifact(artifactId: String): Flow<List<AuditEntryEntity>>

    @Query("SELECT COUNT(*) FROM audit_log WHERE artifactId = :artifactId")
    suspend fun countForArtifact(artifactId: String): Int
}
