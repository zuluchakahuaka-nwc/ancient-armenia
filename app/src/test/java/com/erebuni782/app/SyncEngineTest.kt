package com.erebuni782.app

import com.erebuni782.app.sync.MergeResult
import com.erebuni782.app.sync.SyncConflict
import com.erebuni782.app.sync.SyncEngine
import com.erebuni782.app.sync.SyncRecord
import com.erebuni782.app.sync.VersionVector
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VersionVectorTest {

    @Test
    fun `domination and concurrency`() {
        val a = mapOf("d1" to 2L)
        val b = mapOf("d1" to 1L)
        val c = mapOf("d2" to 1L)
        assertTrue(VersionVector.dominates(a, b))
        assertTrue(!VersionVector.dominates(b, a))
        assertTrue(VersionVector.concurrent(a, c))
        assertTrue(VersionVector.concurrent(c, a))
    }

    @Test
    fun `merge takes componentwise max`() {
        val m = VersionVector.merge(mapOf("d1" to 3L, "d2" to 1L), mapOf("d2" to 5L))
        assertEquals(mapOf("d1" to 3L, "d2" to 5L), m)
    }

    @Test
    fun `increment bumps own counter`() {
        val vv = VersionVector.increment(mapOf("d1" to 1L), "d2")
        assertEquals(mapOf("d1" to 1L, "d2" to 1L), vv)
    }
}

class SyncEngineTest {

    private fun rec(id: String, vv: Map<String, Long>, editor: String = "d1", deleted: Boolean = false) =
        SyncRecord(id, """{"id":"$id"}""", vv, editor, deleted)

    @Test
    fun `remote new record accepted`() {
        val r = mergeResult(local = emptyList(), remote = listOf(rec("a", mapOf("d2" to 1L), "d2")))
        assertEquals(1, r.acceptedRemote)
        assertEquals(0, r.conflicts.size)
        assertTrue(r.merged.any { it.id == "a" })
    }

    @Test
    fun `local dominates keeps local`() {
        val local = listOf(rec("a", mapOf("d1" to 5L)))
        val remote = listOf(rec("a", mapOf("d1" to 3L), "d2"))
        val r = mergeResult(local, remote)
        assertEquals(1, r.keptLocal)
        assertEquals("d1", r.merged.first { it.id == "a" }.lastEditor)
    }

    @Test
    fun `remote dominates replaces`() {
        val local = listOf(rec("a", mapOf("d1" to 1L)))
        val remote = listOf(rec("a", mapOf("d1" to 1L, "d2" to 1L), "d2"))
        val r = mergeResult(local, remote)
        assertEquals(1, r.acceptedRemote)
        assertEquals("d2", r.merged.first().lastEditor)
    }

    @Test
    fun `concurrent edits without winner go to conflict list`() {
        val local = listOf(rec("a", mapOf("d1" to 2L), "d1"))
        val remote = listOf(rec("a", mapOf("d2" to 2L), "d2"))
        val r = mergeResult(local, remote)
        // параллельная правка чужого устройства → конфликт, локальная сохранена
        assertEquals(1, r.conflicts.size)
        assertEquals("a", r.conflicts.first().id)
        assertTrue(r.merged.any { it.id == "a" && it.lastEditor == "d1" })
    }

    @Test
    fun `tombstones sync as records`() {
        val remote = listOf(rec("gone", mapOf("d2" to 4L), "d2", deleted = true))
        val r = mergeResult(emptyList(), remote)
        assertTrue(r.merged.first { it.id == "gone" }.deleted)
    }

    private fun mergeResult(local: List<SyncRecord>, remote: List<SyncRecord>): MergeResult =
        SyncEngine.merge(local, remote, myDeviceId = "d1")
}
