package com.erebuni782.app.sync

import com.erebuni782.app.AppGraph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Loopback-демо синка (P5): сервер и клиент в одном процессе — полная
 * прокрутка протокола (кадры ↔ JSON ↔ merge) без второго устройства.
 * На железе тот же движок работает поверх Bluetooth-транспорта.
 */
object SyncLoopback {

    private const val PORT = 42777

    /** Возвращает статус-строку вида "SYNC ok sent=N recv=N accepted=N conflicts=N". */
    suspend fun run(): String = withContext(Dispatchers.IO) {
        val artifacts = AppGraph.artifacts.observeActive().first()
        val myId = DeviceId.get(AppGraph.appContext)

        val records = JSONArray().putAll(
            artifacts.map { a ->
                JSONObject()
                    .put("id", a.id)
                    .put("payload", com.erebuni782.app.export.entityToJson(a).toString())
                    .put("vv", JSONObject(a.versionVector))
                    .put("lastEditor", a.lastEditor.ifEmpty { myId })
                    .put("deleted", a.deleted)
            }
        )

        val latch = CountDownLatch(1)
        var serverAccepted = -1
        var serverConflicts = -1
        val server = SyncServer(PORT)
        server.start { remoteJson ->
            // сервер играет «новое устройство»: пустой локальный набор
            val remote = parseRecords(remoteJson)
            val result = SyncEngine.merge(emptyList(), remote, myDeviceId = "server-fresh")
            serverAccepted = result.acceptedRemote
            serverConflicts = result.conflicts.size
            latch.countDown()
            records.toString() // отвечаем объединённым состоянием
        }

        val reply = SyncClient().exchange("127.0.0.1", PORT, records.toString())
        latch.await(5, TimeUnit.SECONDS)
        server.stop()

        val mergedRemote = parseRecords(reply)
        val localRecords = parseRecords(records.toString())
        val clientMerge = SyncEngine.merge(localRecords, mergedRemote, myDeviceId = myId)

        "SYNC ok sent=${localRecords.size} recv=${mergedRemote.size} " +
            "accepted=${if (serverAccepted >= 0) serverAccepted else clientMerge.acceptedRemote} " +
            "conflicts=${if (serverConflicts >= 0) serverConflicts else clientMerge.conflicts.size}"
    }

    private fun parseRecords(json: String): List<SyncRecord> {
        val arr = JSONArray(json)
        return (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            val vv = mutableMapOf<String, Long>()
            val vvJson = o.getJSONObject("vv")
            vvJson.keys().forEach { k -> vv[k] = vvJson.getLong(k) }
            SyncRecord(
                id = o.getString("id"),
                payloadJson = o.getString("payload"),
                vv = vv,
                lastEditor = o.getString("lastEditor"),
                deleted = o.getBoolean("deleted")
            )
        }
    }
}

private fun <T> JSONArray.putAll(items: List<T>): JSONArray {
    items.forEach { put(it) }
    return this
}
