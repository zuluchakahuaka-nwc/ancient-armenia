package com.erebuni782.app

import android.app.Application
import com.erebuni782.app.audio.PlayerManager
import com.erebuni782.app.data.AudioRepository
import com.erebuni782.app.data.BookRepository
import com.erebuni782.app.data.SettingsRepository
import com.erebuni782.app.data.WikiRepository
import com.erebuni782.app.data.db.AppDatabase

/** Простой сервис-локатор (P2): без DI-фреймворков, явный граф. */
object AppGraph {
    lateinit var appContext: android.content.Context
        private set
    lateinit var settings: SettingsRepository
        private set
    lateinit var wiki: WikiRepository
        private set
    lateinit var books: BookRepository
        private set
    lateinit var audio: AudioRepository
        private set
    lateinit var player: PlayerManager
        private set
    lateinit var artifacts: com.erebuni782.app.data.ArtifactRepository
        private set
    lateinit var pin: com.erebuni782.app.data.PinRepository
        private set
    lateinit var aerial: com.erebuni782.app.data.AerialRepository
        private set

    fun init(app: Application) {
        appContext = app
        val db = AppDatabase.build(app)
        settings = SettingsRepository(app)
        wiki = WikiRepository(db.wikiDao())
        books = BookRepository(db.bookDao())
        audio = AudioRepository(db.audioTrackDao())
        player = PlayerManager(app)
        artifacts = com.erebuni782.app.data.ArtifactRepository(db.artifactDao(), db.auditDao())
        pin = com.erebuni782.app.data.PinRepository(app)
        aerial = com.erebuni782.app.data.AerialRepository(app, db.aerialSessionDao(), db.aerialMarkerDao())
    }
}

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppGraph.init(this)
    }
}
