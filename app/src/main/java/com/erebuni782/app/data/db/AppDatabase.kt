package com.erebuni782.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.erebuni782.app.data.seed.BOOK_SEED
import com.erebuni782.app.data.seed.GUIDE_SEED
import com.erebuni782.app.data.seed.WIKI_SEED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [
        WikiArticleEntity::class,
        BookEntity::class,
        ChapterEntity::class,
        AudioTrackEntity::class,
        com.erebuni782.app.data.db.ArtifactEntity::class,
        com.erebuni782.app.data.db.AuditEntryEntity::class,
        com.erebuni782.app.data.db.AerialSessionEntity::class,
        com.erebuni782.app.data.db.AerialMarkerEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wikiDao(): WikiDao
    abstract fun bookDao(): BookDao
    abstract fun audioTrackDao(): AudioTrackDao
    abstract fun artifactDao(): com.erebuni782.app.data.db.ArtifactDao
    abstract fun auditDao(): com.erebuni782.app.data.db.AuditDao
    abstract fun aerialSessionDao(): com.erebuni782.app.data.db.AerialSessionDao
    abstract fun aerialMarkerDao(): com.erebuni782.app.data.db.AerialMarkerDao

    companion object {
        /** 4→5: колонка section у вики-статей; НЕ деструктивно — данные сотрудника живы. */
        private val MIGRATION_4_5 = object : androidx.room.migration.Migration(4, 5) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE wiki_articles ADD COLUMN section TEXT NOT NULL DEFAULT 'urartu'")
            }
        }

        fun build(context: Context): AppDatabase {
            val db = Room.databaseBuilder(context, AppDatabase::class.java, "erebuni.db")
                .addMigrations(MIGRATION_4_5)
                .fallbackToDestructiveMigration()
                .build()
            seed(db)
            return db
        }

        private fun seed(db: AppDatabase) {
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            scope.launch {
                // Wiki — справочный сид-контент: upsert каждый запуск (id = PK, REPLACE),
                // чтобы расширение статей доезжало и на существующие установки
                db.wikiDao().insertAll(
                    (WIKI_SEED + com.erebuni782.app.data.seed.ANCIENT_WIKI_SEED).map {
                        com.erebuni782.app.data.db.WikiArticleEntity(
                            id = it.id, category = it.category, sortOrder = it.sortOrder,
                            section = it.section,
                            titleEn = it.titleEn, titleRu = it.titleRu, titleHy = it.titleHy,
                            bodyEn = it.bodyEn, bodyRu = it.bodyRu, bodyHy = it.bodyHy
                        )
                    }
                )
                if (db.bookDao().bookById(BOOK_SEED.id) == null) {
                    db.bookDao().insertBooks(
                        listOf(
                            BookEntity(
                                id = BOOK_SEED.id,
                                titleEn = BOOK_SEED.titleEn, titleRu = BOOK_SEED.titleRu, titleHy = BOOK_SEED.titleHy,
                                authorEn = BOOK_SEED.authorEn, authorRu = BOOK_SEED.authorRu, authorHy = BOOK_SEED.authorHy,
                                licenseNote = BOOK_SEED.licenseNote
                            )
                        )
                    )
                    db.bookDao().insertChapters(
                        BOOK_SEED.chapters.map {
                            ChapterEntity(
                                bookId = BOOK_SEED.id, chapterIndex = it.index,
                                titleEn = it.titleEn, titleRu = it.titleRu, titleHy = it.titleHy,
                                bodyEn = it.bodyEn, bodyRu = it.bodyRu, bodyHy = it.bodyHy
                            )
                        }
                    )
                }
                if (db.audioTrackDao().guideCount() == 0) {
                    db.audioTrackDao().insertAll(GUIDE_SEED)
                }
            }
        }
    }
}
