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

    fun init(app: Application) {
        val db = AppDatabase.build(app)
        settings = SettingsRepository(app)
        wiki = WikiRepository(db.wikiDao())
        books = BookRepository(db.bookDao())
        audio = AudioRepository(db.audioTrackDao())
        player = PlayerManager(app)
    }
}

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppGraph.init(this)
    }
}
