package com.erebuni782.app.sync

/**
 * Запись синка: произвольный payload (JSON артефакта/сессии) + вектор
 * версий + флаг удаления (tombstone). Лампорт = max(vv) записи.
 */
data class SyncRecord(
    val id: String,
    val payloadJson: String,
    val vv: Map<String, Long>,
    val lastEditor: String,
    val deleted: Boolean
) {
    val lamport: Long get() = vv.values.maxOrNull() ?: 0L
}

data class SyncConflict(val id: String, val localLamport: Long, val remoteLamport: Long)

data class MergeResult(
    val merged: List<SyncRecord>,
    val conflicts: List<SyncConflict>,
    val acceptedRemote: Int,
    val keptLocal: Int
)

/**
 * Движок слияния: доминирование векторов; при равенстве/конфликте —
 * last-write-wins по (lamport, deviceId); параллельные версии без
 * победителя → конфликт-лист, локальная версия сохраняется.
 */
object SyncEngine {

    fun merge(local: List<SyncRecord>, remote: List<SyncRecord>, myDeviceId: String): MergeResult {
        val byId = local.associateBy { it.id }.toMutableMap<String, SyncRecord>()
        val conflicts = mutableListOf<SyncConflict>()
        var accepted = 0
        var kept = 0

        remote.forEach { r ->
            val l = byId[r.id]
            if (l == null) {
                byId[r.id] = r
                accepted++
                return@forEach
            }
            val lvDom = VersionVector.dominates(l.vv, r.vv)
            val rvDom = VersionVector.dominates(r.vv, l.vv)
            when {
                lvDom -> kept++
                rvDom -> {
                    byId[r.id] = r
                    accepted++
                }
                else -> {
                    // параллельные правки: детерминированный тай-брейк LWW
                    val tieLocal = compareOf(l) >= compareOf(r)
                    if (tieLocal && l.lastEditor == myDeviceId || !tieLocal && r.lastEditor != myDeviceId) {
                        // локальная побеждает только при явном приоритете; иначе конфли́кт
                        conflicts += SyncConflict(r.id, l.lamport, r.lamport)
                        kept++
                    } else {
                        byId[r.id] = r
                        accepted++
                    }
                }
            }
        }
        return MergeResult(byId.values.toList(), conflicts, accepted, kept)
    }

    /** Детерминированный порядок: (лампорт, редактор) — меньше = старше. */
    private fun compareOf(r: SyncRecord): Long = r.lamport * 1_000_003L + r.lastEditor.hashCode().toLong()
}
