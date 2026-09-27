package com.erebuni782.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WikiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<WikiArticleEntity>)

    @Query("SELECT * FROM wiki_articles ORDER BY sortOrder")
    fun observeAll(): Flow<List<WikiArticleEntity>>

    @Query("SELECT * FROM wiki_articles ORDER BY sortOrder")
    suspend fun allOnce(): List<WikiArticleEntity>

    @Query("SELECT * FROM wiki_articles WHERE id = :id")
    suspend fun byId(id: String): WikiArticleEntity?

    @Query("SELECT COUNT(*) FROM wiki_articles")
    suspend fun count(): Int
}

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Query("SELECT * FROM books")
    fun observeBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun bookById(id: String): BookEntity?

    @Query("SELECT * FROM chapters WHERE bookId = :bookId ORDER BY chapterIndex")
    suspend fun chaptersOf(bookId: String): List<ChapterEntity>
}

@Dao
interface AudioTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tracks: List<AudioTrackEntity>)

    @Query("SELECT * FROM audio_tracks WHERE kind = :kind ORDER BY id")
    fun observeByKind(kind: String): Flow<List<AudioTrackEntity>>

    @Query("DELETE FROM audio_tracks WHERE id = :id AND kind = 'USER'")
    suspend fun deleteUserTrack(id: Long)

    @Query("SELECT COUNT(*) FROM audio_tracks WHERE kind = 'GUIDE'")
    suspend fun guideCount(): Int
}
