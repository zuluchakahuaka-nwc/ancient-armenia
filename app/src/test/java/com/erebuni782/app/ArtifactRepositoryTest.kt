package com.erebuni782.app

import com.erebuni782.app.data.ArtifactRepository
import com.erebuni782.app.data.ArtifactUi
import com.erebuni782.app.data.CustodyStatus
import com.erebuni782.app.data.db.ArtifactDao
import com.erebuni782.app.data.db.ArtifactEntity
import com.erebuni782.app.data.db.AuditDao
import com.erebuni782.app.data.db.AuditEntryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeArtifactDao : ArtifactDao {
    val store = MutableStateFlow<Map<String, ArtifactEntity>>(emptyMap())

    override suspend fun upsert(artifact: ArtifactEntity) {
        store.value = store.value + (artifact.id to artifact)
    }

    override fun observeActive(): Flow<List<ArtifactEntity>> =
        store.value.values.let { MutableStateFlow(it.toList()) }

    override suspend fun byId(id: String): ArtifactEntity? = store.value[id]

    override suspend fun softDelete(id: String, now: Long) {
        store.value[id]?.let {
            store.value = store.value + (id to it.copy(deleted = true, updatedAt = now))
        }
    }

    override suspend fun activeCount(): Int = store.value.values.count { !it.deleted }
}

class FakeAuditDao : AuditDao {
    val entries = mutableListOf<AuditEntryEntity>()
    private val flow = MutableStateFlow<List<AuditEntryEntity>>(emptyList())

    override suspend fun insert(entry: AuditEntryEntity) {
        entries += entry
        flow.value = entries.toList()
    }

    override fun observeForArtifact(artifactId: String): Flow<List<AuditEntryEntity>> =
        flow.map { list -> list.filter { it.artifactId == artifactId } }

    override suspend fun countForArtifact(artifactId: String): Int =
        entries.count { it.artifactId == artifactId }
}

class ArtifactRepositoryTest {

    private lateinit var artifactDao: FakeArtifactDao
    private lateinit var auditDao: FakeAuditDao
    private lateinit var repo: ArtifactRepository
    private val clock = { 1_700_000_000_000L }

    @Before
    fun setUp() {
        artifactDao = FakeArtifactDao()
        auditDao = FakeAuditDao()
        repo = ArtifactRepository(artifactDao, auditDao, clock)
    }

    private fun sample() = ArtifactUi(
        id = "", category = com.erebuni782.app.data.ArtifactCategory.STONE_BLOCK,
        title = "Блок", description = "", latitude = null, longitude = null,
        address = "ул. Ленина 1", discoveryEpochDay = null, custodian = "",
        custodyStatus = CustodyStatus.IN_SITU, nextInspectionEpochDay = null,
        photoPaths = emptyList(), createdAt = 0, updatedAt = 0
    )

    @Test
    fun `create writes audit CREATED`() = runTest {
        val id = repo.create(sample())
        assertEquals("CREATED", auditDao.entries.single().action)
        assertEquals(id, auditDao.entries.single().artifactId)
        assertEquals("Блок", artifactDao.byId(id)?.title)
    }

    @Test
    fun `status only forward and audited`() = runTest {
        val id = repo.create(sample())
        repo.setStatus(id, CustodyStatus.AGREED_FOR_TRANSFER)
        assertEquals(
            CustodyStatus.AGREED_FOR_TRANSFER,
            CustodyStatus.fromRaw(artifactDao.byId(id)?.custodyStatus)
        )
        val statusAudits = auditDao.entries.filter { it.action == "STATUS_CHANGED" }
        assertEquals(1, statusAudits.size)
        assertEquals("IN_SITU -> AGREED_FOR_TRANSFER", statusAudits.single().details)

        val thrown = runCatching { repo.setStatus(id, CustodyStatus.IN_SITU) }
        assertTrue(thrown.isFailure)
    }

    @Test
    fun `photos append with audit`() = runTest {
        val id = repo.create(sample())
        repo.addPhoto(id, "/p/1.jpg")
        repo.addPhoto(id, "/p/2.jpg")
        assertEquals("/p/1.jpg\n/p/2.jpg", artifactDao.byId(id)?.photoPaths)
        assertEquals(2, auditDao.entries.count { it.action == "PHOTO_ADDED" })
    }

    @Test
    fun `soft delete hides from active`() = runTest {
        val id = repo.create(sample())
        repo.softDelete(id)
        assertTrue(artifactDao.byId(id)?.deleted == true)
        assertEquals(0, artifactDao.activeCount())
        assertEquals("DELETED", auditDao.entries.last().action)
    }
}
