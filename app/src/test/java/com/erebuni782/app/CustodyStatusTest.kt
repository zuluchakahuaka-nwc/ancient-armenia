package com.erebuni782.app

import com.erebuni782.app.data.ArtifactCategory
import com.erebuni782.app.data.CustodyStatus
import com.erebuni782.app.data.PinHasher
import com.erebuni782.app.work.ReminderScheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CustodyStatusTest {

    @Test
    fun `linear order and next`() {
        assertEquals(CustodyStatus.AGREED_FOR_TRANSFER, CustodyStatus.IN_SITU.next())
        assertEquals(CustodyStatus.RESEARCHED, CustodyStatus.HANDED_TO_MUSEUM.next())
        assertNull(CustodyStatus.RESEARCHED.next())
    }

    @Test
    fun `fromRaw fallback and parse`() {
        assertEquals(CustodyStatus.IN_TRANSIT, CustodyStatus.fromRaw("IN_TRANSIT"))
        assertEquals(CustodyStatus.IN_SITU, CustodyStatus.fromRaw("garbage"))
        assertEquals(CustodyStatus.IN_SITU, CustodyStatus.fromRaw(null))
        assertEquals(ArtifactCategory.POTTERY, ArtifactCategory.fromRaw("POTTERY"))
        assertEquals(ArtifactCategory.OTHER, ArtifactCategory.fromRaw("??"))
    }

    @Test
    fun `transitions only forward`() {
        assertTrue(CustodyStatus.canTransition(CustodyStatus.IN_SITU, CustodyStatus.RESEARCHED))
        assertFalse(CustodyStatus.canTransition(CustodyStatus.RESEARCHED, CustodyStatus.IN_SITU))
        assertFalse(CustodyStatus.canTransition(CustodyStatus.IN_TRANSIT, CustodyStatus.IN_TRANSIT))
    }
}

class PinHasherTest {

    @Test
    fun `format validation`() {
        assertTrue(PinHasher.isValidPinFormat("1234"))
        assertTrue(PinHasher.isValidPinFormat("12345678"))
        assertFalse(PinHasher.isValidPinFormat("123"))
        assertFalse(PinHasher.isValidPinFormat("123456789"))
        assertFalse(PinHasher.isValidPinFormat("12a4"))
        assertFalse(PinHasher.isValidPinFormat(""))
    }

    @Test
    fun `hash is deterministic and salted`() {
        val a = PinHasher.hash("1234", "salt")
        val b = PinHasher.hash("1234", "salt")
        val c = PinHasher.hash("1234", "other")
        assertEquals(a, b)
        assertTrue(a != c)
        assertEquals(64, a.length)
    }
}

class ReminderSchedulerTest {

    @Test
    fun `delay computation`() {
        val day = 24L * 60 * 60 * 1000
        val now = 100L * day
        assertEquals(0L, ReminderScheduler.computeDelayMillis(now, 50L))          // прошло → сразу
        assertEquals(0L, ReminderScheduler.computeDelayMillis(now, 100L))         // сегодня → сразу
        assertEquals(5L * day, ReminderScheduler.computeDelayMillis(now, 105L))   // через 5 дней
    }
}
