package com.erebuni782.app.sync

/**
 * Version vector: deviceId -> counter. Доминирование и слияние — чистые,
 * JVM-тестируются (спека §1: last-write-wins + version vectors + конфликт-лист).
 */
object VersionVector {

    fun dominates(a: Map<String, Long>, b: Map<String, Long>): Boolean {
        val keys = a.keys + b.keys
        return keys.all { (a[it] ?: 0L) >= (b[it] ?: 0L) } &&
            keys.any { (a[it] ?: 0L) > (b[it] ?: 0L) }
    }

    fun concurrent(a: Map<String, Long>, b: Map<String, Long>): Boolean =
        !dominates(a, b) && !dominates(b, a)

    fun merge(a: Map<String, Long>, b: Map<String, Long>): Map<String, Long> {
        val out = a.toMutableMap()
        b.forEach { (k, v) -> out[k] = maxOf(out[k] ?: 0L, v) }
        return out
    }

    fun increment(vv: Map<String, Long>, deviceId: String): Map<String, Long> =
        vv.toMutableMap().also { it[deviceId] = (it[deviceId] ?: 0L) + 1L }
}
