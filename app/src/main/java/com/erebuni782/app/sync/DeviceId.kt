package com.erebuni782.app.sync

import android.content.Context

/** Стабильный идентификатор устройства (для version vectors). */
object DeviceId {
    fun get(context: Context): String {
        val prefs = context.getSharedPreferences("device", Context.MODE_PRIVATE)
        return prefs.getString("id", null) ?: java.util.UUID.randomUUID().toString().also {
            prefs.edit().putString("id", it).apply()
        }
    }
}
