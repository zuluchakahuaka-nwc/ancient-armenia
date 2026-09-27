package com.erebuni782.app.photos

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.util.UUID

/**
 * Фото артефактов: копия в приватное хранилище приложения.
 * D4: настраиваемо — при stripExif перекодируем битмапом (EXIF исчезает),
 * иначе копируем файл как есть (координаты-доказательства сохраняются).
 */
object PhotoStore {

    fun importPhoto(context: Context, uri: Uri, artifactId: String, keepExif: Boolean): String? {
        val dir = File(context.filesDir, "artifacts/$artifactId").apply { mkdirs() }
        val target = File(dir, "${UUID.randomUUID()}.jpg")
        return try {
            if (keepExif) {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    target.outputStream().use { input.copyTo(it) }
                }
            } else {
                val bitmap: Bitmap = context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it)
                } ?: return null
                target.outputStream().use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                }
                bitmap.recycle()
            }
            target.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
