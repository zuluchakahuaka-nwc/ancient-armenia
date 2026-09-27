package com.erebuni782.app.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest
import java.security.SecureRandom

/** Чистый хэшер — тестируется на JVM без Android. */
object PinHasher {
    fun hash(pin: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest((salt + ":" + pin).toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun newSalt(): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return salt.joinToString("") { "%02x".format(it) }
    }

    fun isValidPinFormat(pin: String): Boolean =
        pin.length in 4..8 && pin.all { it.isDigit() }
}

/**
 * D3: один общий PIN, EncryptedSharedPreferences, смена через настройки.
 * Хранится ТОЛЬКО солёный SHA-256 — не plaintext.
 */
class PinRepository(context: Context) {

    private val prefs = EncryptedSharedPreferences.create(
        context.applicationContext,
        "pin_store",
        MasterKey.Builder(context.applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun hasPin(): Boolean = prefs.contains(KEY_HASH)

    fun setPin(pin: String) {
        val salt = PinHasher.newSalt()
        prefs.edit()
            .putString(KEY_SALT, salt)
            .putString(KEY_HASH, PinHasher.hash(pin, salt))
            .apply()
    }

    fun verify(pin: String): Boolean {
        val salt = prefs.getString(KEY_SALT, null) ?: return false
        val stored = prefs.getString(KEY_HASH, null) ?: return false
        return PinHasher.hash(pin, salt) == stored
    }

    fun change(current: String, newPin: String): Boolean {
        if (!verify(current)) return false
        setPin(newPin)
        return true
    }

    companion object {
        private const val KEY_SALT = "pin_salt"
        private const val KEY_HASH = "pin_hash"
    }
}

/** Сессионный флаг разблокировки (живёт в рамках процесса). */
object EmployeeSession {
    @Volatile
    var unlocked: Boolean = false
}
