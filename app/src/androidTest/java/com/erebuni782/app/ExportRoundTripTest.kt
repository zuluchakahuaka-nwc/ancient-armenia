package com.erebuni782.app

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.erebuni782.app.data.ArtifactRepository
import com.erebuni782.app.data.ArtifactUi
import com.erebuni782.app.data.CustodyStatus
import com.erebuni782.app.data.db.AppDatabase
import com.erebuni782.app.export.ExportRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Раунд-трип подписанного дампа (D2): база-экспортёр → .e782 → база-импортёр.
 * Включая отказ при неверной парольной фразе.
 */
@RunWith(AndroidJUnit4::class)
class ExportRoundTripTest {

    private lateinit var context: Context
    private lateinit var dbA: AppDatabase
    private lateinit var dbB: AppDatabase
    private lateinit var repoA: ArtifactRepository
    private lateinit var exporter: ExportRepository

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        dbA = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries().build()
        dbB = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries().build()
        repoA = ArtifactRepository(dbA.artifactDao(), dbA.auditDao()) { "device-A" }
        exporter = ExportRepository(context, dbA.artifactDao(), dbA.auditDao(), dbA.aerialSessionDao(), dbA.aerialMarkerDao())
    }

    @After
    fun tearDown() {
        dbA.close()
        dbB.close()
    }

    private fun sample() = ArtifactUi(
        id = "", category = com.erebuni782.app.data.ArtifactCategory.STONE_BLOCK,
        title = "RoundTrip Stone", description = "тест", latitude = 40.17, longitude = 44.51,
        address = "Arin-Berd", discoveryEpochDay = 20000L, custodian = "Хранитель",
        custodyStatus = CustodyStatus.IN_SITU, nextInspectionEpochDay = 21000L,
        photoPaths = emptyList(), createdAt = 0, updatedAt = 0
    )

    @Test
    fun exportImportRoundTrip_preservesDataAndSignature() = runBlocking<Unit> {
        val id = repoA.create(sample())
        repoA.setStatus(id, CustodyStatus.AGREED_FOR_TRANSFER)

        val file = File(context.cacheDir, "rt_${System.currentTimeMillis()}.e782")
        val exp = exporter.exportTo(file, "secret-pass")
        assertEquals(1, exp.artifacts)

        // импорт в ДРУГУЮ базу с верной фразой
        val importer = ExportRepository(context, dbB.artifactDao(), dbB.auditDao(), dbB.aerialSessionDao(), dbB.aerialMarkerDao())
        val imp = importer.importFrom(file, "secret-pass")
        assertTrue(imp.verified)
        assertEquals(1, imp.artifacts)

        val restored = dbB.artifactDao().byId(id)!!
        assertEquals("RoundTrip Stone", restored.title)
        assertEquals("AGREED_FOR_TRANSFER", restored.custodyStatus)
        assertEquals(40.17, restored.latitude!!, 0.0001)
        assertEquals("device-A", restored.lastEditor)
        assertTrue(restored.versionVector.contains("device-A"))
        // аудит приехал целиком
        assertEquals(2, dbB.auditDao().countForArtifact(id))
        file.delete()
    }

    @Test
    fun wrongPassphrase_failsSignatureCheck() = runBlocking<Unit> {
        repoA.create(sample())
        val file = File(context.cacheDir, "rt_bad_${System.currentTimeMillis()}.e782")
        exporter.exportTo(file, "right")
        val importer = ExportRepository(context, dbB.artifactDao(), dbB.auditDao(), dbB.aerialSessionDao(), dbB.aerialMarkerDao())
        val result = runCatching { importer.importFrom(file, "wrong") }
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("ПОДПИСЬ") == true)
        assertEquals(0, dbB.artifactDao().activeCount())
        file.delete()
    }
}
